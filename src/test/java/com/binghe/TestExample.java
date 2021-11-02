package com.binghe;

import com.binghe.template.EntityManagerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestExample {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("테스트 예시")
    @Test
    void createQuery_Empty_ReturnEmptyList() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            tx.commit();
        }));
    }
}
