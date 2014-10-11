package com.zyr.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * struts2与springMVC的一个最典型区别是:
 * 在一般情况下,struts2中的Action类的实例是原型(prototype)的,即当用户多次从浏览器访问时Action类的方法时,容器会为每一次
 * 访问都实例化一个Action类的实例,所以Strut2中前端在传值时,可以用Action类的成员变量接收.
 * 而在springMVC中,Controller类的实例是spring容器中的单例(singleton),类似一般情况下的servlet,即
 * 当用户多次从浏览器访问时Controller类的方法时,所有的访问都是公用这个单例的,所以前端在传值时,不可以用Controller类的成员变量来接收值,当然也不可以
 * 在Controller类使用非事实不可变变量, 大家可以为什么想下Controller类的方法中HttpServletRequest 和 HttpServletResponse的变量都是通过
 * 方法参数(即局部变量)提供使用的,原因是一样的
 * 
 * ps:事实不可变变量:即实例化后事实上不会再改变的变量,最典型例子是spring容器负责注入的service,manager,dao等变量
 * 
 * 用于测试非事实不可变变量存在线程安全问题
 * 
 * @author zhouguangming
 *创建时间: 2014年10月10日 下午10:40:04
 */

@Controller
public class SingletonTestController {
	
//	 @Autowired
//	 private XxxxManagerService xxxxManagerService; //xxxxManagerService 是事实不可变变量,不存在线程安全问题

	//TODO number非事实不可变变量,会经常改变,存在线程安全问题
	private String number;
	
	public SingletonTestController() {
		System.err.println("SingletonTestController构造开始.....");
	}
	
	@RequestMapping(value = "test/{count}")
	@ResponseBody
	public String test(@PathVariable("count") String count) throws InterruptedException {
		number = count;
		
		Thread.sleep(500); //为了增大并发产生问题概率 
		
		return number;
	}
}
