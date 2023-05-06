package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.template.EntityManagerTemplate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("@MappedSuperClass 테스트")
public class MappedSuperClassTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("테이블이 어떻게 생성되나 확인하는 테스트")
    @Test
    void mappedSuperClass() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            Member member = new Member();
            member.setName("binghe");
            member.setCreatedBy("mark");
            member.setCreatedAt(LocalDateTime.now());

            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            Member findMember = entityManager.find(Member.class, member.getId());
            assertThat(findMember.getName()).isEqualTo("binghe");
            assertThat(findMember.getCreatedBy()).isEqualTo("mark");

            tx.commit();
        }));
    }

}
