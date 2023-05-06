package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("조인 테스트")
public class JoinTest {

    @DisplayName("내부 조인 (INNER JOIN)")
    @Test
    void innerJoin() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setName("binghe");
            member.setAge(26);

            Team team1 = new Team();
            team1.setName("Team A");
            team1.addMember(member); // cascade.persist 설정함.
            entityManager.persist(team1);

            entityManager.flush();
            entityManager.clear();

            // when (inner 생략 가능)
            String query = "select m from Member as m inner join m.team as t";
            List<Member> result = entityManager.createQuery(query, Member.class)
                .getResultList();

            // then
            Member findMember = result.get(0);
            assertThat(findMember.getName()).isEqualTo(member.getName());
            assertThat(findMember.getTeam().getName()).isEqualTo("Team A");

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("외부 조인 (OUTER JOIN)")
    @Test
    void outerJoin() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setName("binghe");
            member.setAge(26);

            Team team1 = new Team();
            team1.setName("Team A");
            team1.addMember(member); // cascade.persist 설정함.
            entityManager.persist(team1);

            entityManager.flush();
            entityManager.clear();

            // when (outer 생략 가능)
            String query = "select m from Member as m left outer join m.team as t";
            List<Member> result = entityManager.createQuery(query, Member.class)
                .getResultList();

            // then
            Member findMember = result.get(0);
            assertThat(findMember.getName()).isEqualTo(member.getName());
            assertThat(findMember.getTeam().getName()).isEqualTo("Team A");

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("세타 조인 (cross join)")
    @Test
    void setaJoin() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setName("Team A");
            member.setAge(26);

            Team team1 = new Team();
            team1.setName("Team A");
            team1.addMember(member); // cascade.persist 설정함.
            entityManager.persist(team1);

            entityManager.flush();
            entityManager.clear();

            // when (outer 생략 가능)
            String query = "select m from Member as m, Team as t where m.name = t.name";
            List<Member> result = entityManager.createQuery(query, Member.class)
                .getResultList();

            // then
            Member findMember = result.get(0);
            assertThat(findMember.getName()).isEqualTo(member.getName());
            assertThat(findMember.getTeam().getName()).isEqualTo("Team A");

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("ON 절 - 조인 대상 필터링 (회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인)")
    @Test
    void leftJoin_On() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setName("binghe");
            member.setAge(26);

            Team team1 = new Team();
            team1.setName("Team A");
            team1.addMember(member); // cascade.persist 설정함.
            entityManager.persist(team1);

            entityManager.flush();
            entityManager.clear();

            // when (outer 생략 가능)
            String query = "select m from Member as m left join Team t ON t.name=:name";
            List<Member> result = entityManager.createQuery(query, Member.class)
                .setParameter("name", "Team A")
                .getResultList();

            // then
            Member findMember = result.get(0);
            assertThat(findMember.getName()).isEqualTo(member.getName());
            assertThat(findMember.getTeam().getName()).isEqualTo("Team A");

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("ON 절 - 연관관계 없는 엔티티 외부 조인 (회원의 이름과 팀의 이름이 같은 대상 외부 조인)")
    @Test
    void leftJoin_On_() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setName("Team A");
            member.setAge(26);

            Team team1 = new Team();
            team1.setName("Team A");
            team1.addMember(member); // cascade.persist 설정함.
            entityManager.persist(team1);

            entityManager.flush();
            entityManager.clear();

            // when (outer 생략 가능)
            String query = "select m from Member as m left join Team t on m.name = t.name";
            List<Member> result = entityManager.createQuery(query, Member.class)
                .getResultList();

            // then
            Member findMember = result.get(0);
            assertThat(findMember.getName()).isEqualTo(member.getName());
            assertThat(findMember.getTeam().getName()).isEqualTo("Team A");

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
