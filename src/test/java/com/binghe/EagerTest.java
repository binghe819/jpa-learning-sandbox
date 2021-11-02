package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.template.EntityManagerTemplate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("즉시 로딩 테스트")
public class EagerTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("fetch 설정값을 FetchType.Eager로하면 해당 객체를 가져올 때, join을 통해 한번에 가져온다.")
    @Test
    void eagerLoading() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            EagerTeam team = new EagerTeam();
            team.setName("Team A");
            entityManager.persist(team);

            EagerMember member = new EagerMember();
            member.setName("binghe");
            member.setTeam(team);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // fetch 설정이 Eager이므로 Team까지 Join을 통해 가져온다.
            System.out.println("===============이 시점에 한번 쿼리가 날라간다. (find)===============");
            EagerMember findMember = entityManager.find(EagerMember.class, member.getId());

            System.out.println("===============이 시점부터 쿼리가 날라가지 않는다.===============");
            assertThat(findMember.getTeam().getClass().toString()).isEqualTo("class com.binghe.EagerTeam");
            assertThat(findMember.getTeam())
                .extracting("name")
                .isEqualTo(team.getName());
            System.out.println("=============================================");

            tx.commit();
        }));
    }

    @DisplayName("즉시 로딩은 N+1 문제를 일으킨다.")
    @Test
    void nonExpectable() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            EagerTeam team = new EagerTeam(); team.setName("Team A");
            entityManager.persist(team);

            EagerMember member = new EagerMember(); member.setName("binghe"); member.setTeam(team);
            entityManager.persist(member);

            EagerTeam team2 = new EagerTeam(); team2.setName("Team B");
            entityManager.persist(team2);

            EagerMember member2 = new EagerMember(); member2.setName("mark"); member2.setTeam(team2);
            entityManager.persist(member2);

            entityManager.flush();
            entityManager.clear();

            // 모든 Member 가져오기 -> 가져온 Member(프록시처럼 Member 객체만 가져온다)마다 즉시 로딩이므로, 개수만큼 쿼리를 날려 상태(Team)를 가져온다.
            List<EagerMember> members = entityManager.createQuery("select m from EagerMember m", EagerMember.class)
                .getResultList();
            // SQL: select * from Member -> select * from Team where TEAM_ID = ? -> Member 개수만큼 날라간다.

            assertThat(members).hasSize(2);

            tx.commit();
        }));
    }
}
