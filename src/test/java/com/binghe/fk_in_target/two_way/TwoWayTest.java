package com.binghe.fk_in_target.two_way;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.template.EntityManagerTemplate;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("1 : 1 대상 테이블 양방향 테스트")
public class TwoWayTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("대상 테이블이 외래키 관리인이 된다. -> Locker(주인)에 Member를 세팅해줘야 쿼리가 날라간다.")
    @Test
    void twoWay() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            Member member = new Member();
            member.setName("binghe");
            entityManager.persist(member);

            Locker locker = new Locker();
            locker.setName("binghe locker");
            locker.setMember(member);
            entityManager.persist(locker);

            entityManager.flush();
            entityManager.clear();

            // when
            Locker findLocker = entityManager.find(Locker.class, locker.getId());
            Member findMember = findLocker.getMember();

            // then
            assertThat(findLocker.getName()).isEqualTo("binghe locker");
            assertThat(findMember.getName()).isEqualTo("binghe");

            tx.commit();
        }));
    }
}
