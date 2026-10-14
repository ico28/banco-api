package com.franciscoortega.banco_api.common;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage()));

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "La petición contiene datos no válidos");
        problema.setTitle("Validación fallida");
        problema.setProperty("errores", errores);

        return handleExceptionInternal(ex, problema, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleIntegridad(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad de datos", ex);
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "La operación viola una restricción de datos");
        problema.setTitle("Conflicto de datos");
        return problema;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleInesperada(Exception ex) {
        log.error("Error inesperado", ex);
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Se ha producido un error inesperado");
        problema.setTitle("Error interno");
        return problema;
    }
}