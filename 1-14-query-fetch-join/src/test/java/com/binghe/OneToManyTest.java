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

@DisplayName("컬렉션 패치 조인 (1 : N)")
public class OneToManyTest {

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

    @DisplayName("1대다 관계를 fetch join을 통해 불러오면, 중복된 데이터가 결과로 넘어온다. (객체와 RDB의 차이점이기도 하다)")
    @Test
    void fetchJoin_CollectionFetchJoin_test() {
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
                // 검색 결과의 첫 번째와 두 번째 팀은 같은 객체인 것을 알 수 있다.
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

    @DisplayName("1대다 관계를 fetch join을 통해 불러올 때, DISTINCT를 사용해주면 중복 데이터를 제거할 수 있다.")
    @Test
    void fetchJoin_CollectionFetchjoinWithDISTINCT_test() {
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
            String query = "select distinct t from Team t join fetch t.members";
            TypedQuery<Team> result = entityManager.createQuery(query, Team.class);
            List<Team> findTeams = result.getResultList();

            // then
            assertThat(findTeams).hasSize(2); // 중복 제거되어 2개가 정확히 2개가 불러온다.

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
