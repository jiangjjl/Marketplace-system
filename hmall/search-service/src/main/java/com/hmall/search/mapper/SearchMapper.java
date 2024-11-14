package com.hmall.search.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmall.search.domain.po.Item;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SearchMapper extends BaseMapper<Item> {
}
