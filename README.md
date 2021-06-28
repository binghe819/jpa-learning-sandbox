# JPA 놀이터 - N : 1

<br>

# 학습 테스트 내용
- [N : 1 단방향](./src/test/java/com/binghe/one_way/OneWayTest.java)
- [N : 1 양방향](./src/test/java/com/binghe/two_way/TwoWayTest.java)

<br>

# 다대일 N : 1

<br>

**단방향**

<p align="center"><img src="./image/N_vs_1.png"><br>출처: 자바 ORM 표준 JPA 프로그래밍</p>

* DB입장에서 생각해보면 MEMBER N : 1 TEAM인 경우, N(다)쪽에 FK가 가야한다.
    * 아니면 설계가 잘못된 것.
* 객체상에서도 DB상에서의 FK가 있는 쪽에 `@ManyToOne`을 붙여주면 된다.

<br>

**양방향**

<p align="center"><img src="./image/N_vs_1_bothway.png"><br>출처: 자바 ORM 표준 JPA 프로그래밍</p>

* 외래 키가 있는 쪽이 연관관계의 주인
* 양쪽을 서로 참조하도록 개발
* **객체상에선 양방향으로 변경되지만, DB 테이블상에선 아무런 수정사항이 없다.**

<br>

# 일대다 1 : N
> 김영한님의 별로 추천하지 않은 관계라고 한다.

일대다는 다대일과 반대이며, Team이 관리를 하겠다는 설계구조.
