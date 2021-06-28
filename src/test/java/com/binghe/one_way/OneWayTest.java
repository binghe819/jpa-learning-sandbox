package com.binghe.one_way;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("N:1 단방향 테스트")
public class OneWayTest {

    @DisplayName("멤버 N : 1 팀 - 멤버 객체를 가져오면 팀의 정보도 join을 통해 가져온다.")
    @Test
    void oneWay() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Team team = new Team();
            team.setName("Team A");
            entityManager.persist(team);

            Member member = new Member();
            member.setTeam(team);
            member.setName("binghe");
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            Member findMember = entityManager.find(Member.class, member.getId());
            Team findTeam = findMember.getTeam();

            // then
            assertThat(findTeam.getName()).isEqualTo("Team A");

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!!! " + e.getMessage());
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }
}
