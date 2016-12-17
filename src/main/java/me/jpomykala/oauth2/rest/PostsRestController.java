package me.jpomykala.oauth2.rest;

import me.jpomykala.oauth2.model.post.Post;
import me.jpomykala.oauth2.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Evelan on 16/12/2016.
 */
@RestController
public class PostsRestController {

    @Autowired
    private PostsRepository postsRepository;

    @RequestMapping(value = "/api/posts", method = RequestMethod.GET)
    public Page<Post> fetchAllPosts(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                    @RequestParam(value = "size", required = false, defaultValue = "20") Integer pageSize) {

        if (pageSize > 100) {
            pageSize = 100;
        }

        return postsRepository.findAll(new PageRequest(page, pageSize));
    }


}
