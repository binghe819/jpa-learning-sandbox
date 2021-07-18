package com.binghe.two_way;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("N : N 양방향 테스트")
public class TwoWayTest {

    @DisplayName("DB 테이블 생성 테스트용 - N : N은 연결 테이블을 만든다.")
    @Test
    void twoWay() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!!! " + e.getMessage());
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }
}
