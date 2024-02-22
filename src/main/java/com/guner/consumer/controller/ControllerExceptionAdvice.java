package com.guner.consumer.controller;

import com.guner.consumer.exception.MessageNotSuitableException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ControllerExceptionAdvice {

    private static final String TIMESTAMP = "timestamp";
    private static final String MESSAGE = "message";
    private static final String SERVICE_NAME = "serviceName";
    private static final String PATH = "path";

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> methodArgumentTypeMismatchExceptionHandler(
            MethodArgumentTypeMismatchException methodArgumentTypeMismatchException, HandlerMethod handlerMethod,
            HttpServletRequest request) {
        log.error("MethodArgumentTypeMismatchException occurred: ", methodArgumentTypeMismatchException);
        return getStringObjectMap(methodArgumentTypeMismatchException, handlerMethod, request);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> missingServletRequestParameterExceptionHandler(
            MissingServletRequestParameterException missingServletRequestParameterException,
            HandlerMethod handlerMethod, HttpServletRequest request) {
        log.error("MissingServletRequestParameterException occurred: ", missingServletRequestParameterException);
        return getStringObjectMap(missingServletRequestParameterException, handlerMethod, request);
    }

    @ExceptionHandler(value = MessageNotSuitableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> resourceNotFoundExceptionHandler(MessageNotSuitableException messageNotSuitableException,
                                                                HandlerMethod handlerMethod, HttpServletRequest request) {
        log.error("ResourceNotFoundException occurred: ", messageNotSuitableException);
        return getStringObjectMap(messageNotSuitableException, handlerMethod, request);
    }


    @ExceptionHandler(value = RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> runtimeExceptionHandler(RuntimeException runtimeException,
                                                       HandlerMethod handlerMethod, HttpServletRequest request) {
        log.error("RuntimeException occurred: ", runtimeException);
        return getStringObjectMap(runtimeException, handlerMethod, request);
    }

    private Map<String, Object> getStringObjectMap(Exception exception, HandlerMethod handlerMethod,
            HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(MESSAGE, exception.getMessage());
        body.put(SERVICE_NAME, handlerMethod.getMethod().getName());
        body.put(PATH, request.getRequestURI());
        return body;
    }
}
