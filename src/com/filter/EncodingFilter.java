package com.filter;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class EncodingFilter
 */
@WebFilter("/EncodingFilter")
public class EncodingFilter implements Filter {
	
	
	/**
	 * @see 过滤器业务处理方法：处理的公用业务逻辑操作
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		
		// 转型
		final HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		// 一、处理公用业务
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");                   //post提交有效
		
		//出现get中文乱码，是因为在request.getParameter方法内没有进行提交方式判断并处理
		//解决一：重写，但是要重写每个方法
		//解决二：对指定接口的某一个方法进行功能扩展，可以使用代理！ 对request对象(目标对象)，创建代理对象！
		
		HttpServletRequest proxy = (HttpServletRequest)Proxy.newProxyInstance(
				request.getClass().getClassLoader(), // 指定当前使用的类加载器
				new Class[]{HttpServletRequest.class}, // 对目标对象实现的接口类型
				new InvocationHandler() { 	//// 事件处理器
					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args)
							throws Throwable {
					    //定义方法返回值
						Object returnValue = null;
						//获取方法名
						String methodName = method.getName();
						//判断：// 判断：对getParameter方法进行GET提交中文处理
						if ("getParameter".equals(methodName)) {
							// 获取请求数据值【 <input type="text" name="userName">】
							String value = request.getParameter(args[0].toString());	// 调用目标对象的方法	
							// 获取提交方式
							String methodSubmit = request.getMethod(); // 直接调用目标对象的方法
							if("GET".equals(methodSubmit)){
								if(value != null && !"".equals(value.trim())){
									value =new String(value.getBytes("ISO8859-1"),"UTF-8");
								}
								
							}
							return value;
						}
						else {
							// 执行request对象的其他方法
							returnValue = method.invoke(request, args);
						}
						return returnValue;
					}
				});
		
		//二，放行
		chain.doFilter(proxy, response);   // 传入代理对象
	}

	
	

    /**
     * Default constructor. 
     */
    public EncodingFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		System.out.println("过滤器销毁");
	}

	
	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		System.out.println("过滤器初始化");
	}

}
