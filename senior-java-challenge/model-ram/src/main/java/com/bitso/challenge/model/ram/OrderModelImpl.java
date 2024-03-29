package com.bitso.challenge.model.ram;

import com.bitso.challenge.entity.Currency;
import com.bitso.challenge.entity.Order;
import com.bitso.challenge.model.OrderModel;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * RAM-based implementation of orders.
 */
public class OrderModelImpl implements OrderModel {

    private HashMap<Long,Order> orders = new HashMap<>(100);
    private AtomicLong seq = new AtomicLong();

    @Override
    public long submit(Order order) {
        order.setCreated(new Date());
        order.setStatus(Order.Status.active);
        insert(order);
        return order.getId();
    }

    public long insert(Order order) {
        //TODO validate
        order.setId(seq.incrementAndGet());
        orders.put(order.getId(), order);
        return order.getId();
    }

    @Override
    public Optional<Order> get(long id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> ordersForUser(long userId, Order.Status status, Currency major, Currency minor) {
        return orders.values().stream().filter(o -> o.getUserId() == userId)
                .filter(o -> status == null || status == o.getStatus())
                .filter(o -> major == null || major == o.getMajor())
                .filter(o -> minor == null || minor == o.getMinor())
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> book(Currency major, Currency minor) {
        // throw new UnsupportedOperationException("Implement me!");
        return orders.values().stream().filter(o -> o.getMajor() == major)
                .filter(o -> o.getMinor() == minor )
                .collect(Collectors.toList());
    }
}
