package com.binghe.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "post",
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
    )
    private List<Image> images = new ArrayList<>();

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "post",
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<Comment> comments = new ArrayList<>();

    public void addImage(Image image) {
        image.setPost(this);
        this.images.add(image);
    }

    public void addComment(Comment comment) {
        comment.setPost(this);
        this.comments.add(comment);
    }

    public List<Image> getImages() {
        return images;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
