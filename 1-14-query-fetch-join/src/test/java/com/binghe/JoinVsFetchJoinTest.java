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

@DisplayName("일반 join과 fetch join의 차이")
public class JoinVsFetchJoinTest {

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

    @DisplayName("일반 join은 연관관계를 고려하지 않고, 단지 SELECT(프로젝션) 절에 지정한 엔티티만 조회한다. (지연 로딩)")
    @Test
    void join_General_test() {
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
            String query = "select t from Team t join t.members m";
            TypedQuery<Team> result = entityManager.createQuery(query, Team.class);
            List<Team> findTeams = result.getResultList();

            // then
            assertThat(findTeams).hasSize(3); // 2개가 와야하는데 3개가 오게된다.

            for (Team findTeam : findTeams) {
                System.out.println(
                    "팀 이름 = " + findTeam.getName() +
                        ", 팀 참조 주소 = " + findTeam +
                        ", 팀원 인수 = " + findTeam.getMembers().size());
                // fetch join이 아니므로 지연 로딩을 위한 쿼리가 날라간다.
                for (Member member : findTeam.getMembers()) {
                    System.out.println("  -> 회원 이름" + member.getName() + "회원 참조 주소 = " + member);
                }
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

    @DisplayName("fetch join은 지정한 연관된 엔티티도 함께 조회한다. 객체 그래프를 SQL 한번에 조회하는 개념이다.(즉시 로딩)")
    @Test
    void join_FetchJoin_test() {
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
            String query = "select t from Team t join fetch t.members";
            TypedQuery<Team> result = entityManager.createQuery(query, Team.class);
            List<Team> findTeams = result.getResultList();

            // then
            assertThat(findTeams).hasSize(3); // 2개가 와야하는데 3개가 오게된다.

            for (Team findTeam : findTeams) {
                System.out.println(
                    "팀 이름 = " + findTeam.getName() +
                        ", 팀 참조 주소 = " + findTeam +
                        ", 팀원 인수 = " + findTeam.getMembers().size());
                // fetch join이므로 쿼리가 날라가지 않는다.
                for (Member member : findTeam.getMembers()) {
                    System.out.println("  -> 회원 이름" + member.getName() + "회원 참조 주소 = " + member);
                }
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
