package com.hmall.trade.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
@Data
@AllArgsConstructor
public class CartListen {
    private Set<Long> itemIds;
    private Long user;
}
