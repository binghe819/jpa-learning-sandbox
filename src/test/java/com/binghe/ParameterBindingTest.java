package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("파라미터 바인딩")
public class ParameterBindingTest {

    @DisplayName("이름 기준")
    @Test
    void parameterBinding_ByName() {
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
            TypedQuery<Member> query = entityManager.createQuery("select m from Member as m where m.name=:name", Member.class)
                .setParameter("name", "binghe");

            // then
            Member findMember = query.getSingleResult();
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

    @DisplayName("순서 기준 - 추천하지 않음 (순서는 추후에도 바뀔 수 있기 때문)")
    @Test
    void parameterBinding_ByOrder() {
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
            TypedQuery<Member> query = entityManager.createQuery("select m from Member as m where m.name=?1", Member.class)
                .setParameter(1, "binghe");

            // then
            Member findMember = query.getSingleResult();
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
