package com.example.jpanplustest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GroupsBatchSize {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;    // 그룹명 ex) 축구, 농구, 야구, 게임 등등 카테고리와 같다.

    @Builder.Default
    @OneToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    private List<TeamsBatchSize> teams = new ArrayList<>();

    public void addTeams(TeamsBatchSize teams) {
        teams.setGroups(this);
        this.teams.add(teams);
    }
}
