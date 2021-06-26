# JPA 놀이터 - 영속성 컨텍스트

<br>

## 테스트 내용
* [영속성 컨텍스트 기능](./src/test/java/com/binghe/PersistenceContextTest.java)
    * 1차 캐싱
    * 동일성 보장
    * 트랜잭션을 지원하는 쓰기 지연 (쓰기 지연 SQL 저장소)
    * 더티체킹

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
