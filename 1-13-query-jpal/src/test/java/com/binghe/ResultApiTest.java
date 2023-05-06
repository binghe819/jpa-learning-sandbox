package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("결과 조회 API")
public class ResultApiTest {

    @DisplayName("getResultList() - 결과가 없으면 빈 리스트를 반환한다.")
    @Test
    void getResultList_EmptyList() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // when
            TypedQuery<Member> query = entityManager.createQuery("select m from Member as m", Member.class);

            // then
            List<Member> result = query.getResultList();
            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(0);

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("getSingleResult() - 결과가 하나도 없으면 NoResultException을 던진다.")
    @Test
    void getSingleResult_NoData_ThrowNoResultException() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // when
            TypedQuery<Member> query = entityManager.createQuery("select m from Member as m", Member.class);

            // then
            assertThatThrownBy(() -> query.getSingleResult())
                .isInstanceOf(NoResultException.class);

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("getSingleResult() - 결과가 하나 이상이면 NonUniqueResultException을 던진다.")
    @Test
    void getSingleResult_MoreThanOneData_ThrowNonUniqueResultException() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member1 = new Member();
            member1.setName("binghe");
            member1.setAge(15);
            entityManager.persist(member1);

            Member member2 = new Member();
            member2.setName("mark");
            member2.setAge(20);
            entityManager.persist(member2);

            entityManager.flush();
            entityManager.clear();

            // when
            TypedQuery<Member> query = entityManager.createQuery("select m from Member as m", Member.class);

            // then
            assertThatThrownBy(() -> query.getSingleResult())
                .isInstanceOf(NonUniqueResultException.class);

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
