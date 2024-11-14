package com.hmall.item.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.api.client.SearchClient;
import com.hmall.api.dto.CategoryAndBrandVo;
import com.hmall.api.dto.ItemDoc;
import com.hmall.api.dto.ItemPageQuery;
import com.hmall.common.domain.PageDTO;
import com.hmall.item.domain.dto.ItemDTO;
import com.hmall.item.domain.po.Item;
import com.hmall.item.service.IItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "搜索相关接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {


    private final SearchClient searchClient;

    @ApiOperation("搜索商品")
    @GetMapping("/list")
    public PageDTO<ItemDoc> search(ItemPageQuery query) {
        System.out.println(query);
        return searchClient.searchItem(query);
    }
    @ApiOperation("商品聚合")
    @PostMapping("/filters")
    public CategoryAndBrandVo filters(@RequestBody ItemPageQuery query) {
        System.out.println(query);
        return searchClient.getFilters(query);
    }
}
