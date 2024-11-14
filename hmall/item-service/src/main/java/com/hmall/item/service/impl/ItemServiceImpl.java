package com.hmall.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.BeanUtils;
import com.hmall.item.domain.dto.ItemDTO;
import com.hmall.item.domain.dto.ItemDoc;
import com.hmall.item.domain.dto.OrderDetailDTO;
import com.hmall.item.domain.po.Item;
import com.hmall.item.mapper.ItemMapper;
import com.hmall.item.service.IItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author jjl
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {

    private final ItemMapper itemMapper;

    private final RabbitTemplate rabbitTemplate;
    @Override
    public void deductStock(List<OrderDetailDTO> items) {
        String sqlSelectStatement = "com.hmall.item.mapper.ItemMapper.selectStock"; // 查询库存的方法
        String sqlUpdateStatement = "com.hmall.item.mapper.ItemMapper.updateStock"; // 更新库存的方法
        List<ItemDoc> itemDocs = new ArrayList<>();
        for (OrderDetailDTO item : items) {
            Integer currentStock = itemMapper.selectStock(item.getItemId());
            if (currentStock == null || currentStock < item.getNum()) {
                throw new BizIllegalException("库存不足！商品ID: " + item.getItemId());
            }
            ItemDoc itemDoc = new ItemDoc();
            itemDoc.setId(String.valueOf(item.getItemId()));
            itemDoc.setSold(item.getNum());
            itemDocs.add(itemDoc);
        }

        boolean r = false;
        try {
            r = executeBatch(items, (sqlSession, entity) -> sqlSession.update(sqlUpdateStatement, entity));
            rabbitTemplate.convertAndSend("update.item.direct", "item.updateBulk", itemDocs);
        } catch (Exception e) {
            log.error("更新库存异常", e);
            return;
        }

        if (!r) {
            throw new BizIllegalException("更新库存失败！");
        }
    }


    @Override
    public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
        return BeanUtils.copyList(listByIds(ids), ItemDTO.class);
    }
}
