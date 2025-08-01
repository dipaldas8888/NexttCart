package com.dipal.NextCart.service.interfce;


import com.dipal.NextCart.dto.AddressDTO;
import com.dipal.NextCart.dto.OrderRequest;
import com.dipal.NextCart.dto.Response;


import com.dipal.NextCart.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OrderItemService {
    Response placeOrder(OrderRequest orderRequest);
    Response updateOrderItemStatus(Long orderItemId, String status);
    Response filterOrderItems(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate, Long itemId, Pageable pageable);
}