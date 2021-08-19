package com.binghe.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Cascade.REMOVE 테스트 - Post, Image의 관계")
public class CascadeRemoveTest {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeEach
    void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @DisplayName("상위 엔티티(Post)가 삭제되면 하위 엔티티(Image)도 같이 삭제된다.")
    @Test
    void cascade_REMOVE_DeleteParent() {
        // given
        createPostWithImages();

        // when
        entityManager.getTransaction().begin();
        entityManagerFlushAndClear();

        Post findPost = findPostById(1L);
        entityManager.remove(findPost);

        entityManager.getTransaction().commit();

        // then
        assertThat(findAllPosts().size()).isEqualTo(0);
        assertThat(findAllImages().size()).isEqualTo(0);
    }

    @DisplayName("상위 엔티티(Post)의 하위 엔티티(Image)를 삭제해도 DB엔 삭제되지 않는다.")
    @Test
    void cascade_REMOVE_ParentDeleteChild() {
        // given
        createPostWithImages();

        // when
        entityManager.getTransaction().begin();
        entityManagerFlushAndClear();

        Post findPost = findPostById(1L);
        findPost.getImages().remove(0);

        entityManager.getTransaction().commit();

        // then
        assertThat(findAllPosts().size()).isEqualTo(1);
        assertThat(findAllImages().size()).isNotEqualTo(1);
        assertThat(findAllImages().size()).isEqualTo(2);
    }

    @DisplayName("상위 엔티티(Post)와 하위 엔티티(Image)가 프록시일 때 상위 엔티티(Post)를 삭제하면 하위 엔티티(Image)까지 모두 조회하여 삭제한다.")
    @Test
    void cascade_REMOVE_DeleteProxyParentWithProxyChild() {
        // given
        createPostWithImages();

        // when
        entityManager.getTransaction().begin();
        entityManagerFlushAndClear();

        Post findPost = entityManager.getReference(Post.class, 1L);
        System.out.println("========= 여기까진 프록시 =========");
        entityManager.remove(findPost);

        entityManager.getTransaction().commit();

        // then
        assertThat(findAllPosts().size()).isEqualTo(0);
        assertThat(findAllImages().size()).isEqualTo(0);
    }

    private void createPostWithImages() {
        Post post = new Post();
        Image image1 = new Image();
        Image image2 = new Image();

        post.addImage(image1);
        post.addImage(image2);

        entityManager.getTransaction().begin();
        entityManager.persist(post);
        entityManager.getTransaction().commit();

        assertThat(findAllPosts().size()).isEqualTo(1);
        assertThat(findAllImages().size()).isEqualTo(2);
    }

    private Post findPostById(Long id) {
        String query = "select p from Post p join fetch p.images";
        TypedQuery<Post> result = entityManager.createQuery(query, Post.class);
        return result.getSingleResult();
    }

    private List<Post> findAllPosts() {
        String query = "select p from Post p";
        TypedQuery<Post> result = entityManager.createQuery(query, Post.class);
        return result.getResultList();
    }

    private List<Image> findAllImages() {
        String query = "select i from Image i";
        TypedQuery<Image> result = entityManager.createQuery(query, Image.class);
        return result.getResultList();
    }

    private void entityManagerFlushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
