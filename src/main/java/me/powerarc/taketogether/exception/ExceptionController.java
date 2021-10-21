package me.powerarc.taketogether.exception;

import me.powerarc.taketogether.account.AccountController;
import me.powerarc.taketogether.event.EventController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = {AccountController.class, EventController.class})
public class ExceptionController {

    @ExceptionHandler
    public ResponseEntity errorHandler(Exception exception) {
        return ResponseEntity.badRequest()
                .body(ExceptionResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage()).build());
    }
}
