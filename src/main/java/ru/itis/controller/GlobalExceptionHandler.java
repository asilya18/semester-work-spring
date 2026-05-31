package ru.itis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.itis.dto.ErrorResponse;
import ru.itis.exception.ForbiddenException;
import ru.itis.exception.NotFoundException;

import java.io.IOException;

@ControllerAdvice // следит за всеми контроллерами
@Slf4j // lombok- аннотация добавляет поле log в класс
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;
    // инструмент jackson для конвертации обьектов в json

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(NotFoundException ex,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws IOException {
        log.error("not found: {}", ex.getMessage(), ex);
        // третий аргумент добавляет в лог полный стектрейс
        if (isAjax(request)) {
            return writeJson(response, HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        }
        return errorPage("error/404", ex.getMessage());
    }


    @ExceptionHandler(ForbiddenException.class)
    public ModelAndView handleForbidden(ForbiddenException ex,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws IOException {
        log.error("forbidden: {}", ex.getMessage(), ex);
        if (isAjax(request)) {
            return writeJson(response, HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
        }
        return errorPage("error/403", ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneral(Exception ex,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws IOException {
        log.error("unexpected error: {}", ex.getMessage(), ex);
        if (isAjax(request)) {
            return writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "внутренняя ошибка сервера");
        }
        return errorPage("error/500", "внутренняя ошибка сервера");
    }

    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
    // браузер при ajax добавляет header X-Requested-With: XMLHttpRequest

    private ModelAndView writeJson(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(message));
        return null;
    }

    private ModelAndView errorPage(String view, String message) {
        ModelAndView mav = new ModelAndView(view);
        mav.addObject("errorMessage", message);
        return mav;
        // modelAndView - обьект с именем шаблона и данными для него
        // аналогия model для jsp
    }
}