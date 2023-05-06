package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("N + 1 테스트")
public class N_Plus_OneTest {

    private List<Member> members;

    @BeforeEach
    void setUp() {
        Team team1 = new Team();
        team1.setName("Team A");
        Team team2 = new Team();
        team2.setName("Team B");

        Member member1 = new Member();
        member1.setName("회원 1");
        member1.setTeam(team1);

        Member member2 = new Member();
        member2.setName("회원 2");
        member2.setTeam(team1);

        Member member3 = new Member();
        member3.setName("회원 3");
        member3.setTeam(team2);

        members = Arrays.asList(member1, member2, member3);
    }

    @DisplayName("fetch join을 사용하지 않으면 지연 로딩으로 인한 N + 1 문제가 발생한다.")
    @Test
    void lazyLoading_N_Plus_One_Test() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            for (Member member : members) {
                entityManager.persist(member);
            }
            entityManager.flush();
            entityManager.clear();

            // when
            String query = "select m from Member m";
            TypedQuery<Member> result = entityManager.createQuery(query, Member.class);
            List<Member> findMembers = result.getResultList();

            // then
            assertThat(findMembers).hasSize(3);

            for (Member member : findMembers) {
                System.out.println("member = " + member.getName() + ", " + member.getTeam().getName());
                System.out.println("연관 엔티티 Team은 프록시 객체이다 = " + member.getTeam().getClass());
                // 회원 1, 팀 A (SQL)
                // 회원 2, 팀 A (1차 캐싱)
                // 회원 3, 팀 B (SQL)
                // 쿼리 3번 날라감.
                // 만약 회원 10000명 이상이라면.. -> N + 1 문제 발생
            }

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("fetch join을 이용하여 N + 1 문제를 해결할 수 있다. (즉시 로딩으로 쿼리를 한 번만 날림)")
    @Test
    void fetchJoin_Test() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            for (Member member : members) {
                entityManager.persist(member);
            }
            entityManager.flush();
            entityManager.clear();

            // when
            String query = "select m from Member m join fetch m.team";
            TypedQuery<Member> result = entityManager.createQuery(query, Member.class);
            List<Member> findMembers = result.getResultList();

            // then
            assertThat(findMembers).hasSize(3);

            for (Member member : findMembers) {
                System.out.println("member = " + member.getName() + ", " + member.getTeam().getName());
                System.out.println("연관 엔티티 Team은 프록시가 아닌 진짜 객체다 = " + member.getTeam().getClass());

                // 회원 1, 팀 A (1차 캐싱)
                // 회원 2, 팀 A (1차 캐싱)
                // 회원 3, 팀 B (1차 캐싱)
                // 쿼리 1번 날라감.
            }

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
