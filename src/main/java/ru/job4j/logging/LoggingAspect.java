package ru.job4j.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class.getName());

    @Pointcut("execution(ru.job4j.bmb.services.*.*(..)")
    private void serviceLayer() {

    }

    @Before("serviceLayer()")
    public void logBefore(JoinPoint joinPoint) {
        LOG.info("Вызов метода: " + joinPoint.getSignature().getName());
    }
}
