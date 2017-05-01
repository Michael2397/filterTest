package com.filter;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

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
 * Servlet Filter implementation class DataFilter
 */
@WebFilter("/DataFilter")
public class DataFilter implements Filter {
	//初始化无效数据
	private List<String> dirtyData;
    /**
     * Default constructor. 
     */
    public DataFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		System.out.println("执行filter");
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
					
							// 中文数据已经处理完： 下面进行无效数据过滤   
							//【如何value中出现dirtyData中数据，用****替换】  
							for (String data : dirtyData) {
								// 判断当前输入数据(value), 是否包含无效数据
								if (value.contains(data)){
									value = value.replace(data, "*****");
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
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// 模拟几个数据
		dirtyData = new ArrayList<String>();
		dirtyData.add("NND");
		dirtyData.add("炸使馆");
	}

}
