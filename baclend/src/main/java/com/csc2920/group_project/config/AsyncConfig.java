package com.csc2920.group_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "legislationExecutor")
    public Executor legislationExecutor() {
        return Executors.newFixedThreadPool(1);
    }

    @Bean(name = "billSummaryExecutor")
    public Executor billSummaryExecutor() {
        return Executors.newFixedThreadPool(4);
    }
}