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
		for (int num = 0; num < 4; num++) {
			String nextInt = String.valueOf(random.nextInt(10000));
			Future<Result> resultTem = executor.submit(new Task(nextInt));
			
			resultList.add(resultTem);
		}
		
		System.out.println("test");
		
		for (Future<Result> future : resultList) {
			Result result = future.get();
			System.out.println("expected=" + result.getExpected() + "; actual=" + result.getActual());
			assertEquals(result.getExpected(), result.getActual());
		}
		
		/*
		List<Result> resultList = new ArrayList<>();
		for (int num = 0; num < 6; num++) {
			String nextInt = String.valueOf(random.nextInt(10000));
			Future<Result> resultTem = executor.submit(new Task(nextInt));
			
			resultList.add(resultTem.get());
		}
		
		System.out.println("test");
		
		for (Result result : resultList) {
			assertEquals(result.getExpected() , result.getActual());
			System.out.println("expected=" + result + "" + result.getActual());
		}
		*/
	}
	
	private static class Task implements Callable<Result> {
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
				textPage = webClient.getPage("http://localhost:8080/test/" + count);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Result result = new Result(count, textPage.getWebResponse().getContentAsString());
			return result;
		}
	}
	
	private static class Result {
		private final String expected;
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
