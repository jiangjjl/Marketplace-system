package com.hmall.api.client;

import com.hmall.api.client.fallback.PayClientFallback;
import com.hmall.api.dto.CategoryAndBrandVo;
import com.hmall.api.dto.ItemDoc;
import com.hmall.api.dto.ItemPageQuery;
import com.hmall.common.domain.PageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "search-service")
public interface SearchClient {
    @RequestMapping(value = "/search/searchItem", method = RequestMethod.GET)
    PageDTO<ItemDoc> searchItem(@RequestBody ItemPageQuery query);

    @RequestMapping(value = "/search/getFilters", method = RequestMethod.POST)
    CategoryAndBrandVo getFilters(@RequestBody ItemPageQuery query);
}
