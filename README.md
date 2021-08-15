# JPA 놀이터 - JPQL

<br>

## 학습 테스트 내용
- [집합과 정렬 - count, sum, avg, max, min, group by, order by](./src/test/java/com/binghe/SetAndSortTest.java)
- [TypeQuery, Query](./src/test/java/com/binghe/TypeQueryAndQueryTest.java)
- [결과 조회 API - getSingleResult, getResultList](./src/test/java/com/binghe/ResultApiTest.java)
- [파라미터 바인딩 - 이름 기준, 위치 기준](./src/test/java/com/binghe/ParameterBindingTest.java)
- [프로젝션](./src/test/java/com/binghe/ProjectionTest.java)
- [페이징 API](./src/test/java/com/binghe/PagingApiTest.java)
- [조인 (join)](./src/test/java/com/binghe/JoinTest.java)
- [경로 표현식](./src/test/java/com/binghe/PathExpressionTest.java)

<br>

# 목차

<br>

- [목차](#목차)
- [객체지향 쿼리 언어 소개](#객체지향-쿼리-언어-소개)
- [JPQL이란?](#jpql이란)
- [JPQL 기본 문법과 기능](#jpql-기본-문법과-기능)
- [프로젝션](#프로젝션)
- [페이징 API](#페이징-api)
- [조인](#조인)
- [서브 쿼리](#서브-쿼리)
- [JPQL 타입 표현](#jpql-타입-표현)
- [조건식](#조건식)
- [JPQL 기본 함수](#jpql-기본-함수)
- [경로 표현식](#경로-표현식)

<br>

# 객체지향 쿼리 언어 소개

<br>

**🤔 객체지향 쿼리 언어가 필요한 이유**
* 기존의 JPA는 엔티티 기반의 조회 방법을 지원한다. (엔티티 객체 중심)
    * `EntityManger.find()`
    * 객체 그래프 탐색: `a.getA().getB()`
* 하지만 **문제는 검색 쿼리**다.
    * 만약 나이가 18이상인 회원을 모두 검색하고 싶다면 어떻게 해야할까?
    * 결국 **필요한 데이터만 DB에서 불러오려면 검색 조건이 포함된 SQL이 필요하다.**
    * **JPA는 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색하는 것을 지원한다.**

<br>

**JPA가 지원하는 다양한 쿼리 방법**
* JPQL, JPA Criteria, QueryDSL, 네이티브 SQL, JDBC API직접 사용

<br>

# JPQL이란?
🤔 **JPQL이란?**
* JPA는 SQL을 추상화한 JPQL이라는 객제 지향 언어를 제공한다.
* SQL과 문법이 유사하다.
    * SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원

<br>

🤔 **JPQL과 SQL의 차이점**
* **JPQL은 엔티티 객체를 대상으로 쿼리**
* **SQL은 데이터베이스 테이블을 대상으로 쿼리**

<br>

**JPQL 특징**
* **테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리**
* **SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않는다.**
* JPQL을 한마디로 정의하면 **객체 지향 SQL**
* JPQL은 결국 SQL로 변환된다.

<br>

**JPQL의 단점**
* **동적 쿼리를 만들기 힘들다.**
    * `"select m from Member as m where m.name like '%kim'"`
    * 위와 같이 **JPQL은 문자열을 통해 쿼리를 날린다.**
    * **동적 쿼리를 사용하려면 자바 코드에 `if`문을 사용해서 분기별로 다른 쿼리를 날리도록 해야한다.**
* 이러한 **JPQL의 동적 쿼리 단점을 해결한 것이 바로 Criteria와 QueryDSL다**. **(빌더 역할을 하는 대체제)**
    * **마법이 아닌 문자가 아닌 자바코드로 JPQL을 작성할 수 있게 한 것이다.**
    * 대부분은 QueryDSL를 사용한다. Criteria는 유지보수면에서 안 좋다고 한다.

<br>

# JPQL 기본 문법과 기능

<br>

**JPQL 문법 특징**
* `select m from Member as m where m.age > 18`
    * **엔티티와 속성은 대소문자 구분한다. (Member, age등)**
    * **JPQL 키워드는 대소문자를 구분하지 않는다. (SELECT, FROM, where등)**
    * **별칭은 필수다 (ex. `m`). 단, as는 생략 가능하다.**
* 엔티티 대상 쿼리이므로 엔티티 이름을 사용해야 하며, **테이블 이름이 아니다.**

<br>

**집합과 정렬**

```SQL
select
    COUNT(m), // 회원수
    SUM(m.age), // 나이 합
    AVG(m.age), // 평균 나이
    MAX(m.age), // 최대 나이
    MIN(m.age) // 최소 나이
from Member m
```

```SQL
GROUP BY, HAVING
ORDER BY
```
* 위 내용들은 SQL와 동일하게 사용하면 된다.

<br>

**TypeQuery, Query**
* `TypeQuery`: 반환 타입이 명확할 때 사용한다.
    * `TypedQuery<Member> query = entityManager.createQuery("select m from Member as m", Member.class);`
* `Query`: 반환 타입이 명확하지 않을 때 사용한다.
    * `Query query = entityManager.createQuery("select m.name, m.age from Member as m");`

<br>

**결과 조회 API**
* `query.getResultList()`: **결과가 하나 이상일 때**, 리스트 반환.
    * 결과가 없으면 빈 리스트 반환.
* `query.getSingleResult()`: **결과가 정확히 하나**, 단일 객체 반환
    * 결과가 없으면: `javax.persistence.NoResultException`
    * 둘 이상이면: `javax.persistence.NonUniqueResultException`

<br>

> **Spring Data JPA에서는 Optional이나 null로 반환하는 식으로 추상화 되어있다.**
> * 물론 내부적으로 `try-catch`를 통해서 처리한 것.

<br>

**파라미터 바인딩 - 이름 기준, 위치 기준**

```java
// 이름 기준
TypeQuery<Member> query = em.createQuery("SELECT m FROM Member m WHERE m.username=:username", Member.class)
    .setParmeter("username", "binghe");
```

```java
TypeQuery<Member> query = em.createQuery("SELECT m FROM Member m WHERE m.username=?1", Member.class)
    .setParmeter(1, "binghe);
```
* 가능한 이름 기반을 사용하자. 순서는 추후에도 바뀔 수 있기 때문에!

<br>

# 프로젝션

**🤔 프로젝션이란?**

* SELECT 절에 조회할 대상을 지정하는 것.

<br>

**🤔 프로젝션 대상**
* 엔티티 프로젝션
    * `SELECT m FROM Member m` -> 결과 `Member` 엔티티
    * `SELECT m.team FROM Member m` -> 결과 `Team` 엔티티
* 임베디드 타입 프로젝션
    * `SELECT m.address FROM Member m` -> 결과 `Address` 임베디드 타입(값 타입)
* 스칼라 타입(숫자, 문자등 기본 데이터 타입) 프로젝션
    * `SELECT m.username, m.age FROM member m`
* `DISTINCT`로 중복제거할 수 있다.

<br>

**프로젝션 특징 - 중요**
* **엔티티**
    * **엔티티 프로젝션의 경우 SELECT 대상이 되는 결과 엔티티는 모두 영속성 컨텍스트에 의해 관리된다.**
    * **연관관계 주인이 아닌 엔티티를 조회하면, join(암묵적)을 통해서 엔티티를 가져온다.**
* **임베디드 타입**
    * 임베디드는 테이블상에선 같은 테이블이므로 별다른 것 없이 SELECT 쿼리를 통해 가져온다.
* **스칼라 타입 (여러 값 조회) - 중요**
    * Query 타입으로 조회
    * Object[] 타입으로 조회
    * new 명령어로 조회

<br>

# 페이징 API

<br>

**JPA는 페이징을 다음 두 API로 추상화했다**

* `setFirstResult(int startPosition)`: 조회 시작 위치 (0부터 시작)
* `setMaxResult(int maxResult)`: 조회할 데이터 수

<br>

> 몇 번째(`setFirstResult`)부터 몇 개(`setMaxResult`) 가져올래?

<br>

# 조인

```java
// 내부 조인
SELECT * FROM Member as m [INNER] JOIN m.team t

// 외부 조인
SELECT * FROM Member as m LEFT [OUTER] JOIN m.team t

// 세타 조인
SELECT count(m) FROM Member as m, Team as t WHERE m.name = t.name;
```
* 내부 조인과 외부 조인은 기존의 SQL과 동일하다.
* 세타 조인은 관련 없는 테이블끼리 조인하는 것을 의미한다.
    * 아무 관계 없는 Member의 이름과 Team의 이름이 같은지를 통해 쿼리를 할 수 있다.

<br>

**ON 절**

> * ON절?
    >   * ON절은 join할 때 사용되는 where문이다.
>   * ON이 지정한 결합 조건에 일치하는 행만 join한다.
> * JPA 2.1부터 지원하는 기능.

<br>

1. 조인 대상 필터링
    * 조인을 할때 특정 조건에 맞는 행만 조인한다.
    * ex. **회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인**
    * JPQL: `SELECT m, t FROM Member m LEFT JOIN m.team t ON t.name = 'A';`
    * SQL: `SELECT m.*, t.* FROM MEMBER m LEFT JOIN Team t ON m.TEAM_ID = t.id AND t.name = 'A';`
2. 연관관계 없는 엔티티 외부 조인
    * ex. 회원의 이름과 팀의 이름이 같은 대상 외부 조인
    * JPQL: `SELECT m, t FROM Member m LEFT JOIN Team t ON m.name = t.name;`
    * SQL: `SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.name = t.name;`

<br>

# 서브 쿼리

<br>

**SQL의 서브 쿼리 예시**
```sql
-- 서브 쿼리 예시
-- 나이가 평균보다 많은 회원
select m from Member m
where m.age > (select avg(m2.age) from Member m2)

-- 한 건이라도 주문한 고객
select m from Member m 
where (select count(o) from Order o where m = o.member) > 0
```

<br>

**서브 쿼리 지원 함수**

* `[NOT] EXISTS (subquery)`: 서브쿼리에 결과가 존재하면 참
    * `{ALL | ANY | SOME} (subquery)`
    * `ALL` 모두 만족하면 참
    * `ANY`, `SOME`: 같은 의미, 조건을 하나라도 만족하면 참
    * ex. `select m from Member m where exists (select t from m.team t where t.name = 'Team A')`
    * ex. `select o from Order o where o.orderAmount > ALL (select p.stockAmount from Product p)`
* `[NOT] IN (subquery)`: 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참.

<br>

**JPA 서브 쿼리 한계 - 중요**

* JPA는 WHERE, HAVING 절에서만 서브 쿼리 사용 가능하다.
    * 표준 스펙에선 그렇게 정의되어있다.
* SELECT 절도 사용 가능하다. (표준 스펙엔 없지만 하이버네이트에서 제공해준다.)
* FROM 절의 서브 쿼리는 현재 JPQL에서 불가능하다.
    * 조인으로 풀 수 있으면 풀어서 해결하면 된다.

<br>

# JPQL 타입 표현
* 문자: 'HELLO', 'She''s'
* 숫자: 10L(Long), 10D(Double), 10F(Float)
* Boolean: TRUE, FALSE
* ENUM: 패키지명 포함해서 넣어줘야한다. ex. `jpa.MemberType.ADMIN`
* 엔티티 타입: TYPE(m) = Member (상속 관계에서 사용)
    * ex. `select i from Item i where type(i) = Book` (다형성)

<br>

# 조건식

**CASE 식**

```sql
select
    case when m.age <= 10 then '학생요금'
         when m.age >= 60 then '경로요금'
         else '일반요금'
    end
from Member m
```

<br>

**COALESCE와 NULLOF**

```sql
-- COALESCE
select coalesce(m.username, '이름 없는 회원') from Member m

-- NULLIF
select NULLIF(m.username, '관리자') from Member m
```
* COALESCE: 하나씩 조회해서 null이 아니면 반환
* NULLIF: 두 값이 같으면 null 반환, 다르면 첫번째 값 반환.

<br>

# JPQL 기본 함수
* CONCAT
* SUBSTRING
* TRIM
* LOWER, UPPER
* LENGTH
* LOCATE
* ABS, SQRT, MOD
* SIZE, INDEX(JPA 용도)
* 사용자 정의 함수
    * 사용하는 DB 방언을 상속받고, 사용자 정의 함수를 등록할 수 있다.

<br>

# 경로 표현식

<br>

**경로 표현식 종류**
```java
select m.username -> 상태 필드
  from Member m
    join m.team t -> 단일 값 연관 필드
    join m.orders o -> 컬렉션 값 연관 필드
where t.name = '팀A'
```
* 상태 필드: 단순한 값을 저장하기 위한 필드
* 연관 필드: 연관관계를 위한 필드
  * 단일 값 연관 필드: `@ManyToOne`, `@OneToOne`등 대상이 엔티티 (ex. `m.team`)
  * 컬렉션 값 연관 필드: `@OneToMany`, @ManyToMany`등 대상이 컬렉션 (ex. `m.orders`)

<br>

**경로 표현식 특징**

* 상태 필드
  * 경로 탐색의 끝. 탐색 X
* 단일 값 연관 경로
  * 묵시적 내부 조인 (inner join) 발생. 탐색 O
  * ex. `select m.team.X From Member m;` -> team은 내부를 계속해서 탐색이 가능하다.
* 컬렉션 값 연관 경로
  * 묵시적 내부 조인 (inner join) 발생. 탐색X
  * ex. `select t.members from Team t;` -> t.members 내부를 계속해서 탐색이 불가능하다. (이유는 컬렉션이기 때문)
    * 대신 `FROM`절에서 **명시적 조인**을 통해 별칭을 얻으면 별칭을 통해 탐색이 가능하다.
    * ex. `select m.name from Team t join t.members m`

<br>

> **묵시적 내부 조인이란?**
> * 경로 표현식에 의해 묵시적으로 SQL 조인 발생.
> * ex. `select m.team from Member m`을 하면 Member에 속한 team을 가져오기 위해 묵시적으로 inner join이 발생하게 된다.
> * **항상 묵시적 내부 조인이 발생하지 않게 쿼리를 짜야한다. 잘못하면 수백개의 쿼리가 날라갈 수도 있기 때문.**
>
> <br>
>
> **명시적 조인이란?**
> * join 키워드 직접 사용
> * ex. `select m from Member m join m.team t`

<br>

**경로 표현식 vs SQL**

> 상태 필드
```sql
JPQL: select m.username, m.age from Member m

SQL: select m.username, m.age from Member m
```

<br>

> 단일 값 연관 경로 탐색
```sql
JPQL: select o.member from Order o

SQL: select m.* from Orders o inner join Member m on o.member_id = m.id
```
* 묵시적 join이 날라가므로, 추후에 문제가 발생시 원인을 찾기 힘들어진다.

<br>

> 컬렉션 값 연관 경로 탐색 예시

```sql
select t.members from Team t; -> 성공 (하지만 Collection을 반환하므로 좋지 않다.)

select t.members.username from Team t; -> 실패

select m.username from Team t join t.members m; -> 성공
```

