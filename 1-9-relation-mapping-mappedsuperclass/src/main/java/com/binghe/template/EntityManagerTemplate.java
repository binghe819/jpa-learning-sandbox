package com.binghe.template;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class EntityManagerTemplate {

    public void execute(BusinessExecutor businessExecutor) {
        // 로딩 시점에 딱 한번 실행. (DB마다 하나)
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        // DB와 커넥션을 해야하는 트랜잭션단위로 EntityManager를 만들어줘야한다. (DB요청마다 하나)
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            businessExecutor.executeBusiness(entityManager, tx);
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

}
