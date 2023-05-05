package com.binghe.two_way;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.template.EntityManagerTemplate;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("연관관계 주인 양방향 테스트")
public class TwoWayTest {

    private EntityManagerTemplate entityManagerTemplate;
    private Team team;
    private Member member1;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();

        team = new Team();
        team.setName("Team A");

        member1 = new Member();
        member1.setName("binghe");
        member1.setTeam(team);
    }

    @DisplayName("연관관계 주인이 외래 키를 가진 테이블을 관리한다. (등록, 수정)")
    @Test
    void ownerOfRelationship() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            entityManager.persist(team);
            entityManager.persist(member1);

            flushAndClear(entityManager);

            // when
            Member newMember = new Member();
            newMember.setName("mark");
            newMember.setTeam(team); // 연관관계 주인을 수정해줘야 테이블에 반영된다.
            entityManager.persist(newMember);

            flushAndClear(entityManager);

            // then
            Member findMember = entityManager.find(Member.class, member1.getId());
            Team findTeam = findMember.getTeam();
            assertThat(findTeam.getMembers().size()).isEqualTo(2);
            assertThat(findTeam.getMembers())
                .extracting("name")
                .containsExactly(member1.getName(), "mark");

            tx.commit();
        }));
    }

    @DisplayName("연관관계 주인이 아닌 경우 외래 키 테이블을 관리할 수 없다. - 연관관계 주인이 아닌 객체에서의 변경은 테이블에 반영되지 않는다.")
    @Test
    void notOwnerOfRelationship() {
        entityManagerTemplate.execute((entityManager, tx) -> {
            // given
            entityManager.persist(team);
            entityManager.persist(member1);

            flushAndClear(entityManager);

            // when
            Member newMember = new Member();
            newMember.setName("binghe");
            Team findTeam = entityManager.find(Member.class, member1.getId()).getTeam();
            findTeam.getMembers().add(newMember); // 연관관계 주인이 아닌 객체에서의 변경은 테이블에 반영이 안된다.

            flushAndClear(entityManager);

            // then
            Team resultTeam = entityManager.find(Member.class, member1.getId()).getTeam();
            assertThat(resultTeam.getMembers().size()).isNotEqualTo(2);
            assertThat(resultTeam.getMembers()).hasSize(1); // 연관관계 주인의 반대쪽에 추가해도 테이블엔 반영이 안된다.
            assertThat(resultTeam.getMembers())
                .extracting("name")
                .containsExactly("binghe");

            tx.commit();
        });
    }

    private void flushAndClear(EntityManager entityManager) {
        entityManager.flush();
        entityManager.clear();
    }
}
