package vso.project.btgpactual.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vso.project.btgpactual.dtos.ApiResponse;
import vso.project.btgpactual.dtos.OrderResponse;
import vso.project.btgpactual.dtos.PaginationResponse;
import vso.project.btgpactual.service.OrderService;

import java.util.Map;

@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> listOrders(
            @PathVariable("customerId") Long customerId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ){

        var orders = orderService.findAllByCustomerId(customerId, PageRequest.of(page, pageSize));

        var totalOnOrders = this.orderService.findTotalOnOrdersByCustomerId(customerId);

        return ResponseEntity.ok(new ApiResponse<>(
            Map.of("totalOnOrders", totalOnOrders),
            orders.getContent(),
            PaginationResponse.fromPage(orders)
    ));
    }
}
