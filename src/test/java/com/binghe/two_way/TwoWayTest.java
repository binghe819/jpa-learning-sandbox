package com.binghe.two_way;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("N:1 양방향 테스트")
public class TwoWayTest {

    @DisplayName("연관관계 주인이 아닌쪽에서도 조회가 가능하다. (조회만 가능)")
    @Test
    void twoWay() {
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
            assertThat(findTeam.getMembers().size()).isEqualTo(1);
            assertThat(findTeam.getMembers())
                .extracting("name")
                .containsExactly("binghe");

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
