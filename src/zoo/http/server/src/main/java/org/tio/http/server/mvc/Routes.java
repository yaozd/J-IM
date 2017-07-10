package org.tio.http.server.mvc;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.server.annotation.RequestPath;
import org.tio.json.Json;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ClassAnnotationMatchProcessor;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.MethodAnnotationMatchProcessor;

/**
 * @author tanyaowu 
 * 2017年7月1日 上午9:05:30
 */
public class Routes {
	private static Logger log = LoggerFactory.getLogger(Routes.class);
//	private HttpServerConfig httpServerConfig = null;

//	private String[] scanPackages = null;

	/**
	 * Controller路径映射
	 * key: /user
	 * value: object
	 */
	public Map<String, Object> pathBeanMap = new HashMap<>();

	/**
	 * 路径和class映射
	 * key: class
	 * value: /user
	 */
	public Map<Class<?>, String> classPathMap = new HashMap<>();

	/**
	 * Method路径映射
	 */
	public Map<String, Method> pathMethodMap = new HashMap<>();

	/**
	 * 方法参数名映射
	 */
	public Map<Method, String[]> methodParamnameMap = new HashMap<>();

	/**
	 * 
	 */
	public Map<Method, Object> methodBeanMap = new HashMap<>();

	/**
	 * 
	 * @author: tanyaowu
	 */
	public Routes(String[] scanPackages) {
//		this.scanPackages = scanPackages;
		
		if (scanPackages != null) {
			final FastClasspathScanner fastClasspathScanner = new FastClasspathScanner(scanPackages);
//			fastClasspathScanner.verbose();
			fastClasspathScanner.matchClassesWithAnnotation(RequestPath.class, new ClassAnnotationMatchProcessor() {
				@Override
				public void processMatch(Class<?> classWithAnnotation) {
					try {
						Object bean = classWithAnnotation.newInstance();
						RequestPath mapping = classWithAnnotation.getAnnotation(RequestPath.class);
						String beanPath = mapping.value();
//						if (!StringUtils.endsWith(beanUrl, "/")) {
//							beanUrl = beanUrl + "/";
//						}

						beanPath = formateBeanPath(beanPath);
						
						Object obj = pathBeanMap.get(beanPath);
						if (obj != null) {
							log.error("mapping[{}] already exists in class [{}]", beanPath, obj.getClass().getName());
						} else {
							pathBeanMap.put(beanPath, bean);
							classPathMap.put(classWithAnnotation, beanPath);
						}
					} catch (Exception e) {
						
						log.error(e.toString(), e);
					}
				}
			});
			
			fastClasspathScanner.matchClassesWithMethodAnnotation(RequestPath.class, new MethodAnnotationMatchProcessor(){
				@Override
				public void processMatch(Class<?> matchingClass, Executable matchingMethodOrConstructor) {
//					log.error(matchingMethodOrConstructor + "");
					RequestPath mapping = matchingMethodOrConstructor.getAnnotation(RequestPath.class);
					
					String methodName = matchingMethodOrConstructor.getName();

					
					String methodPath = mapping.value();
					methodPath = formateMethodPath(methodPath);
					String beanPath = classPathMap.get(matchingClass);
					
					if (StringUtils.isBlank(beanPath)) {
						log.error("方法有注解，但类没注解, method:{}, class:{}", methodName, matchingClass);
						return;
					}
					
					Object bean = pathBeanMap.get(beanPath);
					String completeMethodPath = methodPath;
					if (beanPath != null) {
						completeMethodPath = beanPath + methodPath;
					}
					
					Class<?>[] parameterTypes = matchingMethodOrConstructor.getParameterTypes();
					Method method;
					try {
						method = matchingClass.getMethod(methodName, parameterTypes);
						
						Paranamer paranamer = new BytecodeReadingParanamer();
						String[] parameterNames = paranamer.lookupParameterNames(method, false); // will return null if not found
						
						Method checkMethod = pathMethodMap.get(completeMethodPath);
						if (checkMethod != null) {
							log.error("mapping[{}] already exists in method [{}]", completeMethodPath, checkMethod.getDeclaringClass() + "#" + checkMethod.getName());
							return;
						}
						
						pathMethodMap.put(completeMethodPath, method);
						methodParamnameMap.put(method, parameterNames);
						methodBeanMap.put(method, bean);
					} catch (Exception e) {
						log.error(e.toString(), e);
					} 
				}
			});
			
			fastClasspathScanner.scan();
			
			log.error("\r\n class scan result :\r\n {}\r\n", Json.toJson(pathBeanMap));
			
			log.error("\r\n classPathMap scan result :\r\n {}\r\n", Json.toJson(classPathMap));
			
			log.error("\r\n method scan result :\r\n {}\r\n", Json.toJson(pathMethodMap));
			
			log.error("\r\n methodParamnameMap scan result :\r\n {}\r\n", Json.toJson(methodParamnameMap));
		}
	}
	
	/**
	 * 格式化成"/user","/"这样的路径
	 * @param initPath
	 * @return
	 * @author: tanyaowu
	 */
	private static String formateBeanPath(String initPath){
		if (StringUtils.isBlank(initPath)) {
			return "/";
		}
		initPath = StringUtils.replaceAll(initPath, "//", "/");
		if (!StringUtils.startsWith(initPath, "/")) {
			initPath = "/" + initPath;
		}
		
		if (StringUtils.endsWith(initPath, "/")) {
			initPath = initPath.substring(0, initPath.length() - 1);
		}
		return initPath;
	}
	
	private static String formateMethodPath(String initPath){
		if (StringUtils.isBlank(initPath)) {
			return "/";
		}
		initPath = StringUtils.replaceAll(initPath, "//", "/");
		if (!StringUtils.startsWith(initPath, "/")) {
			initPath = "/" + initPath;
		}
		
		return initPath;
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}
}
