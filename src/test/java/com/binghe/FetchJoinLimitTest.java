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

@DisplayName("fetch join의 한계")
public class FetchJoinLimitTest {

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

    @DisplayName("컬렉션 패치 조인을 사용하면 경로 로그를 남기고 메모리에서 페이징한다. (사용하면 안되는 유형)")
    @Test
    void collectionFetchJoin_WarningLog_test() {
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
            String query = "select t from Team t join fetch t.members m";
            TypedQuery<Team> result = entityManager.createQuery(query, Team.class)
                .setFirstResult(0)
                .setMaxResults(1);
            List<Team> findTeams = result.getResultList();

            // then
            assertThat(findTeams).hasSize(1);

            for (Team findTeam : findTeams) {
                System.out.println(
                    "팀 이름 = " + findTeam.getName() +
                        ", 팀 참조 주소 = " + findTeam +
                        ", 팀원 인수 = " + findTeam.getMembers().size());

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

    @DisplayName("둘 이상의 일대다(컬렉션) fetch join에서 @BatchSize 애노테이션이 붙어 있으면 where in 쿼리로 한번에 가져온다.")
    @Test
    void collectionFetchJoin_BatchSize_test() {
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
            String query = "select t from Team t";
            TypedQuery<Team> result = entityManager.createQuery(query, Team.class)
                .setFirstResult(0)
                .setMaxResults(2);
            List<Team> findTeams = result.getResultList();

            // then
            assertThat(findTeams).hasSize(2);

            for (Team findTeam : findTeams) {
                System.out.println(
                    "팀 이름 = " + findTeam.getName() +
                        ", 팀 참조 주소 = " + findTeam +
                        ", 팀원 인수 = " + findTeam.getMembers().size());

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
