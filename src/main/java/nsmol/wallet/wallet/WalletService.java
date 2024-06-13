package nsmol.wallet.wallet;

import nsmol.wallet.wallet.exceptions.InsufficientFundsException;
import nsmol.wallet.wallet.exceptions.WalletNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

public class WalletService {
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet getBalance(UUID id) {
        return walletRepository.getWallet(id)
                .orElseThrow(() -> new WalletNotFoundException("id: " + id));
    }

    @Transactional
    public Wallet deposit(UUID id, BigDecimal amount) {
        var walletFromDb = getBalance(id);
        
        var newWallet = Wallet.builder().copyFrom(walletFromDb)
                .withBalance(walletFromDb.balance().add(amount))
                .build();
        
        return walletRepository.updateWallet(newWallet);
    }

    @Transactional
    public Wallet withdraw(UUID id, BigDecimal amount) {
        var walletFromDb = getBalance(id);

        if (walletFromDb.balance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds"); 
        }
        
        var newWallet = Wallet.builder().copyFrom(walletFromDb)
                .withBalance(walletFromDb.balance().subtract(amount))
                .build();

        return walletRepository.updateWallet(newWallet);
    }
}
