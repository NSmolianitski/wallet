package nsmol.wallet.wallet;

import jakarta.validation.Valid;
import nsmol.wallet.wallet.exceptions.InsufficientFundsException;
import nsmol.wallet.wallet.exceptions.WalletNotFoundException;
import nsmol.wallet.wallet.requests.OperationRequest;
import nsmol.wallet.wallet.responses.WalletBalanceResponse;
import nsmol.wallet.wallet.responses.WalletErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("wallets/{id}")
    public ResponseEntity<?> getBalance(@PathVariable UUID id) {
        try {
            var wallet = walletService.getBalance(id);
            return ResponseEntity.ok(new WalletBalanceResponse(
                    HttpStatus.OK.toString(),
                    "Balance retrieved successfully",
                    wallet.balance()));
            
        } catch (WalletNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new WalletErrorResponse(HttpStatus.NOT_FOUND.toString(), "Wallet not found"));
        }
    }

    @PostMapping("wallet")
    public ResponseEntity<?> processOperation(@Valid @RequestBody OperationRequest request) {
        var operationType = request.operationType();
        Wallet wallet;

        try {
            if (operationType == OperationType.DEPOSIT) {
                wallet = walletService.deposit(request.walletId(), request.amount());
                
            } else if (operationType == OperationType.WITHDRAW) {
                wallet = walletService.withdraw(request.walletId(), request.amount());
                
            } else {
                return ResponseEntity.badRequest()
                        .body(new WalletErrorResponse(HttpStatus.BAD_REQUEST.toString(), "Invalid operation type"));
            }
            
            return ResponseEntity.ok(new WalletBalanceResponse(
                    HttpStatus.OK.toString(),
                    "Operation successful",
                    wallet.balance()));
            
        } catch (WalletNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new WalletErrorResponse(HttpStatus.NOT_FOUND.toString(), "Wallet not found"));
            
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                    .body(new WalletErrorResponse(HttpStatus.PAYMENT_REQUIRED.toString(), "Insufficient funds"));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new WalletErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Internal server error"));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WalletErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest()
                .body(new WalletErrorResponse(HttpStatus.BAD_REQUEST.toString(), errors.toString()));
    }
}
