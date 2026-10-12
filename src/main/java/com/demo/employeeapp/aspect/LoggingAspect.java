package com.demo.employeeapp.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(com.demo.employeeapp.controller..*) || within(com.demo.employeeapp.service..*)")
    public void applicationPackagePointcut() {}

    @Around("applicationPackagePointcut()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        Object[] args = joinPoint.getArgs();

        logger.info("Entering: {}.{} with arguments: {}", className, methodName, args != null ? java.util.Arrays.toString(args) : "null");

        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("Exiting: {}.{} with result: {} (executed in {} ms)", className, methodName, result, elapsedTime);
            return result;
        } catch (Throwable throwable) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.error("Exception in: {}.{} after {} ms. Exception: {}", className, methodName, elapsedTime, throwable.getMessage());
            throw throwable;
        }
    }
}
