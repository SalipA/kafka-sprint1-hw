package ru.practicum.kafka_sprint_1.dto;

import lombok.*;

/** Заказ */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long orderId;
    private String clientId;
    private Double sum;

    @Override
    public String toString() {
        return "OrderDto{" +
            "orderId=" + orderId +
            ", clientId='" + clientId + '\'' +
            ", sum=" + sum +
            '}';
    }
}
