package com.binghe.one_way;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.template.EntityManagerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("N:1 단방향 테스트")
public class OneWayTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("멤버 N : 1 팀 - 멤버 객체(N)를 가져오면 팀(1)의 정보도 join을 통해 가져온다. (EAGER인 경우)")
    @Test
    void oneWay() {
        entityManagerTemplate.execute((entityManager, tx) -> {
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
            Member findMember = entityManager.find(Member.class, member.getId());
            Team findTeam = findMember.getTeam();

            // then
            assertThat(findTeam.getName()).isEqualTo("Team A");

            tx.commit();
        });
    }
}
