package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("TypeQuery와 Query 테스트 (차이점)")
public class TypeQueryAndQueryTest {

    @DisplayName("TypeQuery - 반환 타입이 명확할 때 사용한다.")
    @Test
    void typeQuery() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setName("binghe");
            member.setAge(10);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            TypedQuery<Member> query = entityManager.createQuery("select m from Member as m", Member.class);

            // then
            assertThat(query.getResultList().size()).isEqualTo(1);
            assertThat(query.getSingleResult().getName()).isEqualTo("binghe");
            assertThat(query.getSingleResult().getAge()).isEqualTo(10);

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("Query - 반환 타입이 명확하지 않을 때 사용한다.")
    @Test
    void query() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setName("binghe");
            member.setAge(10);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            Query query = entityManager.createQuery("select m from Member as m");

            // then
            Member findMember = (Member) query.getSingleResult();
            assertThat(findMember.getName()).isEqualTo(member.getName());
            assertThat(findMember.getAge()).isEqualTo(member.getAge());

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
