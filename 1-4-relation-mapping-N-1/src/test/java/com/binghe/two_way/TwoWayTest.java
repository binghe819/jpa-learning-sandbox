package com.binghe.two_way;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.template.EntityManagerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("N:1 양방향 테스트")
public class TwoWayTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("멤버 N : 1 팀 - 연관관계 주인이 아닌 엔티티(Team)도 조회가 가능하다. (조회만 가능)")
    @Test
    void twoWay() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            Team team = new Team();
            team.setName("Team A");
            entityManager.persist(team);

            Member member = new Member();
            member.setTeam(team);
            member.setName("binghe");
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            Team findTeam = entityManager.find(Team.class, team.getId());

            // then
            assertThat(findTeam.getMembers()).hasSize(1);
            assertThat(findTeam.getMembers())
                .extracting("name")
                .containsExactly("binghe");

            tx.commit();
        }));
    }

    @DisplayName("멤버 N : 1 팀 - 연관관계 주인이 아닌 엔티티(Team)를 수정해도 DB엔 쿼리가 날라가지 않는다.")
    @Test
    void update_NonOwner() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            Team team = new Team();
            team.setName("Team A");
            entityManager.persist(team);

            Member member = new Member();
            member.setTeam(team);
            member.setName("binghe");
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            Team savedTeam = entityManager.find(Team.class, team.getId());
            savedTeam.getMembers().remove(0);
            assertThat(savedTeam.getMembers()).hasSize(0); // 해당 트랜잭션의 객체상에선 삭제된다.

            entityManager.flush();
            entityManager.clear();

            // then
            Team findTeam = entityManager.find(Team.class, team.getId());
            assertThat(findTeam.getMembers()).hasSize(1); // 삭제했음에도 여전히 size가 1이다 (실제 DB에선 삭제되지 않음)
        }));
    }
}
