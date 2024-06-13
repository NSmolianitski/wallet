package nsmol.wallet.wallet;

import nsmol.wallet.wallet.responses.WalletErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = WalletController.class)
public class WalletControllerAdvice {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<WalletErrorResponse> handleValidationExceptions(Exception ex) {
        var response = new WalletErrorResponse(
                HttpStatus.BAD_REQUEST.toString(),
                "Wrong JSON format"
        );
        return ResponseEntity.badRequest().body(response);
    }
}
