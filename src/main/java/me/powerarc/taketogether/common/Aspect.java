package me.powerarc.taketogether.common;

import me.powerarc.taketogether.event.EventResource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;

@Component
@org.aspectj.lang.annotation.Aspect
public class Aspect {

    @Around(value = "@annotation(MethodExecutionTime)")
    public synchronized Object setClazz(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        System.out.println(joinPoint.getSignature().getName() + " 소요시간:" + (System.currentTimeMillis() - start));
        return proceed;
    }
}
