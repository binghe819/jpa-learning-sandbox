package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("영속성 컨텍스트 테스트 (리팩토링 필요)")
public class PersistenceContextTest {

    @DisplayName("1차 캐싱 - JPA는 1차 캐싱을 통해 동일 트랜잭션 안에서 컬렉션처럼 동작한다.")
    @Test
    void firstLevelCache() {
        // 로딩 시점에 딱 한번 실행. (DB마다 하나)
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        // DB와 커넥션을 해야하는 트랜잭션단위로 EntityManager를 만들어줘야한다. (DB요청마다 하나)
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setId(100L);
            member.setName("binghe");

            // when
            entityManager.persist(member); // 1차 캐시에 저장.

            Member findMember1 = entityManager.find(Member.class, 100L); // 1차 캐시에서 조회. (쿼리를 찌르지 않는다.)

            // then
            assertThat(member).isSameAs(findMember1); // 동일성 보장

            tx.commit(); // flush + 트랜잭션 commit
        } catch (Exception e) {
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("동일성 보장 - JPA는 영속성 컨텍스트(정확히는 1차 캐시)를 통해 동일성을 보장한다.")
    @Test
    void identity() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setId(100L); //  AUTOINCREMENT 전략 사용하지 않음
            member.setName("binghe");

            // when
            entityManager.persist(member); // 1차 캐시에 저장.
            Member findMember1 = entityManager.find(Member.class, member.getId()); // 1차 캐시에서 조회
            Member findMember2 = entityManager.find(Member.class, member.getId()); // 1차 캐시에서 조회

            // then
            assertThat(member).isSameAs(findMember1);
            assertThat(findMember1).isSameAs(findMember2);

            tx.commit(); // flush + 트랜잭션 commit
        } catch (Exception e) {
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("동일성 보장 - JPA는 DB로부터 엔티티를 가져오면 1차 캐싱에 저장하고, 동일성을 보장한다.")
    @Test
    void identityByDB() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setId(100L);
            member.setName("binghe");

            // when
            entityManager.persist(member); // 1차 캐시에 저장.

            entityManager.flush();
            entityManager.clear();

            Member findMember1 = entityManager.find(Member.class, member.getId()); // 1차 캐시에서 조회
            Member findMember2 = entityManager.find(Member.class, member.getId()); // 1차 캐시에서 조회

            // then
            assertThat(member).isNotSameAs(findMember1); // 중요 포인트 (flush하고 clear 했으므로 같지 않다.)
            assertThat(findMember1).isSameAs(findMember2);

            tx.commit(); // flush + 트랜잭션 commit
        } catch (Exception e) {
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("쓰기 지연 SQL 저장소 - JPA는 1차 캐시에 담긴 엔티티를 바로 DB에 저장하지 않고, 쓰기 지연 저장소에 저장하고 flush할 때 한번에 저장한다.")
    @Test
    void sqlStorage() {
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
            System.out.println("================ 쓰기 지연 SQL 저장소 테스트 - 아직 쿼리 안 날림. ================");
            // 여기까지 INSERT SQL을 DB에 보내지 않는다.

            // 커밋하는 순간 DB에 INSERT SQL을 보낸다.
            tx.commit(); // flush + 트랜잭션 commit
            System.out.println("================ 쓰기 지연 SQL 저장소 테스트 - 쿼리 날림 ================");
        } catch (Exception e) {
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("더티체킹 - JPA는 엔티티 수정시 변경을 감지하여 영속적으로 수정해준다.")
    @Test
    void dirtyChecking() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given - Member를 DB에 저장
            Member member = new Member();
            member.setId(100L);
            member.setName("binghe");

            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            Member findMember = entityManager.find(Member.class, 100L);
            findMember.setName("update binghe");

            System.out.println("================ UPDATE 쿼리를 날린다. ================");

            entityManager.flush();
            entityManager.clear();

            Member finalFindMember = entityManager.find(Member.class, 100L);

            assertThat(finalFindMember.getName()).isEqualTo("update binghe");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("기본 키 전략 - 기본 키 전략을 IDENTITY로 하면 persist할 때 바로 SQL이 날라간다.")
    @Test
    void primarykeyByIdentity() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            AutoIncrementMember member = new AutoIncrementMember();
            member.setName("binghe");

            // when
            entityManager.persist(member); // id값을 얻기위해 insert 쿼리를 바로 날린다.
            System.out.println("================ IDENTITY 전략 테스트 - 트랜잭션 전에 쿼리 이미 날림. ================");

            // then
            assertThat(member.getId()).isNotNull();

            tx.commit(); // flush + 트랜잭션 commit
        } catch (Exception e) {
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }
}
