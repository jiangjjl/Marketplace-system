package com.hmall.search.service;

import com.hmall.common.domain.PageDTO;
import com.hmall.search.domain.dto.ItemDTO;
import com.hmall.search.domain.dto.ItemDoc;
import com.hmall.search.domain.query.ItemPageQuery;
import com.hmall.search.domain.vo.CategoryAndBrandVo;

import java.io.IOException;

public interface ISearchService {
    PageDTO<ItemDoc> searchItem(ItemPageQuery query) throws IOException;
    CategoryAndBrandVo getFilters(ItemPageQuery query);
}
