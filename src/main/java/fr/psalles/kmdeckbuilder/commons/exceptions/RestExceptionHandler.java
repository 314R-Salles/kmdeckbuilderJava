package fr.psalles.kmdeckbuilder.commons.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler //extends ResponseEntityExceptionHandler
{

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(Exception ex, WebRequest request) {
        return new ResponseEntity<Object>("unauthorized", null, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(Exception ex, WebRequest request) {
        return new ResponseEntity<Object>("forbidden", null, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<Object> handleTechnicalException(Exception ex, WebRequest request) {
        return new ResponseEntity<Object>("boom", null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(Exception ex, WebRequest request) {
        return new ResponseEntity<Object>(ex.getMessage(), null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinnessException(Exception ex, WebRequest request) {
        return new ResponseEntity<Object>(ex.getMessage(), null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleMalformedException(Exception ex, WebRequest request) {
        return new ResponseEntity<Object>(ex.getMessage(), null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Requête invalide");
        return new ResponseEntity<Object>(message, null, HttpStatus.BAD_REQUEST);
    }

   @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpectedException(Exception ex, WebRequest request) {
        // Exceptions Spring MVC (404, 405, 406...) : erreur du client, on conserve leur statut
        if (ex instanceof ErrorResponse errorResponse) {
            log.debug("Requête rejetée sur {} : {}", request.getDescription(false), ex.getMessage());
            return ResponseEntity.status(errorResponse.getStatusCode()).build();
        }
        log.error("Exception non gérée sur {}", request.getDescription(false), ex);
        return new ResponseEntity<Object>("boom", null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
