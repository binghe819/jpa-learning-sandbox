package com.binghe.fk_in_primary.one_way;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.template.EntityManagerTemplate;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("1 : 1 주 테이블 단방향 테스트")
public class OneWayTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("1 : 1 관계에선 @OneToOne + @JoinColumn을 가진 객체가 연관관계의 주인이 된다.")
    @Test
    void oneWay() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
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

            assertThat(findLocker.getName()).isEqualTo(locker.getName());

            tx.commit();
        }));
    }
}
