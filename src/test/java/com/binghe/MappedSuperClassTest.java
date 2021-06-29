package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("@MappedSuperClass 테스트")
public class MappedSuperClassTest {

    @DisplayName("테이블이 어떻게 생성되나 확인하는 테스트")
    @Test
    void mappedSuperClass() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
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
        } catch (Exception e) {
            System.out.println("Error!!! " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

}
