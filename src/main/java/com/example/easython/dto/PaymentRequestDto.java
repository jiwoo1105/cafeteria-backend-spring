package com.example.easython.dto;

import com.example.easython.domain.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
    @NotNull(message = "테이블 ID는 필수입니다")
    private String tableId;

    @NotNull(message = "결제 금액은 필수입니다")
    private BigDecimal amount;

    @NotNull(message = "결제 방법은 필수입니다")
    private Payment.PaymentMethod paymentMethod;
}

