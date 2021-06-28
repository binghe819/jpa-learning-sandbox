package com.binghe.two_way;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("연관관계 주인 양방향 테스트")
public class TwoWayTest {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private Team team;
    private Member member1;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");
        entityManager = entityManagerFactory.createEntityManager();

        team = new Team();
        team.setName("Team A");

        member1 = new Member();
        member1.setName("binghe");
        member1.setTeam(team);
    }

    @DisplayName("연관관계 주인이 외래 키를 가진 테이블을 관리한다. (등록, 수정)")
    @Test
    void ownerOfRelationship() {
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();

        try {
            // given
            entityManager.persist(team);
            entityManager.persist(member1);

            entityManager.flush();
            entityManager.clear();

            // when
            Member newMember = new Member();
            newMember.setName("mark");
            newMember.setTeam(team); // 연관관계 주인을 수정해줘야 테이블에 반영된다.
            entityManager.persist(newMember);

            entityManager.flush();
            entityManager.clear();

            // then
            Member findMember = entityManager.find(Member.class, member1.getId());
            Team findTeam = findMember.getTeam();
            assertThat(findTeam.getMembers().size()).isEqualTo(2);
            assertThat(findTeam.getMembers())
                .extracting("name")
                .containsExactly("binghe", "mark");

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!!! " + e.getMessage());
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("연관관계 주인이 아닌 경우 외래 키 테이블을 관리할 수 없다.")
    @Test
    void notOwnerOfRelationship() {
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();

        try {
            // given
            entityManager.persist(team);
            entityManager.persist(member1);

            entityManager.flush();
            entityManager.clear();

            // when
            Member newMember = new Member();
            newMember.setName("binghe");
            Team findTeam = entityManager.find(Member.class, member1.getId()).getTeam();
            findTeam.getMembers().add(newMember); // 연관관계 주인이 아닌 객체에서의 변경은 테이블에 반영이 안된다.

            entityManager.flush();
            entityManager.clear();

            // then
            Team resultTeam = entityManager.find(Member.class, member1.getId()).getTeam();
            assertThat(resultTeam.getMembers().size()).isEqualTo(1); // 연관관계 주인의 반대쪽에 추가해도 테이블엔 반영이 안된다.
            assertThat(resultTeam.getMembers())
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
