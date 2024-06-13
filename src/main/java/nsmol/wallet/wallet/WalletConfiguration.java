package nsmol.wallet.wallet;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WalletConfiguration {
    @Bean
    public WalletRepository walletRepository(DSLContext dslContext) {
        return new WalletRepository(dslContext);
    } 
    
    @Bean
    public WalletService walletService(WalletRepository walletRepository) {
        return new WalletService(walletRepository);
    }
}
