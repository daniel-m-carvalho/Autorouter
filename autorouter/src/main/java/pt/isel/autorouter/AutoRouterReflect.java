package pt.isel.autorouter;

import pt.isel.autorouter.annotations.*;

import java.lang.reflect.*;
import java.util.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.Function;
import java.util.stream.Stream;

public class AutoRouterReflect  {
    private static final Map<Class<?>, Function<List<Map<String,String>>, Map<String,String>>> annotationMap = new HashMap<>(){
        {
            put(ArRoute.class, list -> list.get(0));
            put(ArQuery.class, list -> list.get(1));
            put(ArBody.class, list -> list.get(2));
        }
    };

    private static final Map<Class<?>, Function<String, Object>> primitiveMap = new HashMap<>() {
        {
            put(int.class, Integer::parseInt);
            put(Integer.class, Integer::parseInt);
            put(Float.class, Float::parseFloat);
            put(Double.class, Double::parseDouble);
            put(Short.class, Short::parseShort);
            put(Byte.class, Byte::parseByte);
            put(Long.class, Long::parseLong);
            put(Boolean.class, Boolean::parseBoolean);
            put(Character.class, s -> s.charAt(0));
        }
    };

    public static Stream<ArHttpRoute> autorouterReflect(Object controller) {
        Class<?> cls = controller.getClass();
        Method[] declaredMethods = cls.getDeclaredMethods();
        Stream<Method> methods = Arrays.stream(declaredMethods)
                .filter(m -> m.isAnnotationPresent(AutoRoute.class) && m.getReturnType() == Optional.class);
        return methods.map(m -> createArHttpRoute(controller, m));
    }

    private static ArHttpRoute createArHttpRoute(Object target, Method m) {
        String functionName = m.getName();
        ArVerb method = m.getAnnotation(AutoRoute.class).method();
        String path = m.getAnnotation(AutoRoute.class).value();
        ArRetType retType = m.getAnnotation(AutoRoute.class).type();
        List<Parameter> parameters = Arrays.asList(m.getParameters());

        final List<ArgumentCreator> argumentCreators = parameters.stream().map(param -> {
            try {
                return new ArgumentCreator(param);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        ArHttpHandler handler = (routeArgs, queryArgs, bodyArgs) -> {
            try {
                Object[] argValues = argumentCreators.stream()
                        .map(av -> av.createArguments(routeArgs,queryArgs, bodyArgs)).toArray();
                // Iterate over the parameters and extract their values from the request
                Object result = m.invoke(target,argValues);
                return (Optional<?>) result;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
        };
        return new ArHttpRoute(functionName, method, path, handler,retType);
    }

    private static class ArgumentCreator{
        interface ArgumentCreatorFunction extends Function<Map<String,String>, Object> { } //typeAlias

        private final Function<List<Map<String,String>>, Map<String,String>> mapSelector;

        private final ArgumentCreatorFunction argumentCreatorFunction;

        public ArgumentCreator(Parameter param) throws IllegalAccessException {
            var arAnnotations = Stream.of(param.getAnnotations())
                    .map(Annotation::annotationType)
                    .filter(a -> (a == ArRoute.class || a == ArQuery.class || a == ArBody.class)).toList();
            if (arAnnotations.size() != 1){
                throw new IllegalAccessException();
            }
            String pName = param.getName();
            Class<?> pType = param.getType();

            mapSelector = annotationMap.get(arAnnotations.get(0));
            argumentCreatorFunction = getArgumentCreatorFor(pType, pName);
        }

        private ArgumentCreatorFunction getArgumentCreatorFor(Class<?> type, String argName) {
            return type.isPrimitive() ? getPrimitiveType(type, argName) : getComplexType(type, argName);
        }

        public Object createArguments(  Map<String,String> routeArgs,
                                        Map<String,String> queryArgs,
                                        Map<String,String> bodyArgs
                                      ){
            return getAnnotationMap(mapSelector, Stream.of(routeArgs, queryArgs, bodyArgs).toList());
        }

        private ArgumentCreatorFunction getPrimitiveType(final Class<?> type, String argName){
            return (m) -> {
                String argumentValueStr = m.get(argName);
                return getPrimitiveValue(type, argumentValueStr);
            };
        }

        private ArgumentCreatorFunction getComplexType(final Class<?> type, String argName){
            if (type == String.class){
                return map -> map.get(argName);
            }
            Constructor<?>[] constructors = type.getConstructors();
            if (constructors.length > 1)
                throw new RuntimeException("Class " + type + " can't be used!");
            Constructor<?> constructor = constructors[0];
            final List<ArgumentCreatorFunction> argCreators = Arrays.stream(constructor.getParameters())
                    .map(p -> getArgumentCreatorFor(p.getType(), p.getName())).toList();
            return map ->{
                Object[] args = argCreators.stream().map(arg -> arg.apply(map)).toArray();
                try {
                    return constructor.newInstance(args);
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
            };
        }
        private Object getAnnotationMap(
                Function<List<Map<String,String>>, Map<String,String>> mapSelector,
                List<Map<String,String>> value
                ) {
            return argumentCreatorFunction.apply(mapSelector.apply(value));
        }
        private static Object getPrimitiveValue(Class<?> type, String value) {
            return primitiveMap.get(type).apply(value);
        }
    }
}




