package com.zyr.springmvc.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * @author zhouguangming
 * 创建时间: 2014年10月10日 下午10:42:04
 */
public class SingletonTestControllerTest {
    private Server jettyServer;
	
	@Before
	public void before() throws Exception {
		WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setParentLoaderPriority(true);
        webAppContext.setResourceBase("src/test/com/zyr/springmvc/controller/SingletonTestController/webapp");

        jettyServer = new Server(8080);
        jettyServer.setHandler(webAppContext);
        jettyServer.start();
	}
	
	@After
	public void after() throws Exception {
		jettyServer.stop();
	}
	
	
	@Test
	public void test() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(4);
		Random  random = new Random();
		
		
		List<Future<Result>> resultList = new ArrayList<>();
//		for (int num = 0; num < 1; num++) { //如果num(即"用户数")为1的话,是没问题的,
		for (int num = 0; num < 4; num++) {
			String nextInt = String.valueOf(random.nextInt(10000));
			Future<Result> resultTem = executor.submit(new Task(nextInt));
			
			resultList.add(resultTem);
		}		
		
		for (Future<Result> future : resultList) {
			Result result = future.get();
			System.out.println("expected=" + result.getExpected() + "; actual=" + result.getActual());
			//如果不会产生并发问题:result.getExpected() == result.getActual() 应该为true			
			assertEquals(result.getExpected(), result.getActual()); 
		}		
	}
	
	private static class Task implements Callable<Result> {
		/**
		 * 用于模拟页面中需要传递到后台的值
		 */
		private final String count;
		public Task(String count) {
			this.count = count;
		}
		
		@Override
		public Result call() {
			WebClient webClient = new WebClient();
			webClient.getOptions().setTimeout(0);
			UnexpectedPage textPage = null;
			try {
				//访问 SingletonTestController 类的test方法
				textPage = webClient.getPage("http://localhost:8080/test/" + count);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Result result = new Result(count, textPage.getWebResponse().getContentAsString());
			return result;
		}
	}
	
	private static class Result {
		/**
		 * 期望值
		 */
		private final String expected;
		/**
		 * 实际值
		 */
		private final String actual;

		public Result(String expected, String actual) {
			this.expected = expected;
			this.actual = actual;
		}
		
		public String getExpected() {
			return expected;
		}
		
		public String getActual() {
			return actual;
		}
	}
}
