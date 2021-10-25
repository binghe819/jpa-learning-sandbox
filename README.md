# JPA 놀이터 - 지연 로딩과 즉시 로딩

<br>

# 학습 테스트 내용
- [지연 로딩](./src/test/java/com/binghe/LazyTest.java)
- [즉시 로딩](./src/test/java/com/binghe/EagerTest.java)

<br>

# 지연 로딩과 즉시 로딩

<br>

## 지연 로딩

<br>

**지연 로딩이란**

<p align="center"><img src="./image/lazy_loading_1.png"><br>출처: 자바 ORM 표준 JPA 프로그래밍</p>

<br>

<p align="center"><img src="./image/lazy_loading_2.png"><br>출처: 자바 ORM 표준 JPA 프로그래밍</p>

* fetch 설정값을 FetchType.LAZY로하면 해당 의존성 객체는 프록시 객체를 만든다. 그리고 사용하는 시점에 쿼리를 통해 가져온다.

<br>

## 즉시 로딩

<br>

**즉시 로딩이란**

<p align="center"><img src="./image/eager_loading.png"><br>출처: 자바 ORM 표준 JPA 프로그래밍</p>  

* fetch 설정값을 FetchType.Eager로하면 해당 객체를 가져올 때, join을 통해 한번에 가져온다.

<br>

**프록시와 즉시 로딩 주의**
* **가급적 지연 로딩만 사용해야 한다.**
  * 이유는 **즉시 로딩을 적용하면 예상하지 못한 SQL이 발생할 수 있기 때문이다.**
  * 또한, **즉시 로딩은 N+1 문제를 일으킬 수 있다.**
* 모든 연관관계에 지연 로딩을 사용해라!
