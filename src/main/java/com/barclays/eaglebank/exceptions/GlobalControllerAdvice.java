package com.barclays.eaglebank.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<BadRequestErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ) {
        List<BadRequestErrorDetail> details = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            details.add(new BadRequestErrorDetail(fe.getField(), fe.getDefaultMessage(), "validation"));
        }
        for (ObjectError oe : ex.getBindingResult().getGlobalErrors()) {
            details.add(new BadRequestErrorDetail(oe.getObjectName(), oe.getDefaultMessage(), "validation"));
        }
        var body = new BadRequestErrorResponse("Invalid details supplied", details);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<BadRequestErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        var details = ex.getConstraintViolations().stream().map(v ->
                new BadRequestErrorDetail(
                        String.valueOf(v.getPropertyPath()),
                        v.getMessage(),
                        "validation"
                )
        ).toList();
        var body = new BadRequestErrorResponse("Invalid details supplied", details);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorisedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiErrorResponse> unauthorized(UnauthorisedException ex) {
        return new ResponseEntity<>(new ApiErrorResponse("Access token is missing or invalid"),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<BadRequestErrorResponse> unreadable(HttpMessageNotReadableException ex) {
        var details = List.of(new BadRequestErrorDetail("body", "Malformed JSON", "parsing"));
        var body = new BadRequestErrorResponse("Invalid details supplied", details);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiErrorResponse> forbidden(ForbiddenException ex) {
        var body = new ApiErrorResponse(
                (ex.getMessage() == null || ex.getMessage().isBlank()) ? "Forbidden" : ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiErrorResponse> notFound(NotFoundException ex) {
        var body = new ApiErrorResponse(
                (ex.getMessage() == null || ex.getMessage().isBlank()) ? "Resource was not found" : ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiErrorResponse> conflict(ConflictException ex) {
        var body = new ApiErrorResponse(
                (ex.getMessage() == null || ex.getMessage().isBlank()) ? "Conflict occurred" : ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnprocessableException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ApiErrorResponse> handleUnprocessable(UnprocessableException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(ex.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiErrorResponse> handleServerError(final Throwable e, HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/v3/api-docs") || 
            request.getRequestURI().startsWith("/swagger-ui")) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(new ApiErrorResponse("An unexpected error occurred"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public record ApiErrorResponse(String message) {}
    public record BadRequestErrorDetail(String field, String message, String type) {}
    public record BadRequestErrorResponse(String message, List<BadRequestErrorDetail> details) {}
}