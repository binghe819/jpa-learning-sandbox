package com.binghe.one_way;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.template.EntityManagerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("1 : N 단방향 테스트")
public class OneWayTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("팀 1 : N 멤버 - 팀(1)이 연관관계 주인이며, 팀 엔티티를 수정하면 DB에 쿼리가 날라간다.")
    @Test
    void oneWay() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            Member member = new Member();
            member.setName("binghe");
            entityManager.persist(member);

            Team team = new Team();
            team.setName("TeamA");
            team.getMembers().add(member); // 이 코드로 인해 쿼리가 날라간다. (MEMBER테이블에 UPDATE문을 날린다 -> 가장 큰 문제)
            entityManager.persist(team);

            entityManager.flush();
            entityManager.clear();

            // then
            Team findTeam = entityManager.find(Team.class, team.getId());
            assertThat(findTeam.getMembers().size()).isEqualTo(1);

            tx.commit();
        }));
    }
}
