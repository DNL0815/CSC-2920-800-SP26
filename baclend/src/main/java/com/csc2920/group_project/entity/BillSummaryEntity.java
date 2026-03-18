package com.csc2920.group_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "bill_summaries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"legislation_id", "version_code"})
)
public class BillSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legislation_id", nullable = false)
    @JsonIgnore
    private LegislationEntity legislation;

    @Column(name = "action_date", length = 50)
    private String actionDate;

    @Column(name = "action_desc", length = 500)
    private String actionDesc;

    @Lob
    @Column(name = "text", columnDefinition = "LONGTEXT")
    private String text;

    @Column(name = "update_date", length = 50)
    private String updateDate;

    @Column(name = "version_code", length = 50)
    private String versionCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BillSummaryEntity)) return false;
        BillSummaryEntity that = (BillSummaryEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
