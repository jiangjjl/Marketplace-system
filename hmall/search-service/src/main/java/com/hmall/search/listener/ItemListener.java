package com.hmall.search.listener;

import cn.hutool.json.JSONUtil;
import com.hmall.search.domain.dto.ItemDoc;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.Message;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.hibernate.validator.constraints.Range;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemListener {
    private final RabbitTemplate rabbitTemplate;
    private final RestHighLevelClient client;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.update.queue", durable = "true"),
            exchange = @Exchange(name = "update.item.direct"),
            key = "item.update")
    )
    public void listenUpdate(ItemDoc itemDoc, org.springframework.amqp.core.Message message) throws IOException {
        String method = message.getMessageProperties().getHeader("method");
        System.out.println("接收到消息，操作为：" + method);
        if("add".equals(method)){
            // 3.将ItemDTO转json
            String doc = JSONUtil.toJsonStr(itemDoc);
            // 1.准备Request对象
            IndexRequest request = new IndexRequest("items").id(itemDoc.getId());
            // 2.准备Json文档
            request.source(doc, XContentType.JSON);
            // 3.发送请求
            client.index(request, RequestOptions.DEFAULT);
            //新增
        }else if("update".equals(method)){
            //1.准备request对象
            UpdateRequest request = new UpdateRequest("items",itemDoc.getId());
            //2.准备请求体
            request.doc(JSONUtil.toJsonStr(itemDoc), XContentType.JSON);
            //3.发送请求
            try {
                client.update(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //修改
        }else if("delete".equals(method)){
            //删除
            //1.准备request对象
            DeleteRequest request = new DeleteRequest("items").id(itemDoc.getId());
            //2.发送请求
            client.delete(request, RequestOptions.DEFAULT);
        }else {
            System.err.printf("无法匹配操作类型");
        }
    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.update.batch.queue", durable = "true"),
            exchange = @Exchange(name = "update.item.direct"),
            key = "item.updateBulk")
    )
    public void listenUpdateBulk(List<ItemDoc> list) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (ItemDoc itemDoc : list) {
            String id = itemDoc.getId();
            UpdateRequest updateRequest = new UpdateRequest("items", id).doc(JSONUtil.toJsonStr(itemDoc), XContentType.JSON);
            bulkRequest.add(updateRequest);
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        if (bulkResponse.hasFailures()) {
            System.out.println("Bulk update encountered errors: " + bulkResponse.buildFailureMessage());
        } else {
            System.out.println("Bulk update successful.");
        }
    }
}
