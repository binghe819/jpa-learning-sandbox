# JPA 놀이터 - 영속성 컨텍스트

<br>

## 테스트 내용
* [영속성 컨텍스트 기능](./src/test/java/com/binghe/PersistenceContextTest.java)
    * 1차 캐싱
    * 동일성 보장
    * 트랜잭션을 지원하는 쓰기 지연 (쓰기 지연 SQL 저장소)
    * 더티체킹
* [Flush](./src/test/java/com/binghe/FlushTest.java)

<br>

## 영속성 컨텍스트란?
* 영속성 컨텍스트(Persistence Context)란
    * **엔티티를 관리하고 영속화(영구 저장)시키는 환경**
        * ex. `EntityManager.persist(entity)`: DB가 아닌 영속성 컨텍스트에 저장한다는 의미.
    * 논리적인 개념이므로, 눈에 보이지 않는다.
        * **엔티티 매니저를 통해서 접근하기 때문.**
* 애플리케이션과 DB 사이에서 엔티티를 관리해주는 환경.

<br>

## 영속성 컨텍스트 기능
* 1차 캐싱
* 동일성 보장
* 트랜잭션을 지원하는 쓰기 지연 (쓰기 지연 SQL 저장소)
* 더티체킹

<br>

## 플러쉬
* **플러시란?**
    * **영속성 컨텍스트의 변경내용(엔티티의 변경 -> 등록, 수정, 삭제)을 DB에 반영하는 것.**
    * 영속성 컨텍스트에 쌓아뒀던 쿼리들을 DB에 날려주는 것.
    * **보통 트랜잭션 커밋될 때 플러시가 발생한다. (commit 호출시 flush를 호출한 다음에 commit)**
* **플러시 사용시 오해하면 안되는 부분**
    * **플러시는 영속성 컨텍스트를 비우는 것이 아님. (1차 캐시 그대로 남아있는다)**
        * 플러시를 하여 동기화한 다음에 영속성 컨텍스트는 그대로 유지된다.
    * **플러시는 영속성 컨텍스트의 변경내용을 DB에 동기화.**
    * 중요한 점은 트랜잭션이라는 작업 단위가 중요 -> 커밋 직전에만 동기화하면 됨.
* **플러시가 발생하면 발생하면**
    * 변경 감지
    * 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
    * 쓰기 지연 SQL 저장소의 쿼리를 DB에 전송 (등록, 수정, 삭제 쿼리)
* **영속성 컨텍스트 플러시하는 방법**
    * 직접 호출: `em.flush()`
    * 트랜잭션 커밋: 플러시 자동 호출
    * JPQL 쿼리 실행: 플러시 자동 호출
* **플러시 모드 옵션 (`em.setFlushMode(FlushModeType.XXX)`)**
    * `FlushModeType.AUTO`: 커밋이나 쿼리를 실행할 때 플러시 (기본값)
    * `FlushModeType.COMMIT`: 커밋할 때만 플러시

<br>

## 주제
- [JPA 소개](./docs/README.md)
- [영속성 컨텍스트](https://github.com/binghe819/jpa-learning-sandbox/tree/persistence-context)
- 연관관계 매핑
    - [연관관계 주인](https://github.com/binghe819/jpa-learning-sandbox/tree/relation-mapping-owner-of-relationship)
    - [N : 1](https://github.com/binghe819/jpa-learning-sandbox/tree/relation-mapping-N-1)
    - [1 : N](https://github.com/binghe819/jpa-learning-sandbox/tree/relation-mapping-1-N)
    - [1 : 1](https://github.com/binghe819/jpa-learning-sandbox/tree/relation-mapping-1-1)
    - [N : N](https://github.com/binghe819/jpa-learning-sandbox/tree/relation-mapping-N-N)
    - [상속관계 매핑](https://github.com/binghe819/jpa-learning-sandbox/tree/relation-mapping-abstract)
    - [@MappedSuperClass](https://github.com/binghe819/jpa-learning-sandbox/tree/relation-mapping-mappedsuperclass)
- 프록시와 연관관계 관리
    - [프록시](https://github.com/binghe819/jpa-learning-sandbox/tree/proxy)
    - [지연 로딩과 즉시 로딩](https://github.com/binghe819/jpa-learning-sandbox/tree/proxy-lazy-and-eager)
    - [영속성 전이와 고아 객체](https://github.com/binghe819/jpa-learning-sandbox/tree/cascade)
- 객체지향 쿼리 언어
    - [JPQL](https://github.com/binghe819/jpa-learning-sandbox/tree/query-jpql)
    - [fetch join](https://github.com/binghe819/jpa-learning-sandbox/tree/query-fetch-join)
