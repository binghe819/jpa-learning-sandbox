package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("고아 객체 테스트")
public class OrphanTest {

    @DisplayName("자식 엔티티를 컬렉션에서 제거하면 쿼리가 날라간다. (연관관계 주인이 아닌대로 쿼리를 날릴 수 있다.)")
    @Test
    void orphanTest() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            entityManager.persist(parent);

            entityManager.flush();
            entityManager.clear();

            // when
            Parent findParent = entityManager.find(Parent.class, parent.getId());
            assertThat(findParent.getChilds().size()).isEqualTo(2);
            findParent.getChilds().remove(0); // 고아 객체 (orphan)설정을 해주었으므로, delete 쿼리가 날라간다.

            entityManager.flush();
            entityManager.clear();

            // then
            Parent resultParent = entityManager.find(Parent.class, parent.getId());
            assertThat(resultParent.getChilds().size()).isEqualTo(1);

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
