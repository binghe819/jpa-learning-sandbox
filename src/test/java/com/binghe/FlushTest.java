package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * - 플러쉬의 기능: 영속성 컨텍스트의 변경내용을 DB에 반영하는 것. (동기화)
 * - 플러쉬가 발생시: 변경 감지 -> 수정된 엔티티 쓰기 지연 SQL에 등록 -> 쓰기 지연 SQL 저장소의 쿼리들을 DB에 전송 (등록, 수정, 삭제 쿼리)
 * - 플러시를 호출하는 방법: (1) 직접 호출 (2) 트랜잭션 커밋 (3) JPQL 쿼리 실행
 */
@DisplayName("플러시 테스트")
public class FlushTest {

    @DisplayName("플러시의 오해 (직접 호출) - 플러시는 1차 캐시를 지우지 않는다. (영속성 컨텍스트에 쌓아둔 쿼리들을 DB에 날릴뿐)")
    @Test
    void doNotClearCache() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member1 = new Member();
            member1.setId(100L);
            member1.setName("binghe");
            Member member2 = new Member();
            member2.setId(150L);
            member2.setName("younghan");

            // when
            entityManager.persist(member1);
            entityManager.persist(member2);
            entityManager.flush();
            System.out.println("================ flush하면 쓰기 SQL 저장소에 있는 쿼리문을 날린다. ================");

            // then
            // flush를 해도 1차 캐시에서 엔티티를 가져온다. 즉, 1차 캐시 저장소는 비우지 않는다.
            Member findMember1 = entityManager.find(Member.class, 100L);
            Member findMember2 = entityManager.find(Member.class, 150L);
            assertThat(findMember1).isSameAs(member1);
            assertThat(findMember2).isSameAs(member2);

            System.out.println("================ commit ================");
            tx.commit(); // 이땐 쓰기 SQL 저장소에 아무것도 없으므로 쿼리를 날리지 않는다.
        } catch (Exception e) {
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("플러쉬와 트랜잭션 (트랜잭션 호출) - commit 호출시 flush를 호출한 다음에 commit한다.")
    @Test
    void transactionWithFlush() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member1 = new Member();
            member1.setId(100L);
            member1.setName("binghe");
            Member member2 = new Member();
            member2.setId(150L);
            member2.setName("younghan");

            // when
            entityManager.persist(member1);
            entityManager.persist(member2);
            System.out.println("================ commit하기 전엔 flush가 발생하지 않는다. (GeneratedValue사용하지 않을때) ================");

            tx.commit();
            System.out.println("================ commit할 때 flush가 먼저 발생하고 commit이 발생한다. ================");
        } catch (Exception e) {
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("플러쉬와 JPQL (JPQL 호출) - JPQL 쿼리를 실행하면 그 즉시 flush가 된다.")
    @Test
    void jpql() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member1 = new Member();
            member1.setId(100L);
            member1.setName("binghe");
            Member member2 = new Member();
            member2.setId(150L);
            member2.setName("younghan");

            // when
            entityManager.persist(member1); // 쿼리 안 날라감
            entityManager.persist(member2); // 쿼리 안 날라감

            System.out.println("================ 여기까지는 쿼리가 안 날라간다. (AUTOINCREMENT를 뺀 경우) ================");

            Query query = entityManager.createQuery("select m from Member as m");

            System.out.println("================ JPQL을 호출하는 순간 1차 캐싱에서 가져오지 않고 flush. ================");

            List<Member> findMembers = query.getResultList();
            assertThat(findMembers)
                .containsExactly(member1, member2);

            // then
            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!!! ");
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }
}
