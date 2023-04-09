package com.todolist.controllers;

import com.todolist.exceptions.ForbiddenException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.exceptions.RequestTimeoutException;
import com.todolist.exceptions.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class MyErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(MyErrorController.class);

    @RequestMapping("/error")
    public void handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            switch (statusCode) {
                case 403 -> throw new ForbiddenException("You are not authorized to access this page!");
                case 404 -> throw new NotFoundException("The page you are looking for does not exist!");
                case 408 -> throw new RequestTimeoutException("The request has timed out!");
                case 500 -> {
                    logger.error(request.getAttribute(RequestDispatcher.ERROR_MESSAGE).toString());
                    throw new ServerException("Something went wrong on the server side!");
                }
                default -> throw new IllegalArgumentException("Unexpected value: " + statusCode + ".");
            }
        }
    }
}
