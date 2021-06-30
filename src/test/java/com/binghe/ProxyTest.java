package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("프록시 테스트")
public class ProxyTest {

    @DisplayName("em.find()는 데이터 베이스를 통해서 실제 엔티티 객체를 조회한다. -> 진짜 객체를 주기 위해, 바로 DB 쿼리가 날라간다.")
    @Test
    void find() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setName("binghe");

            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            // find는 join을 통해서 한번에 모든 데이터를 가져온다. (의존되는 객체의 데이터도 한번에 가져온다.)
            Member findMember = entityManager.find(Member.class, member.getId());

            // then
            assertThat(findMember.getId()).isEqualTo(member.getId());
            assertThat(findMember.getName()).isEqualTo("binghe");

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("em.getReference()는 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체를 조회한다. -> 가짜 객체를 반환하고, 해당 객체의 속성이 사용될 때 쿼리가 날라간다.")
    @Test
    void getReference() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setName("binghe");

            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            // getReference를 호출할 때는 가짜 객체(프록시)를 만들고, 쿼리를 날리지 않는다.
            Member findMember = entityManager.getReference(Member.class, member.getId());
            System.out.println("하이버네이트가 만들어주는 가짜 객체: " + findMember.getClass());
            System.out.println("========== 이 시점엔 쿼리를 날리지 않는다. ==========");

            // then
            // getReference는 해당 속성이나 객체가 사용될 때 쿼리를 날린다.
            assertThat(findMember.getId()).isEqualTo(member.getId());
            System.out.println("========== 이 시점에 쿼리를 날린다. ==========");
            assertThat(findMember.getName()).isEqualTo("binghe"); // getName은 내부 속성을 사용하는 것이므로, 이때 프록시 객체가 초기화된다.

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("m.getReference()를 통해 가져온 프록시(가짜) 객체는 처음 사용할 때만 영속성 컨텍스트에 초기화 요청을 한다. ")
    @Test
    void getReferenceInitialize() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setName("binghe");

            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            Member findMember = entityManager.getReference(Member.class, member.getId());
            System.out.println("하이버네이트가 만들어주는 가짜 객체: " + findMember.getClass());
            System.out.println("========== 이 시점엔 쿼리를 날리지 않는다. ==========");

            // then
            assertThat(findMember.getId()).isEqualTo(member.getId());
            System.out.println("========== 이 시점에 쿼리를 날린다. (초기화) ==========");
            assertThat(findMember.getName()).isEqualTo("binghe"); // 객체의 속성을 처음 사용할 때이므로, 영속성 컨텍스트에 초기화 요청을 한다.
            System.out.println("========== 이 시점엔 쿼리를 날리지 않는다. (재사용) ==========");
            System.out.println(findMember.getName());

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
