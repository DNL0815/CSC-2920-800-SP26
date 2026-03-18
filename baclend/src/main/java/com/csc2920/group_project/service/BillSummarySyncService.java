package com.csc2920.group_project.service;

import com.csc2920.group_project.entity.BillSummaryEntity;
import com.csc2920.group_project.entity.LegislationEntity;
import com.csc2920.group_project.repository.BillSummaryRepository;
import com.csc2920.group_project.repository.LegislationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class BillSummarySyncService {

    private final RestTemplate restTemplate;
    private final LegislationRepository legislationRepository;
    private final BillSummaryRepository billSummaryRepository;
    private final TransactionTemplate transactionTemplate;
    private final String baseUrl;
    private final String apiKey;

    public BillSummarySyncService(RestTemplate restTemplate,
                                 LegislationRepository legislationRepository,
                                 BillSummaryRepository billSummaryRepository,
                                 PlatformTransactionManager transactionManager,
                                 @Value("${congress.api.base-url}") String baseUrl,
                                 @Value("${congress.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.legislationRepository = legislationRepository;
        this.billSummaryRepository = billSummaryRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    private <T> T safeGet(String url, Class<T> clazz) {
        int retries = 5;
        int delay = 1000;

        for (int i = 0; i < retries; i++) {
            try {
                return restTemplate.getForObject(url, clazz);
            } catch (HttpClientErrorException.TooManyRequests e) {
                delay *= 2;
                try { Thread.sleep(delay); } catch (InterruptedException ignored) {}
            } catch (HttpServerErrorException e) {
                delay *= 2;
                try { Thread.sleep(delay); } catch (InterruptedException ignored) {}
            } catch (ResourceAccessException e) {
                delay *= 2;
                try { Thread.sleep(delay); } catch (InterruptedException ignored) {}
            } catch (Exception e) {
                System.out.println("❌ Bill summary sync unexpected error for URL: " + url + " | " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    @Async("billSummaryExecutor")
    public void syncAllSummariesAsync() {
        List<LegislationEntity> bills = legislationRepository.findAll();
        System.out.println("Starting bill summary sync for " + bills.size() + " bills.");
        for (LegislationEntity bill : bills) {
            try {
                transactionTemplate.execute(status -> {
                    syncSummariesForBill(bill);
                    return null;
                });
            } catch (Exception e) {
                System.out.println("Error syncing summaries for bill id=" + bill.getId() + ": " + e.getMessage());
            }
        }
        System.out.println("Bill summary sync complete.");
    }

    @Async("billSummaryExecutor")
    public void syncOneAsync(Integer congress, String billType, String billNumber) {
        legislationRepository.findByCongressAndBillTypeIgnoreCaseAndBillNumber(congress, billType, billNumber)
                .ifPresentOrElse(
                        bill -> transactionTemplate.execute(status -> {
                            syncSummariesForBill(bill);
                            return null;
                        }),
                        () -> System.out.println("Bill not found in DB: " + congress + " " + billType + " " + billNumber)
                );
    }

    public void syncSummariesForBill(LegislationEntity bill) {
        if (bill.getCongress() == null || bill.getBillType() == null || bill.getBillNumber() == null) {
            return;
        }

        String normalizedType = bill.getBillType().toLowerCase(Locale.ROOT);

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/bill/" + bill.getCongress() + "/" + normalizedType + "/" + bill.getBillNumber() + "/summaries")
                .queryParam("api_key", apiKey)
                .queryParam("format", "json")
                .queryParam("limit", 250)
                .queryParam("offset", 0)
                .toUriString();

        Object response = safeGet(url, Object.class);
        if (response == null) {
            return;
        }

        List<Map<String, Object>> summaries = extractSummaryList(response);

        billSummaryRepository.deleteByLegislation_Id(bill.getId());

        if (summaries.isEmpty()) {
            return;
        }

        List<BillSummaryEntity> toSave = new ArrayList<>();
        for (Map<String, Object> s : summaries) {
            BillSummaryEntity e = new BillSummaryEntity();
            e.setLegislation(bill);
            e.setActionDate((String) s.get("actionDate"));
            e.setActionDesc((String) s.get("actionDesc"));
            e.setText((String) s.get("text"));
            e.setUpdateDate((String) s.get("updateDate"));
            Object vc = s.get("versionCode");
            e.setVersionCode(vc != null ? String.valueOf(vc) : null);
            toSave.add(e);
        }

        billSummaryRepository.saveAll(toSave);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractSummaryList(Object response) {
        if (response == null) return Collections.emptyList();

        if (response instanceof List) {
            return (List<Map<String, Object>>) response;
        }

        if (response instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) response;
            Object v = map.get("summaries");
            if (v instanceof List) return (List<Map<String, Object>>) v;
            v = map.get("billSummaries");
            if (v instanceof List) return (List<Map<String, Object>>) v;
            v = map.get("summariesList");
            if (v instanceof List) return (List<Map<String, Object>>) v;
        }

        return Collections.emptyList();
    }
}
