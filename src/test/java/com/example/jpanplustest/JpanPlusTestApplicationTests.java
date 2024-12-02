package com.example.jpanplustest;

import com.example.jpanplustest.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
class JpanPlusTestApplicationTests {
    @Autowired
    private Members members;
    @Autowired
    private Teams teams;
    @Autowired
    private Groups groups;
    @Autowired
    private MembersBatchSize membersBatchSize;
    @Autowired
    private TeamsBatchSize teamsBatchSize;
    @Autowired
    private GroupsBatchSize groupsBatchSize;
    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName(value = "1차 캐시 관련 테스트 Not Clear")
    void Test1() {
        //한 트랜잭션에 같은 Entity Instance가 있을경우 조회 쿼리문이 발생하지않음.
        com.example.jpanplustest.entity.Members member =
                com.example.jpanplustest.entity.Members.builder().name("호날두").age(25).address("포르투갈").build();

        com.example.jpanplustest.entity.Teams team =
                com.example.jpanplustest.entity.Teams.builder().name("레알마드리드").build();

        com.example.jpanplustest.entity.Groups group =
                com.example.jpanplustest.entity.Groups.builder().name("축구").build();

        group.addTeams(team);
        team.addMember(member);

        groups.save(group);
        teams.save(team);
        members.save(member);

        List<com.example.jpanplustest.entity.Groups> groupsList = groups.findAll(); // 조회 쿼리문이 발생하지 않는것을 확인할수있음!

        for(com.example.jpanplustest.entity.Groups item : groupsList) {
            System.out.println("Group Name is " + item.getName());

            for(com.example.jpanplustest.entity.Teams teamItem : item.getTeams()) {
                System.out.println("-  Group have team, Team Name is " + teamItem.getName());

                for(com.example.jpanplustest.entity.Members memberItem : teamItem.getMembers()) {
                    System.out.println("-   (MemberInfo) ================================== ");
                    System.out.println("-   Team have Member, Member Name is " + memberItem.getName());
                    System.out.println("-   Team have Member, Member Address is " + memberItem.getAddress());
                    System.out.println("-   Team have Member, Member Age is " + memberItem.getAge());
                    System.out.println("-   =============================================== ");
                }
            }
        }

    }

    @Test
    @Transactional
    @DisplayName(value = "1차 캐시 테스트 Yes Clear")
    void Test2() {
        // Entity Instance를 생성후 저장한다음 Entity Manger(1차 캐시) 공간을 clear하여 조회하였을때 어떠한 결과가 있을까?
        com.example.jpanplustest.entity.Members member =
                com.example.jpanplustest.entity.Members.builder().name("호날두").age(25).address("포르투갈").build();

        com.example.jpanplustest.entity.Teams team =
                com.example.jpanplustest.entity.Teams.builder().name("레알마드리드").build();

        com.example.jpanplustest.entity.Groups group =
                com.example.jpanplustest.entity.Groups.builder().name("축구").build();

        group.addTeams(team);
        team.addMember(member);

        groups.save(group);
        teams.save(team);
        members.save(member);

        entityManager.flush();  // 추가
        entityManager.clear();  // 추가

        List<com.example.jpanplustest.entity.Groups> groupsList = groups.findAll(); // 조회 쿼리문이 발생함!

        for(com.example.jpanplustest.entity.Groups item : groupsList) {
            System.out.println("Group Name is " + item.getName());

            for(com.example.jpanplustest.entity.Teams teamItem : item.getTeams()) {
                System.out.println("-  Group have team, Team Name is " + teamItem.getName());

                for(com.example.jpanplustest.entity.Members memberItem : teamItem.getMembers()) {
                    System.out.println("-   (MemberInfo) ================================== ");
                    System.out.println("-   Team have Member, Member Name is " + memberItem.getName());
                    System.out.println("-   Team have Member, Member Address is " + memberItem.getAddress());
                    System.out.println("-   Team have Member, Member Age is " + memberItem.getAge());
                    System.out.println("-   =============================================== ");
                }
            }
        }

    }

    @Test
    @Transactional
    @DisplayName(value = "N+1문제 발생")
    void Test3() {
        // Entity안에 List형태의 Entity를 DB관점으로 고려하여 코드를 보면, List형태의 객체에 데이터를 채워 넣기위해 어떻게든 select 구문을 통해 DB에서 데이터를 조회해야할 필요성이 있다고 생각함
        com.example.jpanplustest.entity.Members member1 =
                com.example.jpanplustest.entity.Members.builder().name("호날두").age(25).address("포르투갈").build();
        com.example.jpanplustest.entity.Members member2 =
                com.example.jpanplustest.entity.Members.builder().name("메시").age(21).address("아르헨티나").build();
        com.example.jpanplustest.entity.Members member3 =
                com.example.jpanplustest.entity.Members.builder().name("음바페").age(19).address("프랑스").build();
        com.example.jpanplustest.entity.Members member4 =
                com.example.jpanplustest.entity.Members.builder().name("수아레즈").age(23).address("우르과이").build();
        com.example.jpanplustest.entity.Members member5 =
                com.example.jpanplustest.entity.Members.builder().name("박지성").age(25).address("대한민국").build();
        com.example.jpanplustest.entity.Members member6 =
                com.example.jpanplustest.entity.Members.builder().name("베컴").age(22).address("잉글랜드").build();

        com.example.jpanplustest.entity.Teams team1 =
                com.example.jpanplustest.entity.Teams.builder().name("레알마드리드").build();
        com.example.jpanplustest.entity.Teams team2 =
                com.example.jpanplustest.entity.Teams.builder().name("바르셀로나").build();

        com.example.jpanplustest.entity.Groups group =
                com.example.jpanplustest.entity.Groups.builder().name("축구").build();

        group.addTeams(team1);
        group.addTeams(team2);
        team1.addMember(member1);
        team1.addMember(member2);
        team1.addMember(member3);
        team2.addMember(member4);
        team2.addMember(member5);
        team2.addMember(member6);

        groups.save(group);
        teams.save(team1);
        teams.save(team2);
        members.save(member1);
        members.save(member2);
        members.save(member3);
        members.save(member4);
        members.save(member5);
        members.save(member6);

        entityManager.flush();
        entityManager.clear();

        List<com.example.jpanplustest.entity.Groups> groupsList = groups.findAll();

        for(com.example.jpanplustest.entity.Groups item : groupsList) {
            System.out.println("Group Name is " + item.getName());

            for(com.example.jpanplustest.entity.Teams teamItem : item.getTeams()) {
                System.out.println("-  Group have team, Team Name is " + teamItem.getName());

                for(com.example.jpanplustest.entity.Members memberItem : teamItem.getMembers()) {
                    System.out.println("-   (MemberInfo) ================================== ");
                    System.out.println("-   Team have Member, Member Name is " + memberItem.getName());
                    System.out.println("-   Team have Member, Member Address is " + memberItem.getAddress());
                    System.out.println("-   Team have Member, Member Age is " + memberItem.getAge());
                    System.out.println("-   =============================================== ");
                }
            }
        }

    }

    @Test
    @Transactional
    @DisplayName(value = "N+1문제 발생 많은 데이터 삽입 - 2개의 Entity")
    void Test4() {
        // 문제는 데이터가 많아지면 많아질수록 SELECT하는 쿼리문이 많이 발생함... 성능에 악영향을 끼칠수 있다고 판단이됨.
        for (int i = 0; i < 10; i++) {
            com.example.jpanplustest.entity.Members member =
                    com.example.jpanplustest.entity.Members.builder().name("유저"+i).age(i).address("국가"+i).build();
            members.save(member);

            com.example.jpanplustest.entity.Teams team =
                    com.example.jpanplustest.entity.Teams.builder().name("팀"+i).build();
            team.addMember(member);

            teams.save(team);
        }

        entityManager.flush();
        entityManager.clear();

        List<com.example.jpanplustest.entity.Teams> all = teams.findAll();
        for (com.example.jpanplustest.entity.Teams team : all) {
            System.out.println("team = " + team + ", " + "members = " + team.getMembers());
        }
    }

    @Test
    @Transactional
    @DisplayName(value = "N+1문제 발생 많은 데이터 삽입 - 3개의 Entity")
    void Test5() {
        // 당연히 3개의 Entity에 대해서도 발생함.
        com.example.jpanplustest.entity.Members member1 =
                com.example.jpanplustest.entity.Members.builder().name("호날두").age(25).address("포르투갈").build();
        com.example.jpanplustest.entity.Members member2 =
                com.example.jpanplustest.entity.Members.builder().name("메시").age(21).address("아르헨티나").build();
        com.example.jpanplustest.entity.Members member3 =
                com.example.jpanplustest.entity.Members.builder().name("음바페").age(19).address("프랑스").build();
        com.example.jpanplustest.entity.Members member4 =
                com.example.jpanplustest.entity.Members.builder().name("수아레즈").age(23).address("우르과이").build();
        com.example.jpanplustest.entity.Members member5 =
                com.example.jpanplustest.entity.Members.builder().name("박지성").age(25).address("대한민국").build();
        com.example.jpanplustest.entity.Members member6 =
                com.example.jpanplustest.entity.Members.builder().name("베컴").age(22).address("잉글랜드").build();
        com.example.jpanplustest.entity.Members member7 =
                com.example.jpanplustest.entity.Members.builder().name("류현진").age(20).address("대한민국").build();
        com.example.jpanplustest.entity.Members member8 =
                com.example.jpanplustest.entity.Members.builder().name("라이언 와이스").age(21).address("미국").build();
        com.example.jpanplustest.entity.Members member9 =
                com.example.jpanplustest.entity.Members.builder().name("문동주").age(22).address("대한민국").build();
        com.example.jpanplustest.entity.Members member10 =
                com.example.jpanplustest.entity.Members.builder().name("빅터 레이예스").age(20).address("베네수엘라").build();
        com.example.jpanplustest.entity.Members member11 =
                com.example.jpanplustest.entity.Members.builder().name("윤동희").age(21).address("대한민국").build();
        com.example.jpanplustest.entity.Members member12 =
                com.example.jpanplustest.entity.Members.builder().name("김원중").age(22).address("대한민국").build();

        com.example.jpanplustest.entity.Teams team1 =
                com.example.jpanplustest.entity.Teams.builder().name("레알마드리드").build();
        com.example.jpanplustest.entity.Teams team2 =
                com.example.jpanplustest.entity.Teams.builder().name("바르셀로나").build();
        com.example.jpanplustest.entity.Teams team3 =
                com.example.jpanplustest.entity.Teams.builder().name("한화").build();
        com.example.jpanplustest.entity.Teams team4 =
                com.example.jpanplustest.entity.Teams.builder().name("롯데").build();

        com.example.jpanplustest.entity.Groups group1 =
                com.example.jpanplustest.entity.Groups.builder().name("축구").build();
        com.example.jpanplustest.entity.Groups group2 =
                com.example.jpanplustest.entity.Groups.builder().name("야구").build();

        group1.addTeams(team1);
        group1.addTeams(team2);
        group2.addTeams(team3);
        group2.addTeams(team4);
        team1.addMember(member1);
        team1.addMember(member2);
        team1.addMember(member3);
        team2.addMember(member4);
        team2.addMember(member5);
        team2.addMember(member6);
        team3.addMember(member7);
        team3.addMember(member8);
        team3.addMember(member9);
        team4.addMember(member10);
        team4.addMember(member11);
        team4.addMember(member12);

        groups.save(group1);
        groups.save(group2);
        teams.save(team1);
        teams.save(team2);
        teams.save(team3);
        teams.save(team4);
        members.save(member1);
        members.save(member2);
        members.save(member3);
        members.save(member4);
        members.save(member5);
        members.save(member6);
        members.save(member7);
        members.save(member8);
        members.save(member9);
        members.save(member10);
        members.save(member11);
        members.save(member12);

        entityManager.flush();
        entityManager.clear();

        List<com.example.jpanplustest.entity.Groups> groupsList = groups.findAll();

        for(com.example.jpanplustest.entity.Groups item : groupsList) {
            System.out.println("Group Name is " + item.getName());

            for(com.example.jpanplustest.entity.Teams teamItem : item.getTeams()) {
                System.out.println("-  Group have team, Team Name is " + teamItem.getName());

                for(com.example.jpanplustest.entity.Members memberItem : teamItem.getMembers()) {
                    System.out.println("-   (MemberInfo) ================================== ");
                    System.out.println("-   Team have Member, Member Name is " + memberItem.getName());
                    System.out.println("-   Team have Member, Member Address is " + memberItem.getAddress());
                    System.out.println("-   Team have Member, Member Age is " + memberItem.getAge());
                    System.out.println("-   =============================================== ");
                }
            }
        }
    }

    @Test
    @Transactional
    @DisplayName(value = "Batch Size어노테이션")
    void Test6() {
        // Batch Size 어노테이션으로 어느정도 해결은 할수 있지만 그래도 보완이 필요해보임. (IN 쿼리문이 발생하여 완화)
        com.example.jpanplustest.entity.MembersBatchSize member1 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("호날두").age(25).address("포르투갈").build();
        com.example.jpanplustest.entity.MembersBatchSize member2 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("메시").age(21).address("아르헨티나").build();
        com.example.jpanplustest.entity.MembersBatchSize member3 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("음바페").age(19).address("프랑스").build();
        com.example.jpanplustest.entity.MembersBatchSize member4 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("수아레즈").age(23).address("우르과이").build();
        com.example.jpanplustest.entity.MembersBatchSize member5 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("박지성").age(25).address("대한민국").build();
        com.example.jpanplustest.entity.MembersBatchSize member6 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("베컴").age(22).address("잉글랜드").build();
        com.example.jpanplustest.entity.MembersBatchSize member7 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("류현진").age(20).address("대한민국").build();
        com.example.jpanplustest.entity.MembersBatchSize member8 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("라이언 와이스").age(21).address("미국").build();
        com.example.jpanplustest.entity.MembersBatchSize member9 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("문동주").age(22).address("대한민국").build();
        com.example.jpanplustest.entity.MembersBatchSize member10 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("빅터 레이예스").age(20).address("베네수엘라").build();
        com.example.jpanplustest.entity.MembersBatchSize member11 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("윤동희").age(21).address("대한민국").build();
        com.example.jpanplustest.entity.MembersBatchSize member12 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("김원중").age(22).address("대한민국").build();

        com.example.jpanplustest.entity.TeamsBatchSize team1 =
                com.example.jpanplustest.entity.TeamsBatchSize.builder().name("레알마드리드").build();
        com.example.jpanplustest.entity.TeamsBatchSize team2 =
                com.example.jpanplustest.entity.TeamsBatchSize.builder().name("바르셀로나").build();
        com.example.jpanplustest.entity.TeamsBatchSize team3 =
                com.example.jpanplustest.entity.TeamsBatchSize.builder().name("한화").build();
        com.example.jpanplustest.entity.TeamsBatchSize team4 =
                com.example.jpanplustest.entity.TeamsBatchSize.builder().name("롯데").build();

        com.example.jpanplustest.entity.GroupsBatchSize group1 =
                com.example.jpanplustest.entity.GroupsBatchSize.builder().name("축구").build();
        com.example.jpanplustest.entity.GroupsBatchSize group2 =
                com.example.jpanplustest.entity.GroupsBatchSize.builder().name("야구").build();

        group1.addTeams(team1);
        group1.addTeams(team2);
        group2.addTeams(team3);
        group2.addTeams(team4);
        team1.addMember(member1);
        team1.addMember(member2);
        team1.addMember(member3);
        team2.addMember(member4);
        team2.addMember(member5);
        team2.addMember(member6);
        team3.addMember(member7);
        team3.addMember(member8);
        team3.addMember(member9);
        team4.addMember(member10);
        team4.addMember(member11);
        team4.addMember(member12);

        groupsBatchSize.save(group1);
        groupsBatchSize.save(group2);
        teamsBatchSize.save(team1);
        teamsBatchSize.save(team2);
        teamsBatchSize.save(team3);
        teamsBatchSize.save(team4);
        membersBatchSize.save(member1);
        membersBatchSize.save(member2);
        membersBatchSize.save(member3);
        membersBatchSize.save(member4);
        membersBatchSize.save(member5);
        membersBatchSize.save(member6);
        membersBatchSize.save(member7);
        membersBatchSize.save(member8);
        membersBatchSize.save(member9);
        membersBatchSize.save(member10);
        membersBatchSize.save(member11);
        membersBatchSize.save(member12);

        entityManager.flush();
        entityManager.clear();

        List<com.example.jpanplustest.entity.GroupsBatchSize> groupsList = groupsBatchSize.findAll();

        for(com.example.jpanplustest.entity.GroupsBatchSize item : groupsList) {
            System.out.println("Group Name is " + item.getName());

            for(com.example.jpanplustest.entity.TeamsBatchSize teamItem : item.getTeams()) {
                System.out.println("-  Group have team, Team Name is " + teamItem.getName());

                for(com.example.jpanplustest.entity.MembersBatchSize memberItem : teamItem.getMembers()) {
                    System.out.println("-   (MemberInfo) ================================== ");
                    System.out.println("-   Team have Member, Member Name is " + memberItem.getName());
                    System.out.println("-   Team have Member, Member Address is " + memberItem.getAddress());
                    System.out.println("-   Team have Member, Member Age is " + memberItem.getAge());
                    System.out.println("-   =============================================== ");
                }
            }
        }
    }

    @Test
    @Transactional
    @DisplayName(value = "FETCH JOIN")
    void Test7() {
        // FETCH JOIN 쿼리문을 사용하게되면 한방쿼리로 변환이 되어 완화가 가능하다. 하지만 문제는 xxxxToMany 관련된 관계에 대해서는 1개밖에 FETCH JOIN을 할수 없다.
        com.example.jpanplustest.entity.Members member1 =
                com.example.jpanplustest.entity.Members.builder().name("호날두").age(25).address("포르투갈").build();
        com.example.jpanplustest.entity.Members member2 =
                com.example.jpanplustest.entity.Members.builder().name("메시").age(21).address("아르헨티나").build();
        com.example.jpanplustest.entity.Members member3 =
                com.example.jpanplustest.entity.Members.builder().name("음바페").age(19).address("프랑스").build();
        com.example.jpanplustest.entity.Members member4 =
                com.example.jpanplustest.entity.Members.builder().name("수아레즈").age(23).address("우르과이").build();
        com.example.jpanplustest.entity.Members member5 =
                com.example.jpanplustest.entity.Members.builder().name("박지성").age(25).address("대한민국").build();
        com.example.jpanplustest.entity.Members member6 =
                com.example.jpanplustest.entity.Members.builder().name("베컴").age(22).address("잉글랜드").build();
        com.example.jpanplustest.entity.Members member7 =
                com.example.jpanplustest.entity.Members.builder().name("류현진").age(20).address("대한민국").build();
        com.example.jpanplustest.entity.Members member8 =
                com.example.jpanplustest.entity.Members.builder().name("라이언 와이스").age(21).address("미국").build();
        com.example.jpanplustest.entity.Members member9 =
                com.example.jpanplustest.entity.Members.builder().name("문동주").age(22).address("대한민국").build();
        com.example.jpanplustest.entity.Members member10 =
                com.example.jpanplustest.entity.Members.builder().name("빅터 레이예스").age(20).address("베네수엘라").build();
        com.example.jpanplustest.entity.Members member11 =
                com.example.jpanplustest.entity.Members.builder().name("윤동희").age(21).address("대한민국").build();
        com.example.jpanplustest.entity.Members member12 =
                com.example.jpanplustest.entity.Members.builder().name("김원중").age(22).address("대한민국").build();

        com.example.jpanplustest.entity.Teams team1 =
                com.example.jpanplustest.entity.Teams.builder().name("레알마드리드").build();
        com.example.jpanplustest.entity.Teams team2 =
                com.example.jpanplustest.entity.Teams.builder().name("바르셀로나").build();
        com.example.jpanplustest.entity.Teams team3 =
                com.example.jpanplustest.entity.Teams.builder().name("한화").build();
        com.example.jpanplustest.entity.Teams team4 =
                com.example.jpanplustest.entity.Teams.builder().name("롯데").build();

        com.example.jpanplustest.entity.Groups group1 =
                com.example.jpanplustest.entity.Groups.builder().name("축구").build();
        com.example.jpanplustest.entity.Groups group2 =
                com.example.jpanplustest.entity.Groups.builder().name("야구").build();

        group1.addTeams(team1);
        group1.addTeams(team2);
        group2.addTeams(team3);
        group2.addTeams(team4);
        team1.addMember(member1);
        team1.addMember(member2);
        team1.addMember(member3);
        team2.addMember(member4);
        team2.addMember(member5);
        team2.addMember(member6);
        team3.addMember(member7);
        team3.addMember(member8);
        team3.addMember(member9);
        team4.addMember(member10);
        team4.addMember(member11);
        team4.addMember(member12);

        groups.save(group1);
        groups.save(group2);
        teams.save(team1);
        teams.save(team2);
        teams.save(team3);
        teams.save(team4);
        members.save(member1);
        members.save(member2);
        members.save(member3);
        members.save(member4);
        members.save(member5);
        members.save(member6);
        members.save(member7);
        members.save(member8);
        members.save(member9);
        members.save(member10);
        members.save(member11);
        members.save(member12);

        entityManager.flush();
        entityManager.clear();

        List<com.example.jpanplustest.entity.Groups> groupsList = groups.findAllFetchJoin();

        for(com.example.jpanplustest.entity.Groups item : groupsList) {
            System.out.println("Group Name is " + item.getName());

            for(com.example.jpanplustest.entity.Teams teamItem : item.getTeams()) {
                System.out.println("-  Group have team, Team Name is " + teamItem.getName());

                for(com.example.jpanplustest.entity.Members memberItem : teamItem.getMembers()) {
                    System.out.println("-   (MemberInfo) ================================== ");
                    System.out.println("-   Team have Member, Member Name is " + memberItem.getName());
                    System.out.println("-   Team have Member, Member Address is " + memberItem.getAddress());
                    System.out.println("-   Team have Member, Member Age is " + memberItem.getAge());
                    System.out.println("-   =============================================== ");
                }
            }
        }
    }

    @Test
    @Transactional
    @DisplayName(value = "FETCH JOIN + Batch Size 어노테이션")
    void Test8() {
        // 그렇다면 위 2개를 모두 합쳐보면? JPA에서 현재로써 가장 최선의 성능 개선율을 볼수 있다.
        com.example.jpanplustest.entity.MembersBatchSize member1 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("호날두").age(25).address("포르투갈").build();
        com.example.jpanplustest.entity.MembersBatchSize member2 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("메시").age(21).address("아르헨티나").build();
        com.example.jpanplustest.entity.MembersBatchSize member3 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("음바페").age(19).address("프랑스").build();
        com.example.jpanplustest.entity.MembersBatchSize member4 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("수아레즈").age(23).address("우르과이").build();
        com.example.jpanplustest.entity.MembersBatchSize member5 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("박지성").age(25).address("대한민국").build();
        com.example.jpanplustest.entity.MembersBatchSize member6 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("베컴").age(22).address("잉글랜드").build();
        com.example.jpanplustest.entity.MembersBatchSize member7 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("류현진").age(20).address("대한민국").build();
        com.example.jpanplustest.entity.MembersBatchSize member8 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("라이언 와이스").age(21).address("미국").build();
        com.example.jpanplustest.entity.MembersBatchSize member9 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("문동주").age(22).address("대한민국").build();
        com.example.jpanplustest.entity.MembersBatchSize member10 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("빅터 레이예스").age(20).address("베네수엘라").build();
        com.example.jpanplustest.entity.MembersBatchSize member11 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("윤동희").age(21).address("대한민국").build();
        com.example.jpanplustest.entity.MembersBatchSize member12 =
                com.example.jpanplustest.entity.MembersBatchSize.builder().name("김원중").age(22).address("대한민국").build();

        com.example.jpanplustest.entity.TeamsBatchSize team1 =
                com.example.jpanplustest.entity.TeamsBatchSize.builder().name("레알마드리드").build();
        com.example.jpanplustest.entity.TeamsBatchSize team2 =
                com.example.jpanplustest.entity.TeamsBatchSize.builder().name("바르셀로나").build();
        com.example.jpanplustest.entity.TeamsBatchSize team3 =
                com.example.jpanplustest.entity.TeamsBatchSize.builder().name("한화").build();
        com.example.jpanplustest.entity.TeamsBatchSize team4 =
                com.example.jpanplustest.entity.TeamsBatchSize.builder().name("롯데").build();

        com.example.jpanplustest.entity.GroupsBatchSize group1 =
                com.example.jpanplustest.entity.GroupsBatchSize.builder().name("축구").build();
        com.example.jpanplustest.entity.GroupsBatchSize group2 =
                com.example.jpanplustest.entity.GroupsBatchSize.builder().name("야구").build();

        group1.addTeams(team1);
        group1.addTeams(team2);
        group2.addTeams(team3);
        group2.addTeams(team4);
        team1.addMember(member1);
        team1.addMember(member2);
        team1.addMember(member3);
        team2.addMember(member4);
        team2.addMember(member5);
        team2.addMember(member6);
        team3.addMember(member7);
        team3.addMember(member8);
        team3.addMember(member9);
        team4.addMember(member10);
        team4.addMember(member11);
        team4.addMember(member12);

        groupsBatchSize.save(group1);
        groupsBatchSize.save(group2);
        teamsBatchSize.save(team1);
        teamsBatchSize.save(team2);
        teamsBatchSize.save(team3);
        teamsBatchSize.save(team4);
        membersBatchSize.save(member1);
        membersBatchSize.save(member2);
        membersBatchSize.save(member3);
        membersBatchSize.save(member4);
        membersBatchSize.save(member5);
        membersBatchSize.save(member6);
        membersBatchSize.save(member7);
        membersBatchSize.save(member8);
        membersBatchSize.save(member9);
        membersBatchSize.save(member10);
        membersBatchSize.save(member11);
        membersBatchSize.save(member12);

        entityManager.flush();
        entityManager.clear();

        List<com.example.jpanplustest.entity.GroupsBatchSize> groupsList = groupsBatchSize.findAllFetchJoin();

        for(com.example.jpanplustest.entity.GroupsBatchSize item : groupsList) {
            System.out.println("Group Name is " + item.getName());

            for(com.example.jpanplustest.entity.TeamsBatchSize teamItem : item.getTeams()) {
                System.out.println("-  Group have team, Team Name is " + teamItem.getName());

                for(com.example.jpanplustest.entity.MembersBatchSize memberItem : teamItem.getMembers()) {
                    System.out.println("-   (MemberInfo) ================================== ");
                    System.out.println("-   Team have Member, Member Name is " + memberItem.getName());
                    System.out.println("-   Team have Member, Member Address is " + memberItem.getAddress());
                    System.out.println("-   Team have Member, Member Age is " + memberItem.getAge());
                    System.out.println("-   =============================================== ");
                }
            }
        }
    }
}
