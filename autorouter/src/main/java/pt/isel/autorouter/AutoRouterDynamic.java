package pt.isel.autorouter;

import org.cojen.maker.ClassMaker;
import org.cojen.maker.FieldMaker;
import org.cojen.maker.MethodMaker;
import org.cojen.maker.Variable;
import pt.isel.autorouter.annotations.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

public class AutoRouterDynamic {

    private static final Map<Class<?>, Integer> annotationMap = new HashMap<>(){
        {
            put(ArRoute.class, 0);
            put(ArQuery.class, 1);
            put(ArBody.class, 2);
        }
    };

    private static final Map<Class<?>, Class<?>> primitiveMap = new HashMap<>() {
        {
            put(int.class, Integer.class);
            put(Integer.class, Integer.class);
            put(float.class, Float.class);
            put(double.class, Double.class);
            put(short.class, Short.class);
            put(byte.class, Byte.class);
            put(long.class, Long.class);
            put(boolean.class, Boolean.class);
            put(char.class, Character.class);
        }
    };
    public static Stream<ArHttpRoute> autorouterDynamic(Object controller) {
        var controllerClass = controller.getClass();
        return Arrays.stream(controllerClass.getDeclaredMethods()).filter(
                    m -> m.isAnnotationPresent(AutoRoute.class) && m.getReturnType() == Optional.class
                ).map(
                    method -> createArrHttpRouteDynamic(controller, controllerClass, method)
                );
    }

    private static ArHttpRoute createArrHttpRouteDynamic(Object controller, Class<?> controllerClass, Method method) {
        ClassMaker classMaker = buildHandler(controllerClass, method);
        Class<?> handlerClass = classMaker.finish();
        try {
            Constructor<ArHttpHandler> constructor = (Constructor<ArHttpHandler>)
                    handlerClass.getConstructor(controllerClass);
            ArHttpHandler handler =  constructor.newInstance(controller);
            return new ArHttpRoute(
                    method.getName(),
                    method.getAnnotation(AutoRoute.class).method(),
                    method.getAnnotation(AutoRoute.class).value(),
                    handler, method.getAnnotation(AutoRoute.class).type());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static ClassMaker buildHandler(Class<?> controllerClass, Method controllerFun) {
        String funName = controllerFun.getName();
        /*
        * Begin creation of a classMaker that implements ArHttpHandler.
        * */
        ClassMaker classMaker = ClassMaker.begin().public_().implement(ArHttpHandler.class);
        /*
        * Add field controller to the class.
        * */
        FieldMaker fieldMaker = classMaker.addField(controllerClass, "controller").private_().final_();
        /*
        * Create a contructor for the class and set field
        * with value passed in constructor's parameters.
        **/
        MethodMaker constructor = classMaker.addConstructor(controllerClass).public_();
            constructor.invokeSuperConstructor();
            constructor.field("controller").set(constructor.param(0));
        /*
        * Add method handle to the class. In this case
        * we can pass the (Object... paramterTypes)
        * hard coded because its always the same number
        * of parameters.
        * */
        MethodMaker methodMaker = classMaker.addMethod(Optional.class, "handle", Map.class, Map.class, Map.class).public_();
        /*
        * Get the the right parameter of the function methodMaker,
        * by geting the anotation of the parameter of fun and
        * create a metod get associated to the function methodMaker
        * */
        Object[] args = Arrays.stream(controllerFun.getParameters()).map(
                parameter-> parameterToVariable(parameter, methodMaker)
        ).toArray();
        methodMaker.return_(methodMaker.field("controller").invoke(funName, args));
        return classMaker;
    }

    private static Integer getAnnotationMap(Class<?> type) {
        return annotationMap.get(type);
    }

    private static Class<?> getPrimitiveValue(Class<?> type) {
        return primitiveMap.get(type);
    }

    private static Variable parameterToVariable(Parameter parameter, MethodMaker methodMaker){
        Class<?> paramType = parameter.getType();
        String paramName = parameter.getName();
        Variable map = methodMaker.param(getAnnotationMap(parameter.getAnnotations()[0].annotationType()));
        Variable res = map.invoke("get", paramName).cast(String.class);
        return parseTypes(paramType, res, methodMaker, map);
    }

    private static Variable getComplexArg(Class<?> clazz, Variable map ,MethodMaker methodMaker) {
        /*
        * Get the declared constructors of the Complex param
        * and check size. Has to be < 1.
        * */
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length > 1)
            throw new RuntimeException("Class " + clazz + " can't be used!");
        /*
        * After checking de declaredConstructor size
        * get the constructor.
        * */
        Constructor<?> constructor = constructors[0];
        /*
        * Transform the consturctor's getParameters
        * to an array of variables with values parsed
        * to the right type
        * */
        Object[] finalArgs = Arrays.stream(constructor.getParameters()).map(
            parameter -> {
                Class<?> paramType = parameter.getType();
                String paramName = parameter.getName();
                Variable res = map.invoke("get", paramName).cast(String.class);
                return parseTypes(paramType, res, methodMaker, map);
            }
        ).toArray();
        /*
        * Create a new instance of the constructor
        * dynamicly with the final args "right"
        * */
        return  methodMaker.new_(clazz, finalArgs);
    }

    private static String getPrimitiveName(Class<?> paramType) {
        return paramType.getName().toUpperCase().charAt(0) + paramType.getName().substring(1);
    }

    private static Variable parseTypes(Class<?> paramType,Variable result, MethodMaker methodMaker, Variable map){
        if(paramType == String.class){
            return result;
        }
        if(paramType.isPrimitive()) {
            Class<?> parser = getPrimitiveValue(paramType);
            return methodMaker.var(parser).invoke("parse" + getPrimitiveName(paramType), result);
        }
        else return getComplexArg(paramType, map, methodMaker);
    }
}
