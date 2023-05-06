package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProjectionTest {

    @DisplayName("엔티티 프로젝션 - SELECT 대상이 되는 결과 엔티티는 모두 영속성 컨텍스트에 의해 관리된다.")
    @Test
    void project_RelationOwnerEntity_ManagedByPersistenceContext() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setName("binghe");
            member.setAge(10);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            Member findMember = entityManager.createQuery("select m from Member m", Member.class)
                .getSingleResult();

            findMember.setAge(20);

            entityManager.flush();
            entityManager.clear();

            // then
            Member result = entityManager.createQuery("select m from Member m", Member.class)
            .getSingleResult();

            assertThat(result.getAge()).isEqualTo(20);

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("엔티티 프로젝션 - 연관관계 주인이 아닌 엔티티를 조회하면 join 쿼리(암묵적)가 날라간다. (동일하게 영속성 컨텍스트에 의해 관리된다.)")
    @Test
    void project_NonRelationOwnerEntity_JoinQuery() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Team team = new Team();
            team.setName("Team A");
            entityManager.persist(team);

            Member member = new Member();
            member.setName("binghe");
            member.setAge(10);
            member.setTeam(team);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            // 묵시적 join
//            Team findTeam = entityManager.createQuery("select t from Member m join m.team t", Team.class)
//                .getSingleResult();
            // 암묵적 join
            Team findTeam = entityManager.createQuery("select m.team from Member m", Team.class)
                .getSingleResult();
            findTeam.setName("Team B");

            entityManager.flush();
            entityManager.clear();

            // then
            Team result = entityManager.createQuery("select m.team from Member m", Team.class)
                .getSingleResult();
            assertThat(result.getName()).isEqualTo("Team B");

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("임베디드 타입 프로젝션 - 임베디드 타입의 값만 쿼리해서 가져온다.")
    @Test
    void embedded() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Address address = new Address();
            address.setCity("seoul");
            address.setStreet("oksu");
            address.setZipcode("40541");

            Order order = new Order();
            order.setAddress(address);
            entityManager.persist(order);

            entityManager.flush();
            entityManager.clear();

            // when
            Address findAddress = entityManager.createQuery("select o.address from Order o", Address.class)
                .getSingleResult();

            // then
            assertThat(findAddress.getCity()).isEqualTo("seoul");

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("스칼라 프로젝션 - Query와 Object[]를 통한 쿼리")
    @Test
    void scala_ByQueryAndObject() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setName("binghe");
            member.setAge(20);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            List<Object[]> resultList = entityManager.createQuery("select m.name, m.age from Member m")
                .getResultList();

            Object[] result = resultList.get(0);
            assertThat(result[0]).isEqualTo(member.getName());
            assertThat(result[1]).isEqualTo(member.getAge());

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error!! -> " + e);
            tx.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    @DisplayName("스칼라 프로젝션 - new 명령어로 조회 (Dto 객체 사용)")
    @Test
    void scala_ByNew() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Member member = new Member();
            member.setName("binghe");
            member.setAge(20);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // when
            List<MemberDto> resultList = entityManager.createQuery("select new com.binghe.MemberDto(m.name, m.age) from Member m", MemberDto.class)
                .getResultList();

            // then
            assertThat(resultList.get(0).getName()).isEqualTo(member.getName());
            assertThat(resultList.get(0).getAge()).isEqualTo(member.getAge());

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
