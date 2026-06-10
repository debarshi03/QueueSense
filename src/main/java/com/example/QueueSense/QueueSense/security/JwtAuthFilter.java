package com.example.QueueSense.QueueSense.security;

import com.example.QueueSense.QueueSense.entity.User;
import com.example.QueueSense.QueueSense.repository.BlacklistedTokenRepository;
import com.example.QueueSense.QueueSense.repository.UserRepository;
import com.example.QueueSense.QueueSense.util.AuthUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.startsWith("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("incoming request: {} ",request.getRequestURI());

        final String requestTokenHandler=request.getHeader("Authorization");
        if(requestTokenHandler==null || !requestTokenHandler.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }

        String token= requestTokenHandler.split("Bearer ")[1];

        if (blacklistedTokenRepository.existsByToken(token)) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token has been revoked"
            );

            return;
        }
        String username=authUtil.ganerateUsernameFromToken(token);

        if (username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            User user=(User) userRepository.findByUsername(username).orElseThrow();
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        filterChain.doFilter(request,response);
    }
}
