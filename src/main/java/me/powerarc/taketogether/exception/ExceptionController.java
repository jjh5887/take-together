package me.powerarc.taketogether.exception;

import me.powerarc.taketogether.account.AccountController;
import me.powerarc.taketogether.event.EventController;
import me.powerarc.taketogether.location.LocationController;
import me.powerarc.taketogether.route.RouteController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = {AccountController.class, EventController.class, LocationController.class, RouteController.class})
public class ExceptionController {

    @ExceptionHandler
    public ResponseEntity errorHandler(WebException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(ExceptionResponse.builder()
                        .status(exception.getStatus())
                        .message(exception.getMessage()).build())
                ;
    }

    @ExceptionHandler
    public ResponseEntity errorHandler(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getCause().getMessage()).build())
                ;
    }
}
