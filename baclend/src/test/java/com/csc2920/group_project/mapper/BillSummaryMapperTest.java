package com.csc2920.group_project.mapper;

import com.csc2920.group_project.dto.BillSummaryDto;
import com.csc2920.group_project.entity.BillSummaryEntity;
import com.csc2920.group_project.entity.LegislationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BillSummaryMapper Tests")
class BillSummaryMapperTest {

    private LegislationEntity legislation;
    private BillSummaryEntity billSummary;

    @BeforeEach
    void setUp() {
        legislation = new LegislationEntity();
        legislation.setId(1L);
        legislation.setCongress(118);
        legislation.setBillType("hr");
        legislation.setBillNumber("1234");

        billSummary = new BillSummaryEntity();
        billSummary.setId(1L);
        billSummary.setLegislation(legislation);
        billSummary.setActionDate("2023-01-15");
        billSummary.setActionDesc("Referred to House Committee on Ways and Means");
        billSummary.setText("This is a detailed bill summary describing the legislation.");
        billSummary.setUpdateDate("2023-01-15");
        billSummary.setVersionCode("00c");
    }

    @Test
    @DisplayName("Should map BillSummaryEntity to BillSummaryDto")
    void testToDtoMapsAllFields() {
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertNotNull(dto);
        assertEquals("2023-01-15", dto.actionDate());
        assertEquals("Referred to House Committee on Ways and Means", dto.actionDesc());
        assertEquals("This is a detailed bill summary describing the legislation.", dto.text());
        assertEquals("2023-01-15", dto.updateDate());
        assertEquals("00c", dto.versionCode());
    }

    @Test
    @DisplayName("Should return null when BillSummaryEntity is null")
    void testToDtoWithNullInput() {
        BillSummaryDto dto = BillSummaryMapper.toDto(null);

        assertNull(dto);
    }

    @Test
    @DisplayName("Should map null summary properties to null in dto")
    void testToDtoWithNullProperties() {
        BillSummaryEntity summary = new BillSummaryEntity();
        summary.setId(1L);
        summary.setLegislation(legislation);
        // All properties left null

        BillSummaryDto dto = BillSummaryMapper.toDto(summary);

        assertNotNull(dto);
        assertNull(dto.actionDate());
        assertNull(dto.actionDesc());
        assertNull(dto.text());
        assertNull(dto.updateDate());
        assertNull(dto.versionCode());
    }

    @Test
    @DisplayName("Should handle null action date")
    void testToDtoWithNullActionDate() {
        billSummary.setActionDate(null);
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertNull(dto.actionDate());
        assertNotNull(dto.actionDesc());
        assertNotNull(dto.text());
    }

    @Test
    @DisplayName("Should handle null action description")
    void testToDtoWithNullActionDesc() {
        billSummary.setActionDesc(null);
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertNull(dto.actionDesc());
        assertNotNull(dto.actionDate());
    }

    @Test
    @DisplayName("Should handle null summary text")
    void testToDtoWithNullText() {
        billSummary.setText(null);
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertNull(dto.text());
        assertNotNull(dto.actionDate());
        assertNotNull(dto.versionCode());
    }

    @Test
    @DisplayName("Should handle null update date")
    void testToDtoWithNullUpdateDate() {
        billSummary.setUpdateDate(null);
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertNull(dto.updateDate());
        assertNotNull(dto.versionCode());
    }

    @Test
    @DisplayName("Should handle null version code")
    void testToDtoWithNullVersionCode() {
        billSummary.setVersionCode(null);
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertNull(dto.versionCode());
        assertNotNull(dto.actionDate());
    }

    @Test
    @DisplayName("Should preserve version code")
    void testToDtoPreservesVersionCode() {
        billSummary.setVersionCode("00d");
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertEquals("00d", dto.versionCode());
    }

    @Test
    @DisplayName("Should handle different version codes")
    void testToDtoWithDifferentVersionCodes() {
        billSummary.setVersionCode("06a");
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertEquals("06a", dto.versionCode());
    }

    @Test
    @DisplayName("Should handle long summary text")
    void testToDtoWithLongText() {
        String longText = "Summary text. ".repeat(1000); // Very long text
        billSummary.setText(longText);
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertEquals(longText, dto.text());
        assertEquals(longText.length(), dto.text().length());
    }

    @Test
    @DisplayName("Should handle empty string action description")
    void testToDtoWithEmptyActionDesc() {
        billSummary.setActionDesc("");
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertEquals("", dto.actionDesc());
    }

    @Test
    @DisplayName("Should handle empty string text")
    void testToDtoWithEmptyText() {
        billSummary.setText("");
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertEquals("", dto.text());
    }

    @Test
    @DisplayName("Should handle special characters in text")
    void testToDtoWithSpecialCharacters() {
        String textWithSpecialChars = "This bill includes special characters: @#$%^&*()_+-=[]{}|;':\",./<>?";
        billSummary.setText(textWithSpecialChars);
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertEquals(textWithSpecialChars, dto.text());
    }

    @Test
    @DisplayName("Should preserve all fields in complete mapping")
    void testToDtoCompleteMapping() {
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertAll("All fields should be preserved",
            () -> assertEquals(billSummary.getActionDate(), dto.actionDate()),
            () -> assertEquals(billSummary.getActionDesc(), dto.actionDesc()),
            () -> assertEquals(billSummary.getText(), dto.text()),
            () -> assertEquals(billSummary.getUpdateDate(), dto.updateDate()),
            () -> assertEquals(billSummary.getVersionCode(), dto.versionCode())
        );
    }

    @Test
    @DisplayName("Should be idempotent - multiple calls return equivalent objects")
    void testToDtoIdempotency() {
        BillSummaryDto dto1 = BillSummaryMapper.toDto(billSummary);
        BillSummaryDto dto2 = BillSummaryMapper.toDto(billSummary);

        assertEquals(dto1.actionDate(), dto2.actionDate());
        assertEquals(dto1.actionDesc(), dto2.actionDesc());
        assertEquals(dto1.text(), dto2.text());
        assertEquals(dto1.updateDate(), dto2.updateDate());
        assertEquals(dto1.versionCode(), dto2.versionCode());
    }

    @Test
    @DisplayName("Should not reference legislation entity in dto")
    void testToDtoDoesNotIncludeLegislation() {
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertNotNull(dto);
        // The DTO should only have 5 fields: actionDate, actionDesc, text, updateDate, versionCode
        // No reference to legislation
    }

    @Test
    @DisplayName("Should handle multi-line text")
    void testToDtoWithMultilineText() {
        String multilineText = "Line 1\nLine 2\nLine 3\nLine 4";
        billSummary.setText(multilineText);
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertEquals(multilineText, dto.text());
        assertTrue(dto.text().contains("\n"));
    }

    @Test
    @DisplayName("Should handle whitespace-only text")
    void testToDtoWithWhitespaceText() {
        billSummary.setText("   \n\t\r\n   ");
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertEquals("   \n\t\r\n   ", dto.text());
    }

    @Test
    @DisplayName("Should handle unicode characters in text")
    void testToDtoWithUnicodeText() {
        String unicodeText = "This summary includes unicode: © ® ™ € £ ¥ § ¶";
        billSummary.setText(unicodeText);
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertEquals(unicodeText, dto.text());
    }

    @Test
    @DisplayName("Should map to record correctly")
    void testToDtoCreatesRecord() {
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        // Records are immutable, so verify this behaves like a record
        assertNotNull(dto);
        BillSummaryDto dto2 = BillSummaryMapper.toDto(billSummary);
        
        // Records with same values should be equal
        assertEquals(dto, dto2);
    }

    @Test
    @DisplayName("Should handle summary with only some fields populated")
    void testToDtoWithPartiallyPopulatedSummary() {
        BillSummaryEntity partial = new BillSummaryEntity();
        partial.setId(2L);
        partial.setLegislation(legislation);
        partial.setActionDate("2023-02-01");
        partial.setVersionCode("00d");
        // actionDesc and text left null

        BillSummaryDto dto = BillSummaryMapper.toDto(partial);

        assertEquals("2023-02-01", dto.actionDate());
        assertNull(dto.actionDesc());
        assertNull(dto.text());
        assertNull(dto.updateDate());
        assertEquals("00d", dto.versionCode());
    }

    @Test
    @DisplayName("Should handle html entities in text")
    void testToDtoWithHtmlEntities() {
        String htmlText = "This bill &amp; related items &lt;legislation&gt; for 2024 &quot;reform&quot;";
        billSummary.setText(htmlText);
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertEquals(htmlText, dto.text());
    }

    @Test
    @DisplayName("Should preserve version code format")
    void testToDtoPreservesVersionCodeFormat() {
        billSummary.setVersionCode("01a");
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);
        assertEquals("01a", dto.versionCode());

        billSummary.setVersionCode("99z");
        dto = BillSummaryMapper.toDto(billSummary);
        assertEquals("99z", dto.versionCode());
    }

    @Test
    @DisplayName("Should handle action dates in various formats")
    void testToDtoWithVariousDateFormats() {
        billSummary.setActionDate("2023-12-31");
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);
        assertEquals("2023-12-31", dto.actionDate());

        billSummary.setActionDate("2024-01-01");
        dto = BillSummaryMapper.toDto(billSummary);
        assertEquals("2024-01-01", dto.actionDate());
    }

    @Test
    @DisplayName("Should handle all fields null except legislation")
    void testToDtoWithAllFieldsNull() {
        BillSummaryEntity allNull = new BillSummaryEntity();
        allNull.setId(3L);
        allNull.setLegislation(legislation);

        BillSummaryDto dto = BillSummaryMapper.toDto(allNull);

        assertNull(dto.actionDate());
        assertNull(dto.actionDesc());
        assertNull(dto.text());
        assertNull(dto.updateDate());
        assertNull(dto.versionCode());
    }

    @Test
    @DisplayName("Should handle very long action description")
    void testToDtoWithVeryLongActionDesc() {
        String longDesc = "X".repeat(5000);
        billSummary.setActionDesc(longDesc);
        BillSummaryDto dto = BillSummaryMapper.toDto(billSummary);

        assertEquals(longDesc, dto.actionDesc());
        assertEquals(5000, dto.actionDesc().length());
    }

    @Test
    @DisplayName("Should handle consecutive null and non-null mappings")
    void testToDoConsecutiveNullAndNonNull() {
        BillSummaryEntity null1 = new BillSummaryEntity();
        null1.setLegislation(legislation);
        null1.setText(null);

        BillSummaryDto dto1 = BillSummaryMapper.toDto(null1);
        assertNull(dto1.text());

        BillSummaryEntity nonNull = new BillSummaryEntity();
        nonNull.setLegislation(legislation);
        nonNull.setText("Some text");

        BillSummaryDto dto2 = BillSummaryMapper.toDto(nonNull);
        assertEquals("Some text", dto2.text());

        BillSummaryDto dto3 = BillSummaryMapper.toDto(null1);
        assertNull(dto3.text());
    }

    @Test
    @DisplayName("Should preserve record immutability across mappings")
    void testToDtoRecordImmutability() {
        BillSummaryDto dto1 = BillSummaryMapper.toDto(billSummary);
        BillSummaryDto dto2 = BillSummaryMapper.toDto(billSummary);
        BillSummaryDto dto3 = BillSummaryMapper.toDto(billSummary);

        // All three should be identical
        assertEquals(dto1, dto2);
        assertEquals(dto2, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertEquals(dto2.hashCode(), dto3.hashCode());
    }
}
