package com.binghe.one_way;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.template.EntityManagerTemplate;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("연관관계 주인 단방향 테스트")
public class OneWayTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @Test
    void owner() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            Team team = new Team();
            team.setName("Team A");
            entityManager.persist(team);

            Member member = new Member();
            member.setName("binghe");
            member.setTeam(team);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            Member findMember = entityManager.find(Member.class, member.getId());
            Team findTeam = findMember.getTeam();

            // then
            assertThat(findMember.getName()).isEqualTo("binghe");
            assertThat(findTeam.getName()).isEqualTo("Team A");

            tx.commit();
        }));
    }
}
