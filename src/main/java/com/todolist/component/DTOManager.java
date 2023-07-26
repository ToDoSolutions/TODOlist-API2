package com.todolist.component;

import com.todolist.dtos.ShowEntity;
import com.todolist.dtos.ToJson;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Component
public class DTOManager {
    private final Reflections reflections;

    @Autowired
    public DTOManager() {
        this.reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new MethodAnnotationsScanner())
                .setUrls(ClasspathHelper.forPackage("com.todolist.dtos")));
    }

    public Map<String, Object> getEntityAsJson(ShowEntity entity) {
        String entityName = entity.getClass().getSimpleName();
        Method toJsonMethod = findToJsonMethod(entityName);
        return invokeToJsonMethod(toJsonMethod, entity, null);
    }

    public Map<String, Object> getEntityAsJson(ShowEntity entity, Consumer<String[]> validator, String... fields) {
        String entityName = entity.getClass().getSimpleName();
        validator.accept(fields);
        Method toJsonMethod = findToJsonMethod(entityName);
        return invokeToJsonMethod(toJsonMethod, entity, fields);
    }

    private Method findToJsonMethod(String entityName) {
        Set<Method> methods = reflections.getMethodsAnnotatedWith(ToJson.class);
        for (Method method : methods) {

            Class<?> declaringClass = method.getDeclaringClass();
            if (declaringClass.getSimpleName().equals(entityName)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Could not find toJson method for entity: " + entityName);
    }

    private Map<String, Object> invokeToJsonMethod(Method toJsonMethod, Object entity, String[] fields) {
        try {
            return (Map<String, Object>) toJsonMethod.invoke(entity, fields);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Error invoking toJson method for entity: " + entity.getClass().getSimpleName(), e);
        }
    }
}

