package com.facts.financial_facts_service.configurations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class RateLimitFilter extends GenericFilterBean {

    private String whitelist;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println(request);
        System.out.println(request.getRemoteAddr());
        chain.doFilter(request, response);
    }

}
