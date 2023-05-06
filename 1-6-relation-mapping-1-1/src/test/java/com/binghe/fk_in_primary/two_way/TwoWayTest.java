package com.binghe.fk_in_primary.two_way;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.template.EntityManagerTemplate;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("1 : 1 주 테이블 양방향 테스트")
public class TwoWayTest {

    private EntityManagerTemplate entityManagerTemplate;
    private Locker locker1;
    private Locker locker2;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
        locker1 = new Locker();
        locker1.setName("Locker 1");
        locker2 = new Locker();
        locker2.setName("Locker 2");
    }

    @DisplayName("연관관계 주인이 아닌 엔티티(Locker)를 통해 조회는 가능하다. (N:1 양방향과 동일하게 동작한다)")
    @Test
    void twoWay() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            entityManager.persist(locker1);

            Member member = new Member();
            member.setName("binghe");
            member.setLocker(locker1);
            entityManager.persist(member);

            flushAndClear(entityManager);

            // then
            Locker findLocker = entityManager.find(Locker.class, locker1.getId());
            Member findMember = findLocker.getMember();

            assertThat(findMember.getName()).isEqualTo("binghe");

            tx.commit();
        }));
    }

    @DisplayName("연관관계 주인이 아닌 엔티티(Locker)를 수정해도 DB엔 쿼리가 날라가지 않는다 (N:1 양방향과 동일하게 동작한다)")
    @Test
    void update_NonOwner() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            entityManager.persist(locker1);

            Member member = new Member();
            member.setName("binghe");
            member.setLocker(locker1);
            entityManager.persist(member);

            flushAndClear(entityManager);

            // when
            Locker savedLocker = entityManager.find(Locker.class, locker1.getId());
            savedLocker.setMember(null);

            flushAndClear(entityManager);

            // then
            Locker findLocker = entityManager.find(Locker.class, locker1.getId());
            assertThat(findLocker.getMember()).isNotNull();
            assertThat(findLocker.getMember())
                .extracting("name")
                .isEqualTo(member.getName());
            tx.commit();
        }));
    }

    @DisplayName("연관관계 주인 엔티티(Member)를 수정하면 DB와 동기화한다. (N:1 양방향과 동일하게 동작한다)")
    @Test
    void update_Owner() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            entityManager.persist(locker1);
            entityManager.persist(locker2);

            Member member = new Member();
            member.setName("binghe");
            member.setLocker(locker1);
            entityManager.persist(member);

            flushAndClear(entityManager);

            // when
            Member savedMember = entityManager.find(Member.class, member.getId());
            Locker savedLocker2 = entityManager.find(Locker.class, locker2.getId());
            savedMember.setLocker(savedLocker2);

            flushAndClear(entityManager);

            // then
            Member findMember = entityManager.find(Member.class, member.getId());
            assertThat(findMember.getLocker())
                .extracting("name")
                .isEqualTo(locker2.getName());
            tx.commit();
        }));
    }

    private void flushAndClear(EntityManager entityManager) {
        entityManager.flush();
        entityManager.clear();
    }
}
