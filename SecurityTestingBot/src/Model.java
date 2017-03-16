import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Model {

	private WebDriver driver;
//	private WebDriverWait driverWait;
//	private JavascriptExecutor jse;
	
	private volatile boolean testRunning = false;
	
	private void setInitiate(){
		if(driver == null){
//			driver = new FirefoxDriver();
			driver = new PhantomJSDriver();
//			driverWait = new WebDriverWait(driver, 30);
//			jse = (JavascriptExecutor)driver;
		}
	}
	
	public void closeDriver(){
		if(driver != null){
			driver.quit();
			driver = null;
		}	
	}
	
	public boolean getTestRunning(){
		return testRunning;
	}
	
	public void setTestRunning(boolean run){
		testRunning = run;
	}
	
//	private void waitForLoad() {
////	    driverWait.until((ExpectedCondition<Boolean>) d ->
////	            ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
//		
//		
//		ExpectedCondition<Boolean> pageLoader = new ExpectedCondition<Boolean>() {
//			boolean status = false;
//			
//
//			public Boolean apply(WebDriver wd){
//				String state = (String)(((JavascriptExecutor) wd).executeScript("return document.readyState"));	
//				
//				
//				
////				String a = ""+(jse.executeScript("var xhttp = new XMLHttpRequest();"
////						+ "return xhttp.getAllResponseHeaders();"
////						+ "if (xhttp.readyState == 4 && xhttp.status == 200) {"
////						+ "return this.responseText;"
////						+ "}"));
////				System.out.println(a);
//				
//				
//				
//				if((!status && state.equals("loading")) || (!status && state.equals("interactive"))){
//					status = true;
//				}else if(status && state.equals("complete")){
//					return true;
//				}
//					
//				return false;
//			}
//		};
//		driverWait.until(pageLoader);
//	}
	
//	public void runSecurityTesting(String url, String framework, HashSet<String> attack){
//	
//		driver.navigate().to(url);
//		
//		FileManager filemanager = new FileManager();
//		ArrayList<String> filepath = filemanager.getSelectedFilePath("all");
//		
//		if(filepath.size() <= 0){
//			System.out.println("File cannot found!!");
//		}else{
//			try {
//				int counterFormTag;
//				int[] counterInputTag;
//				ArrayList<String> script = new ArrayList<String>();
//						
//				BufferedReader reader;
//				String temp;
//				
//				List<WebElement> formTag;
//				List<WebElement> inputTag;
//				
//				for(String path : filepath){				
//					reader = new BufferedReader(new FileReader(path));				
//					while((temp = reader.readLine()) != null){
//						script.add(temp);
//					}
//					reader.close();
//					
//					counterFormTag = 0;
//					counterInputTag = null;
//					
//					while(true){
//						formTag = driver.findElements(By.tagName("form"));
//						
//						if(counterFormTag >= formTag.size()) break;
//						
//						inputTag = formTag.get(counterFormTag).findElements(By.cssSelector("input[type='text'],input[type='password'],textarea"));
//						
//						if(inputTag.size() <= 0){
//							counterFormTag++;
//							continue;
//						}
//						
//						if(counterInputTag == null){
//							counterInputTag = new int[inputTag.size()];
//						}
//											
//						System.out.println(inputTag.size());
//						for(int i = 0; i < inputTag.size(); i++){
//							System.out.println(inputTag.get(i).getTagName());
//							inputTag.get(i).sendKeys(script.get(counterInputTag[i]));
//							System.out.println(script.get(counterInputTag[i]));
//						}
//						System.out.println("");
//						
//						formTag.get(counterFormTag).submit();
//						
//						for(int i = counterInputTag.length-1; i >= 0; i--){
//							if(counterInputTag[i] == script.size()-1){
//								counterInputTag[i] = 0;
//								if(i == 0){
//									counterFormTag++;
//									counterInputTag = null;
//								}
//							}else{
//								counterInputTag[i]++;
//								break;
//							}
//										
//						}
//						
//						try{
//							this.waitForLoad();
//						} catch(TimeoutException e) {
//							System.out.println("Timeout for loading page");
//						}
//												
//						driver.navigate().to(url);					
//					}
//					
//					script.clear();
//								
//				}	
//
//				//driver.quit();
//				
//			} catch (FileNotFoundException e1) {
//				e1.printStackTrace();
//			} catch (IOException e2) {
//				e2.printStackTrace();
//			}
//		}	
//	}
	
	private String getOnlyHostName(String url){
		int index;
		int count = 1;
		for(index = url.indexOf("/"); index >= 0 && count < 3; count++){
			index = url.indexOf("/", index+1);
		}
		
		return index >= 0 ? url.substring(0, index) : url;
	}
	
	private String getCurrentDateTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		String date = sdf.format(new Date());
		return date;
	}
	
	public void testSQLiJoomla(String fullUrl, final int numLog){		
		this.setInitiate();
		final String url = getOnlyHostName(fullUrl);
		
		this.setTestRunning(true);
		
		new Thread(new Runnable() {
			@Override
			public void run() {

				FileManager filemanager = new FileManager();
				filemanager.setReader("Joomla//SQLInjection");
				filemanager.setWriter("Joomla-log" + (numLog + 1) + ".txt");
				
				String temp;
				
				URL web;
				HttpURLConnection http;
				
				filemanager.writeLine("================================================================");
				filemanager.writeLine(url + " SQL Injection " + getCurrentDateTime());
				filemanager.writeLine("================================================================");
				
				while((temp = filemanager.readLineAllFile()) != null){
					System.out.println(url + temp);
					
					try {
						web = new URL(url + temp);
						http = (HttpURLConnection)web.openConnection();
						int statusCode = http.getResponseCode();
						String statusMessage = http.getResponseMessage();
						
						driver.get(url + temp);
						String title = driver.getTitle();
						
						filemanager.writeLine(temp);
						filemanager.writeLine("Header : " + statusCode + " " + statusMessage);
						filemanager.writeLine("Title : " + title);
						
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e){
						e.printStackTrace();
					}
					
				}
				
				filemanager.close();
				
				setTestRunning(false);
			}
		}).start();
	}
	
	private int isAlertPresent(){
		/*
		 * 
		 * alert = 0 Nothing happen
		 * alert = 1 Scripts are placed in html
		 * alert = 2 Scripts are interpreted 
		 *  
		 * */
		
		int alert = 0;
		
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String alertXSSMsg = "window.alertXSSMsg = 'no'; window.alert = function(msg){window.alertXSSMsg = msg;};";
		js.executeScript(alertXSSMsg);
		
		try{
			
			if(driver.findElements(By.xpath("//script[.='alert(/xss/)']")).size() > 0){
				alert = 2;
			}
			else if(driver.findElements(By.xpath("//*[@data-xss='xss']")).size() > 0){
			
					alert++;
					
					WebElement element = driver.findElement(By.xpath("//*[@data-xss='xss']"));
					js.executeScript("arguments[0].click();", element);
					
					String checkXss = (String) js.executeScript("return window.alertXSSMsg");
					
					if(checkXss.equalsIgnoreCase("xss")) alert++;
			}
			
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		return alert;
	}
	
	public void testXSSJoomla(String fullUrl, final int numLog){
		this.setInitiate();
		final String url = getOnlyHostName(fullUrl);

		this.setTestRunning(true);
		
		new Thread(new Runnable() {
			@Override
			public void run() {

				FileManager filemanager = new FileManager();
				filemanager.setReader("Joomla//XSS");
				filemanager.setWriter("Joomla-log" + (numLog + 1) + ".txt");
				
				String temp;
				
				filemanager.writeLine("================================================================");
				filemanager.writeLine(url + " Cross-site Script " + getCurrentDateTime());
				filemanager.writeLine("================================================================");
				
				while((temp = filemanager.readLineAllFile()) != null){
					System.out.println(url + temp);
					
					try{
						
						driver.get(url + temp);
						
						int alert = isAlertPresent();
						
						filemanager.writeLine(temp);
						
						switch(alert){
						case 0: 
							filemanager.writeLine("No-Risk");
							break;
						case 1:
							filemanager.writeLine("Risk No");
							break;
						case 2:
							filemanager.writeLine("Risk Yes");
							break;
						default: 
							filemanager.writeLine("No-Risk");
							break;
						}
						
					}
					catch(Exception e){
						System.out.println(e.getMessage());
					}
							
				}
				
				filemanager.close();
				
				setTestRunning(false);
			}
		}).start();
	}
	
	public void testSQLiWordpress(String url, final int numLog){
		System.out.println("This is testSQLiWordpress method but have no content.");
	}
	
	public void testXSSWordpress(String url, final int numLog){
		System.out.println("This is testXSSWordpress method but have no content.");
	}
	
	public void testSQLiDrupal(String url, final int numLog){
		System.out.println("This is testSQLiDrupal method but have no content.");
	}
	
	public void testXSSDrupal(String url, final int numLog){
		System.out.println("This is testXSSDrupal method but have no content.");
	}

}