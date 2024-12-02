package com.example.jpanplustest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Teams {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;    //그룹에 속해 있는 팀이름 ex) 축구 Group - 레알마드리드 Team

    @Builder.Default
    @OneToMany(mappedBy = "teams", fetch = FetchType.LAZY)
    private List<Members> members = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groups")
    private Groups groups;

    public void setGroups(Groups groups) {
        this.groups = groups;
    }

    public void addMember(Members members) {
        members.setTeams(this);
        this.members.add(members);
    }
}
