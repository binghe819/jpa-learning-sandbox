package com.binghe.one_way;

import com.binghe.template.EntityManagerTemplate;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("N : N 단방향 테스트")
public class OneWayTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("DB 테이블 생성 테스트용 - N : N은 연결 테이블을 만든다.")
    @Test
    void oneWay() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            tx.commit();
        }));
    }

}
