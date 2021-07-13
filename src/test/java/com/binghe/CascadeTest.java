package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("영속성 전이 (CASCADE) 테스트")
public class CascadeTest {

    @DisplayName("Parent를 영속화하면 내부의 엔티티(Child)가 자동으로 영속화된다.")
    @Test
    void cascadeTest() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            Child child1 = new Child();
            child1.setName("child1");
            Child child2 = new Child();
            child2.setName("child2");

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            // Parent만 영속화하면 안에 들어있는 child도 자동으로 영속화된다.
            entityManager.persist(parent);

            entityManager.flush();
            entityManager.clear();

            Parent findParent = entityManager.find(Parent.class, parent.getId());

            assertThat(findParent.getChilds())
                .extracting("name")
                .containsExactly("child1", "child2");

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
