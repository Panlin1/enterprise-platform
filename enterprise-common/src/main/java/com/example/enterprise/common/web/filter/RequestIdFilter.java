package com.example.enterprise.common.web.filter;

import com.example.enterprise.common.core.context.RequestIdContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Ensures every request has an {@code X-Request-Id} and puts it into MDC + ThreadLocal.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = request.getHeader(RequestIdContext.HEADER_NAME);
        if (!StringUtils.hasText(requestId)) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }

        RequestIdContext.set(requestId);
        MDC.put(MDC_KEY, requestId);
        response.setHeader(RequestIdContext.HEADER_NAME, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            RequestIdContext.clear();
            MDC.remove(MDC_KEY);
        }
    }
}
