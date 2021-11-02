package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.binghe.template.EntityManagerTemplate;
import javax.persistence.EntityManager;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("프록시 테스트 (연관관계는 EAGER)")
public class ProxyTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("em.find()는 데이터 베이스를 통해서 실제 엔티티 객체를 조회한다. -> 진짜 객체를 주기 위해, 바로 DB 쿼리가 날라간다. (EAGER)")
    @Test
    void find() {
        entityManagerTemplate.execute((entityManager, tx) -> {
            // given
            Member member = new Member();
            member.setName("binghe");

            entityManager.persist(member);

            flushAndClear(entityManager);

            // when
            // find는 join을 통해서 한번에 모든 데이터를 가져온다. (의존되는 객체의 데이터도 한번에 가져온다.)
            Member findMember = entityManager.find(Member.class, member.getId());
            System.out.println("========== 이 시점에 이미 쿼리가 날라갔다. (EAGER면 JOIN) ==========");

            // then
            assertThat(findMember.getId()).isEqualTo(member.getId());
            assertThat(findMember.getName()).isEqualTo("binghe");

            tx.commit();
        });
    }

    @DisplayName("em.getReference()는 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체를 조회한다. -> 가짜 객체를 반환하고, 해당 객체의 속성이 사용될 때 쿼리가 날라간다.")
    @Test
    void getReference() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            Member member = new Member();
            member.setName("binghe");

            entityManager.persist(member);

            flushAndClear(entityManager);

            // when
            // getReference를 호출할 때는 가짜 객체(프록시)를 만들고, 쿼리를 날리지 않는다.
            Member findMember = entityManager.getReference(Member.class, member.getId());
            System.out.println("하이버네이트가 만들어주는 가짜 객체: " + findMember.getClass());
            System.out.println("========== 이 시점엔 쿼리를 날리지 않는다. ==========");

            // then
            // getReference는 해당 속성이나 객체가 사용될 때 쿼리를 날린다.
            assertThat(findMember.getId()).isEqualTo(member.getId());
            System.out.println("========== 이때까진 쿼리가 날라가지 않는다. (프록시여도 id는 가지고 있기 때문) ==========");
            assertThat(findMember.getName()).isEqualTo("binghe"); // getName은 내부 속성을 사용하는 것이므로, 이때 프록시 객체가 초기화된다.
            System.out.println("========== 이 시점에 쿼리가 날라갔다. ==========");

            tx.commit();
        }));
    }

    @DisplayName("m.getReference()를 통해 가져온 프록시(가짜) 객체는 처음 사용할 때만 영속성 컨텍스트에 초기화 요청을 한다. ")
    @Test
    void getReferenceInitialize() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            Member member = new Member();
            member.setName("binghe");

            entityManager.persist(member);

            flushAndClear(entityManager);

            // when
            Member findMember = entityManager.getReference(Member.class, member.getId());
            System.out.println("하이버네이트가 만들어주는 가짜 객체: " + findMember.getClass());
            System.out.println("========== 이 시점엔 쿼리를 날리지 않는다. ==========");

            // then
            assertThat(findMember.getId()).isEqualTo(member.getId());
            System.out.println("========== 이때까진 쿼리가 날라가지 않는다. (프록시여도 id는 가지고 있기 때문) ==========");
            assertThat(findMember.getName()).isEqualTo("binghe"); // 객체의 속성을 처음 사용할 때이므로, 영속성 컨텍스트에 초기화 요청을 한다.
            System.out.println("========== 이 시점엔 쿼리가 날라갔다 ==========");

            System.out.println(findMember.getName());
            System.out.println("========== 이 시점엔 쿼리가 날라가지 않았다 (1차 캐싱을 통한 재사용) ==========");

            tx.commit();
        }));
    }

    @DisplayName("프록시 객체를 초기화할 때, 프록시 객체가 실제 엔티티로 바뀌는 것이 아니다. 초기화되면 프록시 객체를 통해서 실제 엔티티에 접근하는 것.")
    @Test
    void proxyToTarget() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            Member member = new Member();
            member.setName("binghe");

            entityManager.persist(member);

            flushAndClear(entityManager);

            // when
            Member findMember = entityManager.getReference(Member.class, member.getId());
            Class<? extends Member> beforeInit = findMember.getClass();

            // then
            assertThat(findMember.getId()).isEqualTo(member.getId());
            System.out.println("========== 이때까진 쿼리가 날라가지 않는다. (프록시여도 id는 가지고 있기 때문) ");
            assertThat(findMember.getName()).isEqualTo("binghe"); // 객체의 속성을 처음 사용할 때이므로, 영속성 컨텍스트에 초기화 요청을 한다.
            System.out.println("========== 이 시점엔 쿼리가 날라갔다 ==========");

            Class<? extends Member> afterInit = findMember.getClass();
            assertThat(beforeInit).isSameAs(afterInit); // 프록시 객체를 초기화한다고 새로운 타깃 엔티티로 바뀌는 것이 아니다.

            tx.commit();
        }));
    }

    @DisplayName("프록시 객체는 원본 엔티티를 상속받음, 따라서 타입 체크시 주의해야한다. -> 동일성(==) 비교는 실패한다. 대신 instanceOf를 사용한다.")
    @Test
    void typeComparing() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            Member member1 = new Member();
            member1.setName("binghe");
            entityManager.persist(member1);

            Member member2 = new Member();
            member2.setName("mark");
            entityManager.persist(member2);

            flushAndClear(entityManager);

            // then
            // find의 경우는 타입에 대한 == 비교가 가능하다.
            Member m1 = entityManager.find(Member.class, member1.getId());
            Member m2 = entityManager.find(Member.class, member2.getId());

            assertThat(m1.getClass()).isSameAs(m2.getClass());

            flushAndClear(entityManager);

            // then
            // getReference의 경우는 타입에 대한 == 비교가 불가능하다. instanceOf를 사용해야 함.
            // 프록시 객체가 상속을 받기 때문이다.
            Member pm1 = entityManager.find(Member.class, member1.getId()); // 실제 객체
            Member pm2 = entityManager.getReference(Member.class, member2.getId()); // 프록시 객체

            assertThat(pm1.getClass()).isNotSameAs(pm2.getClass()); // 상속으로 인해 타입이 같지 않다.
            assertThat(pm1).isNotSameAs(pm2);

            flushAndClear(entityManager);

            // then
            // getReference를 통해 생성되는 프록시의 클래스 정보는 동일하다 (ByteBuddy를 통해 생성함)
            Member pm1_ = entityManager.getReference(Member.class, member1.getId());
            Member pm2_ = entityManager.getReference(Member.class, member2.getId());

            assertThat(pm1_.getClass()).isSameAs(pm2_.getClass());
            assertThat(pm1_).isNotSameAs(pm2_);

            tx.commit();
        }));
    }

    @DisplayName("영속성 컨텍스트에 프록시 객체가 이미 있다면, 같은 id에 대한 find의 결과는 저장된 프록시 객체를 가져온다. (물론 쿼리를 통해 Target 객체도 가져온다.)")
    @Test
    void findGetProxy() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            Member member1 = new Member();
            member1.setName("binghe");
            entityManager.persist(member1);

            flushAndClear(entityManager);

            // then
            Member pm1 = entityManager.getReference(Member.class, member1.getId()); // 프록시 객체
            System.out.println("========== 이때까진 쿼리가 날라가지 않는다. (프록시여도 id는 가져고 있기 때문) ==========");
            Member pm2 = entityManager.find(Member.class, member1.getId()); // find를 통해 엔티티 조회
            System.out.println("========== 이 시점에 쿼리가 날라간다 (프록시에 대한 Target을 가져온다) ==========");

            assertThat(pm1.getClass()).isSameAs(pm2.getClass()); // 프록시 객체로 인해 타입이 같다.
            assertThat(pm1).isSameAs(pm2); // 프록시 객체를 그대로 가져온 것을 볼 수 있다.

            tx.commit();
        }));
    }

    @DisplayName("영속성 컨텍스트에 찾는 엔티티가 이미 있으면 getReference를 호출해도 실제 엔티티를 반환한다.")
    @Test
    void getRealEntityWhenCached() {
        entityManagerTemplate.execute((entityManager, tx) -> {
            // given
            Member member = new Member();
            member.setName("binghe");
            entityManager.persist(member);

            flushAndClear(entityManager);

            // when
            Member findMember1 = entityManager.find(Member.class, member.getId()); // 영속성 컨텍스트에 저장.
            System.out.println("========== 이 시점엔 쿼리가 날라갔다 (find) ==========");
            Member findMember2 = entityManager.getReference(Member.class, member.getId()); // getReference시 영속성 컨텍스트에서 가져옴.
            System.out.println("========== find후 이 시점까지 쿼리가 날라가지 않는다. (1차 캐싱) ==========");

            // then
            assertThat(findMember1).isSameAs(findMember2); // 같은 객체 (캐싱)
            assertThat(findMember1.getClass()).isSameAs(findMember2.getClass()); // 같은 타입

            tx.commit();
        });
    }

    @DisplayName("JPA는 한 트랜잭션 내에서 READ REFEATABLE을 기본으로 사용하기 때문에, 같은 트랜잭션 안에선 == 비교가 true가 나와야한다. -> 그러므로 프록시 객체도 동일하게 캐시된다.")
    @Test
    void getSameProxyEntity() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            Member member = new Member();
            member.setName("binghe");
            entityManager.persist(member);

            flushAndClear(entityManager);

            // when
            Member findMember1 = entityManager.getReference(Member.class, member.getId());
            Member findMember2 = entityManager.getReference(Member.class, member.getId());
            System.out.println("========== 이 시점까지 쿼리가 날라가지 않았다. (프록시여도 id는 가지고 있기 때문) ==========");

            // then
            // 프록시 객체도 동일하게 캐싱된다.
            assertThat(findMember1).isSameAs(findMember2);
            assertThat(findMember1.getClass()).isSameAs(findMember2.getClass());

            tx.commit();
        }));
    }

    @DisplayName("영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화하면 문제가 발생한다. -> LazyInitializationException")
    @Test
    void detachedInitialization() {
        entityManagerTemplate.execute(((entityManager, tx) -> {
            // given
            Member member1 = new Member();
            member1.setName("binghe");
            entityManager.persist(member1);

            flushAndClear(entityManager);

            // when
            Member refMember = entityManager.getReference(Member.class, member1.getId());
            System.out.println("refMember = " + refMember.getClass()); // Proxy

            entityManager.detach(refMember); // 준영속 상태로 전환.
//            entityManager.clear();

            assertThatCode(() -> {
                refMember.getName();
            }).isInstanceOf(LazyInitializationException.class);
            // no Session 문제로 인해 불가능한 것. (쉽게 말해 refMember를 관리하는 EntityManager가 없기 때문)

            tx.commit();
        }));
    }

    private void flushAndClear(EntityManager entityManager) {
        entityManager.flush();
        entityManager.clear();
    }
}
