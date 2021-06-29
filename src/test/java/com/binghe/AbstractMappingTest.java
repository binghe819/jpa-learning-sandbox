package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("상속관계 매핑 테스트")
public class AbstractMappingTest {

    @DisplayName("테이블이 어떻게 생성되나 확인하는 테스트")
    @Test
    void table() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {

            Movie movie = new Movie();
            movie.setDirector("A");
            movie.setActor("binghe");
            movie.setName("wooteco");
            movie.setPrice(10_000);

            entityManager.persist(movie);

            entityManager.flush();
            entityManager.clear();

            Movie findMovie = entityManager.find(Movie.class, movie.getId());
            assertThat(findMovie.getName()).isEqualTo("wooteco");
            assertThat(findMovie.getDirector()).isEqualTo("A");
            assertThat(findMovie.getActor()).isEqualTo("binghe");
            assertThat(findMovie.getPrice()).isEqualTo(10_000);

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
