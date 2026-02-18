package com.resale.resaleuser.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resale.resaleuser.model.ActionType;
import com.resale.resaleuser.model.UserExceptionLog;
import com.resale.resaleuser.model.UserLog;
import com.resale.resaleuser.repository.UserExceptionLogRepository;
import com.resale.resaleuser.repository.UserLogRepository;
import com.resale.resaleuser.security.CustomUserPrincipal;
import com.resale.resaleuser.utils.RequestBodyCachingFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserLoggingAspect {

    private final UserLogRepository userLogRepository;
    private final UserExceptionLogRepository userExceptionLogRepository;
    private final HttpServletRequest request;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(logActivity)")
    public Object log(ProceedingJoinPoint joinPoint, LogActivity logActivity) throws Throwable {

        long start = System.currentTimeMillis();
        String httpMethod = request.getMethod();

        ActionType action = logActivity.value();
        Integer actionCode = action.getCode();
        String actionName = action.name();

        Integer userId = resolveUserId();

        String headersJson = extractHeaders();
        String paramsJson = extractQueryParams();
        String requestBodyJson = extractRequestBody();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            int status = 200;
            if (result instanceof ResponseEntity<?> res) {
                status = res.getStatusCode().value();
            }

            UserLog logEntity = new UserLog();
            logEntity.setActionCode(actionCode);
            logEntity.setActionName(actionName);
            logEntity.setUserId(userId);
            logEntity.setHttpMethod(httpMethod);
            logEntity.setStatusCode(status);
            logEntity.setHeaders(headersJson);
            logEntity.setQueryParams(paramsJson);
            logEntity.setRequestBody(requestBodyJson);
            logEntity.setResponseBody(toJson(result));
            logEntity.setExecutionTimeMs(executionTime);
            logEntity.setCreatedAt(LocalDateTime.now());

            userLogRepository.save(logEntity);

            return result;

        } catch (Exception ex) {

            int status = resolveHttpStatus(ex);
            long executionTime = System.currentTimeMillis() - start;

            if (status == 500) {

                UserExceptionLog exceptionLog = new UserExceptionLog();
                exceptionLog.setActionCode(actionCode);
                exceptionLog.setActionName(actionName);
                exceptionLog.setUserId(userId);
                exceptionLog.setHttpMethod(httpMethod);
                exceptionLog.setExceptionType(ex.getClass().getSimpleName());
                exceptionLog.setMessage(ex.getMessage());
                exceptionLog.setStacktrace(getStackTrace(ex));
                exceptionLog.setHeaders(headersJson);
                exceptionLog.setQueryParams(paramsJson);
                exceptionLog.setCreatedAt(LocalDateTime.now());

                userExceptionLogRepository.save(exceptionLog);

            } else {
                UserLog logEntity = new UserLog();
                logEntity.setActionCode(actionCode);
                logEntity.setActionName(actionName);
                logEntity.setUserId(userId);
                logEntity.setHttpMethod(httpMethod);
                logEntity.setStatusCode(status);
                logEntity.setHeaders(headersJson);
                logEntity.setQueryParams(paramsJson);
                logEntity.setRequestBody(requestBodyJson);
                logEntity.setResponseBody(ex.getMessage());
                logEntity.setExecutionTimeMs(executionTime);
                logEntity.setCreatedAt(LocalDateTime.now());

                userLogRepository.save(logEntity);
            }

            throw ex;
        }
    }

    private Integer resolveUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserPrincipal principal) {
            return principal.getId().intValue();
        }

        return null;
    }

    private String extractRequestBody() {
        try {
            Object cached = request.getAttribute(RequestBodyCachingFilter.CACHED_REQUEST);

            if (!(cached instanceof ContentCachingRequestWrapper wrapper)) {
                return null;
            }

            byte[] content = wrapper.getContentAsByteArray();
            if (content.length == 0) {
                return null;
            }

            return new String(content, StandardCharsets.UTF_8);

        } catch (Exception e) {
            return null;
        }
    }


    private String extractHeaders() {
        try {
            Map<String, String> headers = new LinkedHashMap<>();
            Enumeration<String> names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                headers.put(name, request.getHeader(name));
            }
            return objectMapper.writeValueAsString(headers);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractQueryParams() {
        try {
            return objectMapper.writeValueAsString(request.getParameterMap());
        } catch (Exception e) {
            return null;
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    private String getStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : t.getStackTrace()) {
            sb.append(ste).append("\n");
        }
        return sb.toString();
    }

    private boolean isClientException(Exception ex) {
        return ex instanceof org.springframework.security.core.AuthenticationException
                || ex instanceof MethodArgumentNotValidException
                || ex instanceof IllegalArgumentException
                || ex instanceof HttpMessageNotReadableException;
    }

    private Integer resolveHttpStatus(Exception ex) {

        if (ex instanceof org.springframework.web.server.ResponseStatusException rse) {
            return rse.getStatusCode().value();
        }

        var responseStatus = ex.getClass().getAnnotation(
                org.springframework.web.bind.annotation.ResponseStatus.class
        );
        if (responseStatus != null) {
            return responseStatus.value().value();
        }

        if (isClientException(ex)) {
            return 400;
        }

        return 500;
    }
}


