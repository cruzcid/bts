package com.bitso.challenge.service;

import com.bitso.challenge.entity.Currency;
import com.bitso.challenge.entity.Order;
import com.bitso.challenge.model.OrderModel;
import com.bitso.challenge.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * REST endpoint to submit and query orders.
 */
@RestController("orders")
public class OrderController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private OrderModel orderModel;
    @Resource
    private UserModel userModel;

    @RequestMapping("/get/{id}")
    @ResponseBody
    public Optional<Order> get(@PathVariable long id) {
        Optional<Order> order = orderModel.get(id);
        if (order.isPresent()) {
            log.debug("get {}: {}", id, order.get());
        } else {
            log.debug("get {}: null", id);
        }
        return order;
    }

    @RequestMapping("/submit") @PostMapping
    public Order submit(Order order) {
        log.debug("Submitting order {}", order);
        orderModel.submit(order);
        return order;
    }

    @RequestMapping("/book/{major}/{minor}")
    public List<Order> book(@PathVariable String major,
                            @PathVariable String minor) {
        //TODO validate currencies
        Currency maj = major == null || major.isEmpty() ? null : Currency.valueOf(major);
        Currency min = minor == null || minor.isEmpty() ? null : Currency.valueOf(minor);
        return orderModel.book(maj, min);
    }

    @RequestMapping("/query/{userId}/{status}/{major}/{minor}")
    public List<Order> getBy(@PathVariable long userId,
                             @PathVariable String status,
                             @PathVariable String major,
                             @PathVariable String minor) {
        Order.Status st = status == null || status.isEmpty() ? null : Order.Status.valueOf(status);
        Currency maj = major == null || major.isEmpty() ? null : Currency.valueOf(major);
        Currency min = minor == null || minor.isEmpty() ? null : Currency.valueOf(minor);
        List<Order> r = orderModel.ordersForUser(userId, st, maj,min);
        log.debug("Query {}/{}/{}/{} returns {} orders", userId, st, maj, min, r.size());
        return r;
    }
}
