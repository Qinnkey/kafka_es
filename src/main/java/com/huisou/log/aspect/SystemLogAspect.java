package com.huisou.log.aspect;


import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.huisou.common.ResUtils;
import com.huisou.log.annotation.LogAop;





/**
 * @author caoxt
 * @version 创建时间：2017-07-5 下午9:29:05
 * @desc 切点类 
 */

@Aspect
@Component
public class SystemLogAspect {

    
    private  static  final Logger logger = LoggerFactory.getLogger(SystemLogAspect. class);  
    
    //Controller层切点  
    @Pointcut("execution (* com.huisou.controller..*.*(..))")  
    public  void controllerAspect() {  
    }  
    
    /** 
     * 前置通知 用于拦截Controller层记录用户的操作 
     * 
     * @param joinPoint 切点 
     */ 
    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) {
    	logger.info("==========执行controller前置通知===============");

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        Map map = new LinkedHashMap();
        Enumeration enu=request.getParameterNames();  
        while(enu.hasMoreElements()){  
        	String paraName=(String)enu.nextElement();
        	map.put(paraName, request.getParameter(paraName));
        }
        logger.info("===请求request参数==="+JSON.toJSONString(map));
        //读取session中的用户  
       // User user = (User) session.getAttribute("user");  
        //请求的IP  
        String ip = request.getRemoteAddr();
        String path = request.getServletPath();
        try {  
            
        	String targetName = joinPoint.getTarget().getClass().getName();  
            String methodName = joinPoint.getSignature().getName();  
            Object[] arguments = joinPoint.getArgs();  
            Class targetClass = Class.forName(targetName);  
            Method[] methods = targetClass.getMethods();
            String operationType = "";
            String operationName = "";
            for (Method method : methods) {  
                 if (method.getName().equals(methodName)) {  
                	 Class[] clazzs = method.getParameterTypes();  
                     if (clazzs.length == arguments.length) {  
                    	 LogAop logVo = method.getAnnotation(LogAop.class);
                    	 if(logVo!=null){
                    		 operationType = method.getAnnotation(LogAop.class).operationType();
                             operationName = method.getAnnotation(LogAop.class).operationName();
                             break;
                    	 }
                    }  
                }  
            }
            //*========控制台输出=========*//  
            logger.info("请求方法:" + (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()")+"."+operationType);  
            logger.info("方法描述:" + operationName);  
            logger.info("请求IP:" + ip + "===路径path:"+path);  
            //*========数据库日志=========*//  
        }  catch (Exception e) {  
            //记录本地异常日志  
            logger.error("==后置通知异常==");  
            logger.error("异常信息:{}", e.getMessage());  
        }  
    }    
    
    
    //配置后置返回通知,使用在方法aspect()上注册的切入点
//      @AfterReturning("controllerAspect()")
//      public void afterReturn(JoinPoint joinPoint){
//          System.out.println("=====执行controller后置返回通知=====");  
//              if(logger.isInfoEnabled()){
//                  logger.info("afterReturn " + joinPoint);
//              }
//      }
    
    /** 
     * 异常通知 用于拦截记录异常日志 
     * 
     * @param joinPoint 
     * @param e 
     */  
     @AfterThrowing(pointcut = "controllerAspect()", throwing="e")  
     public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {  
        
         try {  
             
//             String targetName = joinPoint.getTarget().getClass().getName();  
//             String methodName = joinPoint.getSignature().getName();  
//             Object[] arguments = joinPoint.getArgs();  
//             Class targetClass = Class.forName(targetName);  
//             Method[] methods = targetClass.getMethods();
//             String operationType = "";
//             String operationName = "";
//              for (Method method : methods) {  
//                  if (method.getName().equals(methodName)) {  
//                     Class[] clazzs = method.getParameterTypes();  
//                      if (clazzs.length == arguments.length) {  
//                          operationType = method.getAnnotation(LogAop.class).operationType();
//                          operationName = method.getAnnotation(LogAop.class).operationName();
//                          break;  
//                     }  
//                 }  
//             }
            /*========控制台输出=========*/  
        	logger.info("=====异常通知开始====="+e);  
        	logger.info("异常代码:" + e.getClass().getName());  
        	logger.info("异常信息:" + e.getMessage());  
        	logger.info("异常方法:" + (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));  
            /*==========数据库日志=========*/  
        	logger.info("=====异常通知结束=====");  
        }  catch (Exception ex) {  
            //记录本地异常日志  
        	logger.error("==异常通知异常==");  
            logger.error("异常信息:{}", ex.getMessage());  
        }  
         /*==========记录本地异常日志==========*/  
        logger.error("异常方法:{}异常代码:{}异常信息:{}参数:{}", joinPoint.getTarget().getClass().getName() + joinPoint.getSignature().getName(), e.getClass().getName(), e.getMessage(), null);  
        //异常返回
        writeContent(ResUtils.execRes());
    }  
     
	private void writeContent(String content) {
    	HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    	response.reset();
    	response.setCharacterEncoding("UTF-8");
    	response.setHeader("Content-Type", "text/plain;charset=UTF-8");
    	response.setHeader("icop-content-type", "exception");
    	PrintWriter writer = null;
    	try {
    		writer = response.getWriter();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	writer.print(content);
    	writer.flush();
    	writer.close();
     }
    
}
