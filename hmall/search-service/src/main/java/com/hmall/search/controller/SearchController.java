package com.hmall.search.controller;

import com.hmall.common.domain.PageDTO;
import com.hmall.search.domain.dto.ItemDTO;
import com.hmall.search.domain.dto.ItemDoc;
import com.hmall.search.domain.query.ItemPageQuery;
import com.hmall.search.domain.vo.CategoryAndBrandVo;
import com.hmall.search.service.ISearchService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final ISearchService iSearchService;

    @GetMapping("/searchItem")
    public PageDTO<ItemDoc> searchItem(@RequestBody ItemPageQuery query) throws IOException {
        return iSearchService.searchItem(query);
    }

    @PostMapping("/getFilters")
    public CategoryAndBrandVo getFilters(@RequestBody ItemPageQuery query){
        return iSearchService.getFilters(query);
    }
}
