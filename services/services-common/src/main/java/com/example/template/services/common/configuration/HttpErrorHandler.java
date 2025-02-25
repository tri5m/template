package com.example.template.services.common.configuration;

import com.example.template.services.common.common.response.ResponseResult;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * @title: 错误页面统一处理类
 * @author: trifolium
 * @date: 2022/1/27
 * @modified :
 */
@Hidden
@RestController
public class HttpErrorHandler implements ErrorController {


//    private final ServerProperties serverProperties;
//
//    public HttpErrorHandler(ServerProperties serverProperties) {
//
//        this.serverProperties = serverProperties;
//    }

    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @RequestMapping("${server.error.path:${error.path:/error}}")
    public ResponseResult<Void> errorJson(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);

        return ResponseResult.failure(status.value() + " : " + status.getReasonPhrase());
    }

}

