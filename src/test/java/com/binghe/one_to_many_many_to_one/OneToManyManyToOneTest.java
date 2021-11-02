package com.binghe.one_to_many_many_to_one;

import com.binghe.template.EntityManagerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("N : N의 한계 극복 테스트")
public class OneToManyManyToOneTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("N : N의 한계를 극복하기 위해 1 : N + N : 1로 구성하는 예시")
    @Test
    void test() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            tx.commit();
        }));
    }
}
