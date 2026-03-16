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
@Table(name = "members")
public class MemberEntity {

    @Id
    @Column(name = "bioguide_id", length = 20)
    private String bioguideId;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "current_member")
    private Boolean currentMember;

    @Column(name = "party_name", length = 50)
    private String partyName;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "district")
    private Integer district;

    @Column(name = "chamber", length = 255)
    private String chamber;

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "update_date", length = 50)
    private String updateDate;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<MemberLegislationEntity> memberLegislation = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberEntity)) return false;
        MemberEntity that = (MemberEntity) o;
        return Objects.equals(bioguideId, that.bioguideId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bioguideId);
    }
}