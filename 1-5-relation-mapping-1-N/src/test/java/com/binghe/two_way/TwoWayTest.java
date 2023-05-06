package com.binghe.two_way;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("1 : N 양방향 테스트")
public class TwoWayTest {

    @DisplayName("N이 연관관계의 주인이 아니므로, 읽기 기능만 가능하다. (저장은 당연히 안된다)")
    @Test
    void twoWay() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Team team = new Team();
            team.setName("TeamA");
            entityManager.persist(team);

            Member member = new Member();
            member.setName("binghe");
            entityManager.persist(member);

            team.getMembers().add(member); // 1에 해당하는 객체(주인)에서 변경이 일어나므로 테이블에 쿼리가 날라간다.

            entityManager.flush();
            entityManager.clear();

            // when
            Team findTeam = entityManager.find(Team.class, team.getId());

            Member newMember = new Member();
            newMember.setName("mark");
            newMember.setTeam(findTeam); // N에 해당하는 객체에서 추가해줘도 테이블엔 쿼리가 안날라간다.

            entityManager.flush();
            entityManager.clear();

            // then
            Team resultTeam = entityManager.find(Team.class, team.getId());
            assertThat(resultTeam.getMembers().size()).isEqualTo(1);

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
