package nsmol.wallet.wallets;

import nsmol.wallet.wallet.Wallet;
import nsmol.wallet.wallet.WalletController;
import nsmol.wallet.wallet.WalletService;
import nsmol.wallet.wallet.exceptions.InsufficientFundsException;
import nsmol.wallet.wallet.exceptions.WalletNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
public class WalletControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    private UUID walletId;
    private BigDecimal amount;

    @BeforeEach
    public void setUp() {
        walletId = UUID.randomUUID();
        amount = new BigDecimal(100);
    }

    @Test
    public void getBalance_ValidRequest_ReturnsSuccessWithBalance() throws Exception {
        Wallet wallet = Wallet.builder()
                .withId(walletId)
                .withBalance(amount)
                .build();

        Mockito.when(walletService.getBalance(wallet.id()))
                .thenReturn(wallet);

        mockMvc.perform(get("/api/v1/wallets/{id}", walletId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.toString()))
                .andExpect(jsonPath("$.message").value("Balance retrieved successfully"))
                .andExpect(jsonPath("$.balance").value(amount));
    }

    @Test
    public void getBalance_WrongWalletId_ReturnsWalletNotFoundError() throws Exception {
        Mockito.when(walletService.getBalance(walletId))
                .thenThrow(new WalletNotFoundException("Wallet not found"));

        mockMvc.perform(get("/api/v1/wallets/{id}", walletId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.toString()))
                .andExpect(jsonPath("$.message").value("Wallet not found"));
    }

    @Test
    public void processOperation_ValidDepositRequest_Success() throws Exception {
        Wallet wallet = Wallet.builder()
                .withId(walletId)
                .withBalance(amount)
                .build();

        Mockito.when(walletService.deposit(walletId, amount))
                .thenReturn(wallet);

        var json = "{" +
                "\"walletId\": \"" + walletId + "\", " +
                "\"operationType\": \"DEPOSIT\", " +
                "\"amount\": \"" + amount + "\"" +
                "}";

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.toString()))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.balance").value(amount));
    }

    @Test
    public void processOperation_ValidWithdrawRequest_Success() throws Exception {
        Wallet wallet = Wallet.builder()
                .withId(walletId)
                .withBalance(amount)
                .build();

        Mockito.when(walletService.deposit(walletId, amount))
                .thenReturn(wallet);

        var json = "{" +
                "\"walletId\": \"" + walletId + "\", " +
                "\"operationType\": \"DEPOSIT\", " +
                "\"amount\": \"" + amount + "\"" +
                "}";

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.toString()))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.balance").value(amount));
    }

    @Test
    public void testInvalidFormatRequest() throws Exception {
        var json = "{ \"wrong\": \"data\" }";

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message")
                        .value("{walletId=must not be null, " +
                                "amount=must not be null, " +
                                "operationType=must not be null}"));
    }

    @Test
    public void processOperation_InsufficientFunds_ReturnsInsufficientFundsError() throws Exception {
        Mockito.when(walletService.withdraw(walletId, amount))
                .thenThrow(new InsufficientFundsException("Insufficient funds"));

        var json = "{" +
                "\"walletId\": \"" + walletId + "\", " +
                "\"operationType\": \"WITHDRAW\", " +
                "\"amount\": \"" + amount + "\"" +
                "}";

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isPaymentRequired())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.PAYMENT_REQUIRED.toString()))
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }

    @Test
    public void processOperation_NullPointer_ReturnsInternalServerError() throws Exception {
        Mockito.when(walletService.withdraw(walletId, amount))
                .thenThrow(new NullPointerException());

        var json = "{" +
                "\"walletId\": \"" + walletId + "\", " +
                "\"operationType\": \"WITHDRAW\", " +
                "\"amount\": \"" + amount + "\"" +
                "}";

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.toString()))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    @Test
    public void processOperation_WrongWalletId_ReturnsWalletNotFoundError() throws Exception {
        Mockito.when(walletService.withdraw(walletId, amount))
                .thenThrow(new WalletNotFoundException("Wallet not found"));

        var json = "{" +
                "\"walletId\": \"" + walletId + "\", " +
                "\"operationType\": \"WITHDRAW\", " +
                "\"amount\": \"" + amount + "\"" +
                "}";

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.toString()))
                .andExpect(jsonPath("$.message").value("Wallet not found"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0"})
    public void processOperation_WrongAmount_ReturnsBadRequestError(String wrongAmount) throws Exception {
        var json = "{" +
                "\"walletId\": \"" + walletId + "\", " +
                "\"operationType\": \"DEPOSIT\", " +
                "\"amount\": \"" + wrongAmount + "\"" +
                "}";

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("{amount=Amount must be positive}"));
    }
}
