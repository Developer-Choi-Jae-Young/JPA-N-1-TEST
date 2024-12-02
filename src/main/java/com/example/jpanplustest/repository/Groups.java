package com.example.jpanplustest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Groups extends JpaRepository<com.example.jpanplustest.entity.Groups, Long> {
    //@Query("SELECT g FROM Groups g JOIN FETCH  g.teams t JOIN FETCH t.members") // multiple bags error발생 xxxToMany 관계의 객체를 두개이상 fetch join 할경우 발생!!!
    @Query("SELECT g FROM Groups g JOIN FETCH  g.teams t")
    List<com.example.jpanplustest.entity.Groups> findAllFetchJoin();
}
