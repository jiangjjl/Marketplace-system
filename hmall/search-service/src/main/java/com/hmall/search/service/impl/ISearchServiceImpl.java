package com.hmall.search.service.impl;

import cn.hutool.json.JSONUtil;
import com.hmall.common.domain.PageDTO;
import com.hmall.common.utils.CollUtils;
import com.hmall.search.domain.dto.ItemDTO;
import com.hmall.search.domain.dto.ItemDoc;
import com.hmall.search.domain.query.ItemPageQuery;
import com.hmall.search.domain.vo.CategoryAndBrandVo;
import com.hmall.search.service.ISearchService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.management.Query;
import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ISearchServiceImpl implements ISearchService {
    private final RestHighLevelClient client;
    @Override
    public PageDTO<ItemDoc> searchItem(ItemPageQuery query) throws IOException {
        System.out.println(query.toString());
        SearchRequest searchRequest = new SearchRequest("items");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("name", query.getKey()));
        if(query.getBrand() != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery("brand", query.getBrand()));
        }
        if(query.getCategory() != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery("category", query.getCategory()));

        }
        //排序
        if(query.getSortBy().isEmpty()){
            searchRequest.source().sort("updateTime", query.getIsAsc() ? SortOrder.ASC : SortOrder.DESC);
        }else{
            searchRequest.source().sort(query.getSortBy(), query.getIsAsc() ? SortOrder.ASC : SortOrder.DESC);
        }
        //价格
        if(query.getMinPrice() != null && query.getMaxPrice() != null){
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(query.getMinPrice()).lte(query.getMaxPrice()));
        }
        //高亮
        searchRequest.source().highlighter(
                SearchSourceBuilder.highlight()
                        .field("name")
                        .preTags("<em>")
                        .postTags("</em>")
        );
        //排名 广告优先
        searchRequest.source().query(QueryBuilders.functionScoreQuery(boolQueryBuilder,
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.termQuery("isAD", true),
                                ScoreFunctionBuilders.weightFactorFunction(100))
                }).boostMode(CombineFunction.MULTIPLY));

        searchRequest.source().from(((query.getPageNo() - 1) * query.getPageSize())).size(query.getPageSize());
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 4. 获取查询结果并封装成PageDTO
        SearchHits searchHits = searchResponse.getHits();
        SearchHit[] hits = searchHits.getHits();
        List<ItemDoc> itemDocList = new ArrayList<>();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            ItemDoc itemDoc = JSONUtil.toBean(sourceAsString, ItemDoc.class);
            Map<String, HighlightField> hfs = hit.getHighlightFields();
            if(CollUtils.isNotEmpty(hfs)){
                HighlightField hf = hfs.get("name");
                if(hf != null){
                    StringBuilder str = new StringBuilder();
                    for (Text fragment : hf.getFragments()) {
                        str.append(fragment);
                    }
                    itemDoc.setName(str.toString());
                }
            }
            itemDocList.add(itemDoc);
        }

        // 5. 封装分页数据
        PageDTO<ItemDoc> pageDTO = new PageDTO<>();
        pageDTO.setTotal(searchHits.getTotalHits().value);  // 设置总记录数
        pageDTO.setPages((long) query.getPageNo()); // 设置当前页码
        pageDTO.setList(itemDocList);  // 设置当前页的记录
        return pageDTO;
    }

    @Override
    public CategoryAndBrandVo getFilters(ItemPageQuery query) {
        CategoryAndBrandVo categoryAndBrandVo = new CategoryAndBrandVo();
        // 1.创建Request
        SearchRequest request = new SearchRequest("items");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (query.getKey() != null && !query.getKey().isEmpty()) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", query.getKey()));
        }
        if (query.getCategory()!=null && !query.getCategory().isEmpty()){
            boolQueryBuilder.filter(QueryBuilders.termQuery("category", query.getCategory()));
        }
        if (query.getBrand()!=null && !query.getBrand().isEmpty()){
            boolQueryBuilder.filter(QueryBuilders.termQuery("brand", query.getBrand()));
        }
        if (query.getMinPrice() != null && query.getMaxPrice() != null){
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(query.getMinPrice()).lte(query.getMaxPrice()));
        }
        request.source().query(boolQueryBuilder).size(0);
        request.source().aggregation(
                AggregationBuilders.terms("category_agg").field("category").size(10)
        );
        request.source().aggregation(
                AggregationBuilders.terms("brand_agg").field("brand").size(10));
        List<String> categoryList = new ArrayList<>();
        List<String> brandList = new ArrayList<>();
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            Terms categoryTerms = aggregations.get("category_agg");
            // 5.2.获取聚合中的桶
            List<? extends Terms.Bucket> buckets = categoryTerms.getBuckets();
            for (Terms.Bucket bucket : buckets) {
                // 5.4.获取桶内key
                String category = bucket.getKeyAsString();
                categoryList.add(category);
            }
            Terms brandTerms = aggregations.get("brand_agg");
            // 5.2.获取聚合中的桶
            List<? extends Terms.Bucket> buckets1 = brandTerms.getBuckets();
            for (Terms.Bucket bucket : buckets1) {
                // 5.4.获取桶内key
                String brand = bucket.getKeyAsString();
                brandList.add(brand);
            }
            categoryAndBrandVo.setCategory(categoryList);
            categoryAndBrandVo.setBrand(brandList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return categoryAndBrandVo;
    }
}
