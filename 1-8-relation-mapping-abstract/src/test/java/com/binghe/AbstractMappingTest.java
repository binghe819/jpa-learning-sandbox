package com.binghe;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.template.EntityManagerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("상속관계 매핑 테스트")
public class AbstractMappingTest {

    private EntityManagerTemplate entityManagerTemplate;

    @BeforeEach
    void setUp() {
        entityManagerTemplate = new EntityManagerTemplate();
    }

    @DisplayName("테이블이 어떻게 생성되나 확인하는 테스트")
    @Test
    void table() {
        entityManagerTemplate.execute((entityManager, tx) -> {

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
        });
    }
}
