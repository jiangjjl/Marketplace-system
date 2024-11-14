package com.hmall.item;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.common.utils.CollUtils;
import com.hmall.item.domain.dto.ItemDoc;
import com.hmall.item.domain.po.Item;
import com.hmall.item.service.IItemService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
@SpringBootTest
class ItemTest {
    private RestHighLevelClient client;

    @Autowired
    private IItemService itemService;

    @Test
    void testLoadItemDocs() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.112.134:9200")
        ));
        // 分页查询商品数据
        int pageNo = 1;
        int size = 1000;
        while (true) {
            Page<Item> page = itemService.lambdaQuery().eq(Item::getStatus, 1).page(new Page<Item>(pageNo, size));
            // 非空校验
            List<Item> items = page.getRecords();
            if (CollUtils.isEmpty(items)) {
                return;
            }
            System.out.printf("加载第{}页数据，共{}条", pageNo, items.size());
            // 1.创建Request
            BulkRequest request = new BulkRequest("items");
            // 2.准备参数，添加多个新增的Request
            for (Item item : items) {
                // 2.1.转换为文档类型ItemDTO
                ItemDoc itemDoc = BeanUtil.copyProperties(item, ItemDoc.class);
                // 2.2.创建新增文档的Request对象
                request.add(new IndexRequest()
                        .id(itemDoc.getId())
                        .source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON));
            }
            // 3.发送请求
            client.bulk(request, RequestOptions.DEFAULT);

            // 翻页
            pageNo++;
        }
    }

}
