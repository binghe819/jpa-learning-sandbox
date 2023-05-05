package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.template.EntityManagerTemplate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지연 로딩 테스트")
public class LazyTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("fetch 설정값을 FetchType.LAZY로하면 해당 의존성 객체는 프록시 객체를 만든다. 그리고 사용하는 시점에 쿼리를 통해 가져온다.")
    @Test
    void lazyLoading() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            LazyTeam team = new LazyTeam();
            team.setName("Team A");
            entityManager.persist(team);

            LazyMember member = new LazyMember();
            member.setName("binghe");
            member.setTeam(team);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // fetch 설정이 LAZY이므로 Member 객체만 가져온다.
            LazyMember findMember = entityManager.find(LazyMember.class, member.getId());

            // findMember.getTeam() 시점에 쿼리가 날라간다.
            System.out.println("===============밑에 쿼리가 날라간다(getTeam)===============");
            assertThat(findMember.getTeam().getClass().toString()).startsWith("class com.binghe.LazyTeam$HibernateProxy");
            assertThat(findMember.getTeam())
                .extracting("name")
                .isEqualTo("Team A");
            System.out.println("=============================================");

            tx.commit();
        }));
    }

    @DisplayName("지연 로딩도 N + 1가 발생하지만, 호출될 때 쿼리가 날라간다. (List<Member>만 가져오고 싶을 때 유용)")
    @Test
    void lazyLoading_List() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            LazyTeam team = new LazyTeam(); team.setName("Team A");
            entityManager.persist(team);

            LazyMember member = new LazyMember(); member.setName("binghe"); member.setTeam(team);
            entityManager.persist(member);

            LazyTeam team2 = new LazyTeam(); team2.setName("Team B");
            entityManager.persist(team2);

            LazyMember member2 = new LazyMember(); member2.setName("mark"); member2.setTeam(team2);
            entityManager.persist(member2);

            entityManager.flush();
            entityManager.clear();

            // when
            System.out.println("===============쿼리가 한 번만 날라간다.===============");
            List<LazyMember> members = entityManager.createQuery("select m from LazyMember m", LazyMember.class)
                .getResultList();

            members.forEach(it -> {
                System.out.println("조회할 때만 날아간다.");
                System.out.println(it.getTeam().getName());
            });

            // then
            assertThat(members).hasSize(2);
        }));
    }
}
