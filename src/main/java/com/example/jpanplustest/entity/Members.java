package com.example.jpanplustest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Members {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;    // 팀에 소속된 멤버 이름
    private int age;    // 팀에 소속된 멤버의 나이
    private String address; // 팀에 소속된 멤버의 주소

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teams")
    private Teams teams;

    public void setTeams(Teams teams) {
        this.teams = teams;
    }
}
