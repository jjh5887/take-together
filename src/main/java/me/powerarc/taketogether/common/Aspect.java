package me.powerarc.taketogether.common;

import me.powerarc.taketogether.event.EventResource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;

@Component
@org.aspectj.lang.annotation.Aspect
public class Aspect {

    @Around("execution(* me.powerarc.taketogether..*.*Controller.*(..))")
    public synchronized Object setClazz(ProceedingJoinPoint joinPoint) throws Throwable {
        EventResource.setClazz(joinPoint.getTarget().getClass());
        return joinPoint.proceed();
    }
}
