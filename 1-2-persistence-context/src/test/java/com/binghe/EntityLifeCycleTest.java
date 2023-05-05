package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.template.EntityManagerTemplate;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("엔티티 라이프사이클 테스트")
public class EntityLifeCycleTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("준영속 - 준영속 상태의 엔티티는 DB에 반영되지 않는다. (더티 체킹)")
    @Test
    void detach_someEntity() {
        // given - 특정 엔티티 영속화
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given - 특정 엔티티 영속화
            Member member = new Member();
            member.setId(1L);
            member.setName("binghe");

            entityManager.persist(member);
            flushAndClear(entityManager);

            // when
            Member savedMember = entityManager.find(Member.class, 1L);
            entityManager.detach(savedMember);
            savedMember.setName("mark"); // 이때 준영속이므로, 더티체킹이 발생하지 않는다.
            entityManager.flush();

            // then
            Member findMember = entityManager.find(Member.class, 1L);
            assertThat(findMember.getName()).isEqualTo("binghe");
            assertThat(findMember.getName()).isNotEqualTo("mark");
        }));
    }

    private void flushAndClear(EntityManager entityManager) {
        entityManager.flush();
        entityManager.clear();
    }
}
