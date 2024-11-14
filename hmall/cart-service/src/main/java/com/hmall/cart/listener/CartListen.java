package com.hmall.cart.listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartListen {
    private Set<Long> itemIds;
    private Long user;
}
