package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Slf4j
@Component
public class AutoFillAspect {
    @Pointcut("execution (* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void AutoFillPointCut() {}
    @Before("AutoFillPointCut()")
    public void autoFillMethods(JoinPoint joinPoint) {
        log.info("执行了自动填充的切面方法");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType type = annotation.value();

        Object[] args = joinPoint.getArgs();
        Object entity = args[0];

        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        if(type==OperationType.INSERT){
            try{Method md = entity.getClass().getMethod("setCreateTime", LocalDateTime.class);
                Method mu = entity.getClass().getMethod("setUpdateTime", LocalDateTime.class);
                Method mcu = entity.getClass().getMethod("setCreateUser", Long.class);
                Method muu =  entity.getClass().getMethod("setUpdateUser", Long.class);
                md.invoke(entity, now);
                mu.invoke(entity, now);
                mcu.invoke(entity, currentId);
                muu.invoke(entity, currentId);
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(type==OperationType.UPDATE){
            try{
                Method yy=entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                Method yuu=entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);
                yy.invoke(entity, now);
                yuu.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }
}
