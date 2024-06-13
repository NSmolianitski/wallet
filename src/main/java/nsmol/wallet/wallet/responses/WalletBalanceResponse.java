package nsmol.wallet.wallet.responses;

import java.math.BigDecimal;

public record WalletBalanceResponse(String status, String message, BigDecimal balance) {
}
