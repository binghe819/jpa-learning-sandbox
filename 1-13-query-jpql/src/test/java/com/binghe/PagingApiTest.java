package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("페이징 API 테스트")
public class PagingApiTest {

    @DisplayName("페이징 API - 몇 번째부터 몇 개 까지 가져올 지 쉽게 설정할 수 있다.")
    @Test
    void pagine_HelloWorld() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            for (int i = 0; i < 50; i++) {
                Member member = new Member();
                member.setName("binghe " + i);
                member.setAge(i);
                entityManager.persist(member);
            }

            entityManager.flush();
            entityManager.clear();

            // when
            List<Member> result = entityManager.createQuery("select m from Member m order by m.age desc", Member.class)
                .setFirstResult(10)
                .setMaxResults(10)
                .getResultList();

            // then
            assertThat(result.size()).isEqualTo(10);
            for (Member member : result) {
                System.out.println(member.getName() + ": " + member.getAge());
            }

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
