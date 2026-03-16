package com.csc2920.group_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "legislation",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"congress", "bill_type", "bill_number"}
        )
)
public class LegislationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "congress")
    private Integer congress;

    @Column(name = "bill_type", length = 10)
    private String billType;

    @Column(name = "bill_number", length = 20)
    private String billNumber;

    @Column(name = "introduced_date", length = 50)
    private String introducedDate;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "policy_area", length = 200)
    private String policyArea;

    @Column(name = "latest_action_date", length = 50)
    private String latestActionDate;

    @Column(name = "latest_action_text", columnDefinition = "TEXT")
    private String latestActionText;

    @Column(name = "url", length = 500)
    private String url;

    @OneToMany(
            mappedBy = "legislation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private Set<MemberLegislationEntity> memberLinks = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LegislationEntity)) return false;
        LegislationEntity that = (LegislationEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}