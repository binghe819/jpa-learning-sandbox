package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("집합과 정렬 테스트")
public class SetAndSortTest {

    private Member member1;
    private Member member2;
    private Member member3;
    private Member member4;

    @BeforeEach
    void setUp() {
        member1 = new Member();
        member1.setName("binghe");
        member1.setAge(26);

        member2 = new Member();
        member2.setName("mark");
        member2.setAge(21);

        member3 = new Member();
        member3.setName("byeonghwa");
        member3.setAge(15);

        member4 = new Member();
        member4.setName("byeonghwa");
        member4.setAge(17);
    }

    @DisplayName("기본적으로 count, sum, avg, max, min 등을 지원한다.")
    @Test
    void sum_avg_max_min() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            entityManager.persist(member1);
            entityManager.persist(member2);
            entityManager.persist(member3);

            entityManager.flush();
            entityManager.clear();

            // when
            TypedQuery<Long> count = entityManager.createQuery("select COUNT(m) from Member as m", Long.class);
            TypedQuery<Long> sum = entityManager.createQuery("select SUM(m.age) from Member as m", Long.class);
            TypedQuery<Double> avg = entityManager.createQuery("select AVG(m.age) from Member as m", Double.class);
            TypedQuery<Integer> max = entityManager.createQuery("select MAX(m.age) from Member as m", Integer.class);
            TypedQuery<Integer> min = entityManager.createQuery("select MIN(m.age) from Member as m", Integer.class);

            assertThat(count.getSingleResult()).isEqualTo(3);
            assertThat(sum.getSingleResult()).isEqualTo(62);
            assertThat(avg.getSingleResult()).isEqualTo(20.666666666666668);
            assertThat(max.getSingleResult()).isEqualTo(26);
            assertThat(min.getSingleResult()).isEqualTo(15);

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("GROUP BY")
    @Test
    void groupBy() {

    }

    @DisplayName("기본적으로 ORDER BY를 지원한다.")
    @Test
    void orderBy() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            entityManager.persist(member1);
            entityManager.persist(member2);
            entityManager.persist(member3);

            entityManager.flush();
            entityManager.clear();

            // when
            TypedQuery<Member> query = entityManager.createQuery("select m from Member as m order by m.age DESC ", Member.class);
            List<Member> result = query.getResultList();

            // then
            assertThat(result)
                .extracting("age")
                .containsExactly(26, 21, 15);

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
