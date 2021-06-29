package com.binghe.fk_in_primary.two_way;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("1 : 1 주 테이블 양방향 테스트")
public class TwoWayTest {

    @DisplayName("양방향은 동일하게 연관관계 주인이 아니면 조회만 가능하다. -> N:1 양방향과 동일하게 동작한다")
    @Test
    void twoWay() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // given
            Locker locker = new Locker();
            locker.setName("binghe locker");
            entityManager.persist(locker);

            Member member = new Member();
            member.setName("binghe");
            member.setLocker(locker);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // then
            Member findMember = entityManager.find(Member.class, member.getId());
            Locker findLocker = findMember.getLocker();

            assertThat(findLocker.getMember()).isSameAs(findMember);

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
