package vso.project.btgpactual.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import vso.project.btgpactual.dtos.OrderCreatedEvent;
import vso.project.btgpactual.service.OrderService;

import static vso.project.btgpactual.config.RabbitMqConfig.ORDER_CREATED_QUEUE;

@Component
public class OrderCreatedListener {
    private final Logger logger = LoggerFactory.getLogger(OrderCreatedEvent.class);
    private final OrderService orderService;

    public OrderCreatedListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = ORDER_CREATED_QUEUE)
    public void listener(Message<OrderCreatedEvent> message){
        logger.info("Order Created Event Received: {}", message);

        orderService.save(message.getPayload());
    }
}
