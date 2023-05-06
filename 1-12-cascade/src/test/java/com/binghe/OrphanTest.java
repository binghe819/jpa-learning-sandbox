package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.template.EntityManagerTemplate;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("고아 객체 테스트")
public class OrphanTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("자식 엔티티를 컬렉션에서 제거하면 쿼리가 날라간다. (연관관계 주인이 아닌대로 쿼리를 날릴 수 있다.)")
    @Test
    void orphanTest() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
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
            System.out.println("===============고아 객체 (orphan) 설정을 해주었으므로, 자식을 제거하면 실제 DB에 delete 쿼리가 날아간다..===============");
            findParent.getChilds().remove(0); // 0번째 자식 객체를 고아상태로 변경.

            entityManager.flush();
            entityManager.clear();

            // then
            Parent resultParent = entityManager.find(Parent.class, parent.getId());
            assertThat(resultParent.getChilds().size()).isEqualTo(1);

            tx.commit();
        }));
    }
}
