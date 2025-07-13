package com.example.demo.audit;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.User.User;
import com.example.demo.User.UserRepo;
import com.example.demo.config.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuditInterceptor implements HandlerInterceptor {

    @Autowired private AuditRepo auditRepo;
    @Autowired private UserRepo userRepo;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,@Nullable Exception ex) throws Exception {
        
        System.out.println("In afterCompletion");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable ModelAndView modelAndView) throws Exception {
        String userName = (String) request.getAttribute("username"); 
        String bookName = (String) request.getAttribute("bookname");
        
        User user = userRepo.findByUsername(userName);

        Audit audit = new Audit();
        audit.setUsername(userName);
        audit.setBookname(bookName);
        audit.setEmailSent(false);
        audit.setEmail(user.getEmail());
        audit.setAccessedAt(LocalDateTime.now());
        auditRepo.save(audit);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        return true;
    }
    
}
