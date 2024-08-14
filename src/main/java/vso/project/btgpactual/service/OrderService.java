package vso.project.btgpactual.service;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Service;
import vso.project.btgpactual.dtos.OrderCreatedEvent;
import vso.project.btgpactual.dtos.OrderResponse;
import vso.project.btgpactual.entity.OrderEntity;
import vso.project.btgpactual.entity.OrderItem;
import vso.project.btgpactual.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;

    public OrderService(OrderRepository orderRepository, MongoTemplate mongoTemplate) {
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void save(OrderCreatedEvent event){
        var entity = new OrderEntity();
        entity.setOrderId(event.codigoPedido());
        entity.setCustomerId(event.codigoCliente());
        entity.setItems(getOrderItems(event));
        entity.setTotal(getTotal(event));
        orderRepository.save(entity);

    }

    public Page<OrderResponse> findAllByCustomerId(Long customerId, PageRequest pageRequest){
        var orders = orderRepository.findAllByCustomerId(customerId, pageRequest);

        return orders.map(OrderResponse::fromEntity);
    }

    private BigDecimal getTotal(OrderCreatedEvent event) {
        return event.itens().stream()
                //multiplica o preço pelo valor da quantidade
                .map(i -> i.preco().multiply(BigDecimal.valueOf(i.quantidade())))
                //soma todos os valores e retorna o resultado
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public static List<OrderItem> getOrderItems(OrderCreatedEvent event){
        return event.itens().stream()
                .map(i -> new OrderItem(i.produto(), i.quantidade(), i.preco()))
                .toList();
    }

    public BigDecimal findTotalOnOrdersByCustomerId(Long customerId){
        var aggregations = newAggregation(
                match(Criteria.where("customerId").is(customerId)),
                group("customerId").sum("total").as("total")
        );
        var response = mongoTemplate.aggregate(aggregations, "tb_order", Document.class);

        return new BigDecimal(Objects.requireNonNull(response.getUniqueMappedResult()).get("total").toString());
    }
}
