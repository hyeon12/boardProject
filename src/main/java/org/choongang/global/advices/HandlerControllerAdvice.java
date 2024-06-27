package org.choongang.global.advices;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.choongang.global.Interceptor;
import org.choongang.global.config.annotations.*;
import org.choongang.global.config.containers.BeanContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HandlerControllerAdvice {

    private final HttpServletRequest request;

    public boolean handle(Object controller) {
        Class clazz = controller.getClass();
        String pkName = clazz.getPackageName();

        boolean isRest = Arrays.stream(clazz.getAnnotations()).anyMatch(a -> a instanceof RestController);
        List<Object> advices = getControllerAdvices(isRest);
        List<Object> matchedAdvices = new ArrayList<>();
        for (Object advice : advices) {
            Annotation[] annotations = advice.getClass().getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof ControllerAdvice anno) {
                    boolean isMatched = Arrays.stream(anno.value()).anyMatch(pkName::startsWith);
                    if (isMatched) {
                        matchedAdvices.add(advice);

                    }
                }
            }
        }

        boolean isContinue = true; //controller 실행
        // 매칭된
        if (matchedAdvices != null) {

            //인터셉터 체크
            if(matchedAdvices instanceof Interceptor interceptor){
                isContinue = interceptor.preHandle();
            }

            Method[] methods = matchedAdvices.getClass().getDeclaredMethods();
            for(Method method : methods) {
                for (Annotation anno : method.getDeclaredAnnotations()) {
                    // 공통 유지할 속성 처리 S
                    if (anno instanceof ModelAttribute ma) {
                        try {
                            String name = ma.value().isBlank() ? method.getName() : ma.value().trim();
                            Object value = method.invoke(matchedAdvices);
                            request.setAttribute(name, value);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    // 공통 유지할 속성 처리 E
                } // endfor
            } // endfor
        }

        return isContinue;
    }

    public List<Object> getControllerAdvices(boolean isRest) {

        return BeanContainer.getInstance()
                    .getBeans()
                    .values()
                    .stream()
                    .filter(b -> Arrays.stream(b.getClass().getAnnotations()).anyMatch(a -> (!isRest && a instanceof ControllerAdvice) || (isRest && a instanceof RestControllerAdvice)))
                    .toList();
    }
}
