package me.jpomykala.oauth2.repository;

import me.jpomykala.oauth2.model.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by Evelan on 16/12/2016.
 */
@Repository
public interface PostsRepository extends JpaRepository<Post, Long>, QueryDslPredicateExecutor<Post> {
}
