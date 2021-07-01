package com.binghe;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("즉시 로딩 테스트")
public class EagerTest {

    @DisplayName("fetch 설정값을 FetchType.Eager로하면 해당 객체를 가져올 때, join을 통해 한번에 가져온다.")
    @Test
    void eagerLoading() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("Team A");
            entityManager.persist(team);

            Member member = new Member();
            member.setName("binghe");
            member.setTeam(team);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // fetch 설정이 Eager이므로 Team까지 Join을 통해 가져온다.
            Member findMember = entityManager.find(Member.class, member.getId());

            System.out.println("===============밑에 쿼리가 날라가지 않는다.===============");
            System.out.println(findMember.getTeam().getClass());
            System.out.println(findMember.getTeam().getName());
            System.out.println("=============================================");

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    // Member에서 애노테이션 설정을 바꿔줘야한다.
    @DisplayName("즉시 로딩은 N+1 문제를 일으킨다.")
    @Test
    void nonExpectable() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("Team A");
            entityManager.persist(team);

            Member member = new Member();
            member.setName("binghe");
            member.setTeam(team);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // 모든 Member 가져오기 -> 가져온 Member(프록시처럼 Member 객체만 가져온다)마다 즉시 로딩이므로, 개수만큼 쿼리를 날려 상태를 가져온다.
            List<Member> members = entityManager.createQuery("select m from Member m", Member.class)
                .getResultList();

            // SQL: select * from Member -> select * from Team where TEAM_ID = ? -> Member 개수만큼 날라간다.

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
