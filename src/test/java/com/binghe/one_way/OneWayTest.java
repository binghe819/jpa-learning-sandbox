package com.binghe.one_way;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("1 : N 단방향 테스트")
public class OneWayTest {

    @DisplayName("1이 연관관계의 주인이 된다. 즉, 1에 해당하는 객체를 수정하면 DB에 쿼리가 날라간다.")
    @Test
    void oneWay() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setName("binghe");
            entityManager.persist(member);

            Team team = new Team();
            team.setName("TeamA");
            team.getMembers().add(member); // 이 코드로 인해 쿼리가 날라간다. (MEMBER테이블에 UPDATE문을 날린다 -> 가장 큰 문제)
            entityManager.persist(team);

            entityManager.flush();
            entityManager.clear();

            // then
            Team findTeam = entityManager.find(Team.class, team.getId());
            assertThat(findTeam.getMembers().size()).isEqualTo(1);

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
