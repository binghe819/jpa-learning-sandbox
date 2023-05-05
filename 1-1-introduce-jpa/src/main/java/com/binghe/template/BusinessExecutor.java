package com.binghe.template;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * JPA의 핵심 로직을 수행하는 람다식
 */
@FunctionalInterface
public interface BusinessExecutor {
    void executeBusiness(EntityManager entityManager, EntityTransaction tx);
}
