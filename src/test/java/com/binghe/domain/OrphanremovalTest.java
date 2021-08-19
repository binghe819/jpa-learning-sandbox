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

@DisplayName("Orphanremoval = true 테스트")
public class OrphanremovalTest {

    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;

    @BeforeEach
    void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("test_persistence_config");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @DisplayName("상위 엔티티(Post)가 삭제되면 하위 엔티티(Comment)도 같이 삭제된다.")
    @Test
    void orphanremoval_True_DeleteParent() {
        // given
        createPostWithComment();

        // when
        entityManager.getTransaction().begin();
        entityManagerFlushAndClear();

        Post findPost = findPostById(1L);
        entityManager.remove(findPost);

        entityManager.getTransaction().commit();

        // then
        assertThat(findAllPosts().size()).isEqualTo(0);
        assertThat(findAllComments().size()).isEqualTo(0);
    }

    @DisplayName("상위 엔티티(Post)에서 하위 엔티티(Comment)를 삭제하면 DB에서 삭제된다.")
    @Test
    void orphanremoval_True_ParentDeleteChild() {
        // given
        createPostWithComment();

        // when
        entityManager.getTransaction().begin();
        entityManagerFlushAndClear();

        Post findPost = findPostById(1L);
        findPost.getComments().remove(0);

        entityManager.getTransaction().commit();

        // then
        assertThat(findAllPosts().size()).isEqualTo(1);
        assertThat(findAllComments().size()).isEqualTo(1);
        assertThat(findAllComments().size()).isNotEqualTo(2);
    }

    @DisplayName("상위 엔티티(Post)와 하위 엔티티(Comment)가 프록시일 때 상위 엔티티(Post)를 삭제하면 하위 엔티티(Comment)까지 모두 조회하여 삭제한다.")
    @Test
    void orphanremoval_True_DeleteParentWithProxyChild() {
        // given
        createPostWithComment();

        // when
        entityManager.getTransaction().begin();
        entityManagerFlushAndClear();

        Post findPost = entityManager.getReference(Post.class, 1L);
        System.out.println("========= 여기까진 프록시 =========");
        entityManager.remove(findPost);

        entityManager.getTransaction().commit();

        // then
        assertThat(findAllPosts().size()).isEqualTo(0);
        assertThat(findAllComments().size()).isEqualTo(0);
    }

    private void createPostWithComment() {
        Post post = new Post();
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();

        post.addComment(comment1);
        post.addComment(comment2);

        entityManager.getTransaction().begin();
        entityManager.persist(post);
        entityManager.getTransaction().commit();

        assertThat(findAllPosts().size()).isEqualTo(1);
        assertThat(findAllComments().size()).isEqualTo(2);
    }

    private Post findPostById(Long id) {
        String query = "select p from Post p join fetch p.comments";
        TypedQuery<Post> result = entityManager.createQuery(query, Post.class);
        return result.getSingleResult();
    }

    private List<Post> findAllPosts() {
        String query = "select p from Post p";
        TypedQuery<Post> result = entityManager.createQuery(query, Post.class);
        return result.getResultList();
    }

    private List<Comment> findAllComments() {
        String query = "select c from Comment c";
        TypedQuery<Comment> result = entityManager.createQuery(query, Comment.class);
        return result.getResultList();
    }

    private void entityManagerFlushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
