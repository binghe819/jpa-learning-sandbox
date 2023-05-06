package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("경로 표현식 테스트")
public class PathExpressionTest {

    @DisplayName("상태 필드 - 단순한 값을 저장하기 위한 필드(단순 경로), 일반 SQL과 동일하게 쿼리가 날라간다. (join이 발생하지 않음.)")
    @Test
    void stateField() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member1 = new Member();
            member1.setName("binghe");
            member1.setAge(10);
            entityManager.persist(member1);

            Member member2 = new Member();
            member2.setName("mark");
            entityManager.persist(member2);

            entityManager.flush();
            entityManager.clear();

            // when
            // SQL 쿼리: select m.name from Member m;
            String query = "select m.name from Member m";
            TypedQuery<String> result = entityManager.createQuery(query, String.class);
            List<String> memberNames = result.getResultList();

            // then
            assertThat(memberNames)
                .hasSize(2)
                .containsExactly(member1.getName(), member2.getName());

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("단일 값 연관 경로 - `@ManyToOne`, `@OneToOne`등 대상인 엔티티를 묵시적 내부 조인(inner join)을 통해 가져온다.")
    @Test
    void singleValue() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member1 = new Member();
            member1.setName("binghe");

            Team team = new Team();
            team.setName("Team A");
            team.addMember(member1);
            entityManager.persist(team); // 영속성 전이되어 있음.

            entityManager.flush();
            entityManager.clear();

            // when
            // SQL 쿼리: select * from Member m inner join Team t on m.id = t.id (묵시적 내부 조인이 발생함)
            // 묵시적 조인 JPQL: "select t from Member m join m.team t"
            String query = "select m.team from Member m";
            TypedQuery<Team> result = entityManager.createQuery(query, Team.class);
            List<Team> memberTeams = result.getResultList();

            // then
            assertThat(memberTeams)
                .hasSize(1)
                .extracting("id", "name")
                    .containsExactly(Tuple.tuple(team.getId(), team.getName()));

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("컬렉션 값 연관 경로 - 묵시적 조인이 발생하며, 탐색은 안된다. 단 명시적 조인을 통해 가져올 수 있다.")
    @Test
    void collectionValue_join() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member1 = new Member();
            member1.setName("binghe");

            Member member2 = new Member();
            member2.setName("mark");

            Team team = new Team();
            team.setName("Team A");
            team.addMember(member1);
            team.addMember(member2);
            entityManager.persist(team); // 영속성 전이되어 있음.

            entityManager.flush();
            entityManager.clear();

            // when
            // SQL: select * from Team t inner join Member m on m.teamId = t.id
//             똑같은 JPQL: select m.name from Team t join Member m on m.team = t
            String query = "select m.name from Team t join t.members m";
            TypedQuery<String> result = entityManager.createQuery(query, String.class);
            List<String> memberNames = result.getResultList();

            // then
            assertThat(memberNames)
                .hasSize(2)
                    .containsExactly(member1.getName(), member2.getName());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }
}
