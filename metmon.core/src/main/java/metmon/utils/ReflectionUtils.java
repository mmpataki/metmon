package metmon.utils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ReflectionUtils {

	public static Constructor<?> getCtor(String className, Class<?> ...argClasses) throws Exception {
		Class<?> clazz = (Class<?>) Class.forName(className);
		return clazz.getConstructor(argClasses);
	}
	
	public static Object getInstance(String className, Object ...args) throws Exception {
		Class<?> argClasses[] = (Class<?>[])Arrays.stream(args).map(a -> a.getClass()).collect(Collectors.toList()).toArray();
		return getCtor(className, argClasses).newInstance(args);
	}
	
}
