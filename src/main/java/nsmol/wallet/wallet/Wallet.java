package nsmol.wallet.wallet;

import java.math.BigDecimal;
import java.util.UUID;

public record Wallet(UUID id, BigDecimal balance) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private BigDecimal balance;

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }
        
        public Builder withBalance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder copyFrom(Wallet wallet) {
            this.id = wallet.id;
            this.balance = wallet.balance;
            return this;
        }

        public Wallet build() {
            return new Wallet(id, balance);
        }
    }
}
