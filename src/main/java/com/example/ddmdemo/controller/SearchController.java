package com.example.ddmdemo.controller;

import com.example.ddmdemo.dto.SearchPhraseQueryDTO;
import com.example.ddmdemo.dto.SearchQueryDTO;
import com.example.ddmdemo.indexmodel.DummyIndex;
import com.example.ddmdemo.model.CustomResponse;
import com.example.ddmdemo.service.interfaces.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/simple")
    public Page<DummyIndex> simpleSearch(@RequestBody SearchQueryDTO simpleSearchQuery,
                                         Pageable pageable) {
        return searchService.simpleSearch(simpleSearchQuery.keywords(), pageable);
    }

    @PostMapping("/advanced")
    public Page<DummyIndex> advancedSearch(@RequestBody SearchQueryDTO advancedSearchQuery,
                                           Pageable pageable) {
        return searchService.advancedSearch(advancedSearchQuery.keywords(), pageable);
    }

    @PostMapping("/phrase")
    public Page<DummyIndex> advancedSearch(@RequestBody SearchPhraseQueryDTO phraseQuery,
                                           Pageable pageable) {
        return searchService.phraseSearch(phraseQuery.query(), pageable);
    }
}
