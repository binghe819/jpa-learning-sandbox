package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("벌크 연산 테스트")
public class BulkOperationTest {

    @DisplayName("벌크 연산을 사용하면 한번에 UPDATE문을 날릴 수 있다.")
    @Test
    void bulkOperation_Update_test() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member1 = new Member();
            member1.setName("binghe");
            member1.setAge(10);
            entityManager.persist(member1);

            Member member2 = new Member();
            member2.setName("mark");
            member2.setAge(26);
            entityManager.persist(member2);

            entityManager.flush();
            entityManager.clear();

            // when
            String query = "update Member m set m.age = 20";
            int resultCount = entityManager.createQuery(query)
                    .executeUpdate();

            assertThat(resultCount).isEqualTo(2);

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스 직접 쿼리를 날린다. (벌크 연산을 먼저 실행하든, 영속성 컨텍스트를 초기화해주어야한다.")
    @Test
    void bulkOperation_Caution_test() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member1 = new Member();
            member1.setName("binghe");
            member1.setAge(0);
            entityManager.persist(member1);

            Member member2 = new Member();
            member2.setName("mark");
            member2.setAge(0);
            entityManager.persist(member2);

            // when
            String query = "update Member m set m.age = 20";
            int resultCount = entityManager.createQuery(query)
                .executeUpdate();

            // then
            assertThat(resultCount).isEqualTo(2);

            // 변경 요청을 했지만, DB에는 반영이 안된 것을 알 수 있다.
            // 벌크 연산은 영속성 컨텍스트와 관계없이 쿼리를 날리기 때문이다.
            // en.clear()를 해주면 변경된 값을 잘 가지고 오게 된다.
            Member findMember1 = entityManager.find(Member.class, member1.getId());
            assertThat(findMember1.getAge()).isNotEqualTo(20);

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
