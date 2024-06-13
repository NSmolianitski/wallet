package nsmol.wallet.wallet;

import org.jooq.DSLContext;
import org.jooq.Record;

import java.util.Optional;
import java.util.UUID;

import static jooq.generated.Tables.WALLETS;

public class WalletRepository {
    private final DSLContext db;

    public WalletRepository(DSLContext db) {
        this.db = db;
    }

    public Optional<Wallet> getWallet(UUID id) {
        return db
                .selectFrom(WALLETS)
                .where(WALLETS.ID.eq(id))
                .fetchOptional(WalletRepository::buildWallet);
    }
    
    public Wallet updateWallet(Wallet wallet) {
        return db
                .update(WALLETS)
                .set(WALLETS.BALANCE, wallet.balance())
                .where(WALLETS.ID.eq(wallet.id()))
                .returning()
                .fetchSingle(WalletRepository::buildWallet);
    }
    
    public static Wallet buildWallet(Record record) {
        return Wallet
                .builder()
                .withId(record.get(WALLETS.ID))
                .withBalance(record.get(WALLETS.BALANCE))
                .build();
    }
}
