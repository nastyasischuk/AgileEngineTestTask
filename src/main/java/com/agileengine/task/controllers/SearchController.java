package com.agileengine.task.controllers;

import com.agileengine.task.db.ImageService;
import com.agileengine.task.dto.SearchTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("search")
public class SearchController {
    @Autowired
    private ImageService imageService;

    @GetMapping
    public Object search(SearchTerm searchTerm) {
        return imageService.searchForImages(searchTerm);
    }
}
