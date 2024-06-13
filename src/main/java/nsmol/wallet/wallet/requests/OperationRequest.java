package nsmol.wallet.wallet.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import nsmol.wallet.wallet.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

public record OperationRequest(
        @NotNull UUID walletId,
        @NotNull OperationType operationType,
        @NotNull @Positive(message = "Amount must be positive") BigDecimal amount
) {
}
