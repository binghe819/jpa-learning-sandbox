package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지연 로딩 테스트")
public class LazyTest {

    @DisplayName("fetch 설정값을 FetchType.LAZY로하면 해당 의존성 객체는 프록시 객체를 만든다. 그리고 사용하는 시점에 쿼리를 통해 가져온다.")
    @Test
    void lazyLoading() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("Team A");
            entityManager.persist(team);

            Member member = new Member();
            member.setName("binghe");
            member.setTeam(team);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // fetch 설정이 LAZY이므로 Member 객체만 가져온다.
            Member findMember = entityManager.find(Member.class, member.getId());

            // findMember.getTeam() 시점에 쿼리가 날라간다.
            System.out.println("===============밑에 쿼리가 날라간다===============");
            System.out.println(findMember.getTeam().getClass());
            System.out.println(findMember.getTeam().getName());
            System.out.println("=============================================");

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
