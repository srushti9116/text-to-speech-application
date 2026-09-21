package tts_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tts_backend.dto.ApiErrorResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ApiErrorResponse response = new ApiErrorResponse(
                false,
                "Validation failed",
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception) {

        Map<String, String> errors = new HashMap<>();

        String message = exception.getMessage();

        if (message != null && message.startsWith("Unsupported language:")) {

            errors.put("language", message);

        } else if (message != null && message.startsWith("Unsupported voice:")) {

            errors.put("voice", message);

        } else if (message != null
                && message.startsWith("Voice '")) {

            errors.put("voice", message);

        } else {

            errors.put(
                    "request",
                    message != null
                            ? message
                            : "Invalid TTS request."
            );
        }

        ApiErrorResponse response = new ApiErrorResponse(
                false,
                "Invalid TTS request",
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(
            RuntimeException exception) {

        ApiErrorResponse response = new ApiErrorResponse(
                false,
                exception.getMessage() != null
                        ? exception.getMessage()
                        : "Failed to generate speech.",
                new HashMap<>()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}