package org.sid.authservice.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@Component
public class EndpointLoggingListener {

    private static final Logger logger = LoggerFactory.getLogger(EndpointLoggingListener.class);

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @EventListener(ApplicationReadyEvent.class)
    public void logEndpoints() {
        logger.info("✅ APPLICATION STARTED SUCCESSFULLY ✅");
        logger.info("Available endpoints:");
        
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        
        handlerMethods.forEach((mapping, method) -> {
            logger.info("{} - Controller: {}", mapping, method.getMethod().getDeclaringClass().getSimpleName());
        });
        
        logger.info("Total endpoints: {}", handlerMethods.size());
    }
} 