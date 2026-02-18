package com.resale.resaleuser.config;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocaleHeaderForwarder implements RequestInterceptor {

    private final HttpServletRequest request;

    @Override
    public void apply(RequestTemplate template) {
        String locale = request.getHeader("locale");
        if (locale != null && !locale.isEmpty()) {
            template.header("locale", locale);
        }
    }
}


