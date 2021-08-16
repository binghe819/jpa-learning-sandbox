package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("패치 조인 기본 테스트")
public class FetchJoinBasicTest {

    @DisplayName("fetch join을 사용하면 연관된 엔티티를 함께 조회할 수 있다. 즉시 로딩과 비슷하다고 볼 수 있다. (즉시 로딩과 비슷)")
    @Test
    void fetchJoin_OneQuery_Test() {
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
            member.setName("binghe");
            member.setAge(26);
            member.setTeam(team);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            String query = "select m from Member m join fetch m.team";
            TypedQuery<Member> result = entityManager.createQuery(query, Member.class);
            List<Member> members = result.getResultList();
            Member findMember = members.get(0);

            // then
            assertThat(members)
                .hasSize(1);
            assertThat(findMember.getTeam())
                .isInstanceOf(Team.class);

            System.out.println("=================================================");
            System.out.println("Team 이름: " + findMember.getTeam().getName());
            System.out.println("Team은 프록시가 아닌 진짜 객체 -> " + findMember.getTeam().getClass());

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("fetch join을 사용하지 않으면 연관된 엔티티는 프록시로 가져오고, 사용될 때 쿼리를 다시 날려 가져온다. (지연 로딩)")
    @Test
    void nonFetchJoin_TwoQuery_Test() {
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
            member.setName("binghe");
            member.setAge(26);
            member.setTeam(team);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            String query = "select m from Member m";
            TypedQuery<Member> result = entityManager.createQuery(query, Member.class);
            List<Member> members = result.getResultList();
            Member findMember = members.get(0);

            // then
            assertThat(members)
                .hasSize(1);
            assertThat(findMember.getTeam())
                .isInstanceOf(Team.class);

            System.out.println("=================================================");
            System.out.println("Team 이름 (쿼리를 날려 가져옴): " + findMember.getTeam().getName());
            System.out.println("Team은 프록시 객체이다 -> " + findMember.getTeam().getClass());

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }
}
