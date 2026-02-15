package com.resale.homeflyuser.security;

import com.resale.homeflyuser.components.auth.CustomUserDetailsService;
import com.resale.homeflyuser.utils.ReturnObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }

        if (jwt == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                jwt = Arrays.stream(cookies)
                        .filter(c ->
                                c.getName().equals("ADMIN_AUTH_TOKEN") ||
                                        c.getName().equals("SALES_AUTH_TOKEN"))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);
            }
        }

        if (jwt != null) {
            try {
                username = jwtUtil.extractUsername(jwt);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }


            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }


    @Override
        protected boolean shouldNotFilter (HttpServletRequest request){
            String path = request.getRequestURI();

            return path.startsWith("/auth/")
                    || path.startsWith("/user/id");
        }
    }

