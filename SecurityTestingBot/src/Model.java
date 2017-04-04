import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.phantomjs.PhantomJSDriver;


public class Model {

	private WebDriver driver;
	
	private volatile boolean testRunning = false;
	private volatile boolean cancelled = false;
	
	private void setInitiate(){
		if(driver == null){
			driver = new PhantomJSDriver();
		}
	}
	
	public void closeDriver(){
		if(driver != null){
			driver.quit();
			driver = null;
		}	
	}
	
	public boolean isTestRunning(){
		return testRunning;
	}
	
	public void setTestRunning(boolean run){
		testRunning = run;
	}
	
	public boolean isCancelled(){
		return cancelled;
	}
	
	public void setCancelled(boolean cancel){
		cancelled = cancel;
	}
	
	public void sendResultToWeb(String webname, String filename){
		String url = "https://seniorsecure.tk/projects/new";
		String filepath = Paths.get("").toAbsolutePath().toString() + "//src//OutputLog//" + filename;
		File file = new File(filepath);
		if(!file.isFile()) return;
		
		driver.get(url);
		
		WebElement title = driver.findElement(By.id("project_title"));
		title.sendKeys(webname);
		WebElement choosefile = driver.findElement(By.id("project_filetxt"));
		choosefile.sendKeys(filepath);
		driver.findElement(By.id("new_project")).submit();
	}
	
	private ArrayList<String> extractUrl(String url){
		String temp = "";
		int indexStartQuery = url.indexOf("?") >= 0 ? url.indexOf("?") : url.length();
		ArrayList<String> allPossibleUrlPath = new ArrayList<String>();
				
		if(url.indexOf("https://") == 0){
			temp = "https://";
			url = url.substring(temp.length(), indexStartQuery);
		}else if(url.indexOf("http://") == 0){
			temp = "http://";
			url = url.substring(temp.length(), indexStartQuery);
		}else{
			temp = "http://";
			url = url.substring(0, indexStartQuery);
		}
		
		for(String path : url.split("/")){			
			temp += path;
			allPossibleUrlPath.add(temp);
			temp += "/";
		}
		
		if(allPossibleUrlPath.size() <= 0) allPossibleUrlPath.add(temp);
		
		return allPossibleUrlPath;
	}
	
	private String getCurrentDateTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		String date = sdf.format(new Date());
		return date;
	}
	
	public void testSQLiJoomla(String fullUrl, int numLog){		
		this.setInitiate();
		this.setTestRunning(true);

		ArrayList<String> allPossibleUrlPath = extractUrl(fullUrl);
		
		FileManager filemanager = new FileManager();
		filemanager.setReader("Joomla//SQLInjection");
		filemanager.setWriter("Joomla-log" + (numLog + 1) + ".txt");
		
		String temp;
		
		URL web;
		HttpURLConnection http;
		
		filemanager.writeLine("================================================================");
		filemanager.writeLine(allPossibleUrlPath.get(0) + " SQL Injection " + getCurrentDateTime());
		filemanager.writeLine("================================================================");
		
		while((temp = filemanager.readLineAllFile()) != null && !isCancelled()){
			System.out.println(temp);
			
			try {
				
				boolean check = false;
				
				for(String url : allPossibleUrlPath){
					web = new URL(url + temp);
					http = (HttpURLConnection)web.openConnection();
					int statusCode = http.getResponseCode();
					String statusMessage = http.getResponseMessage();
					driver.get(url + temp);
					String title = driver.getTitle();
					
					if(isCancelled()) break; 
					
					if(statusMessage.indexOf("1064") >= 0 || statusMessage.indexOf("SQL") >= 0
							|| title.indexOf("1064") >= 0 ){
						check = true;
						filemanager.writeLine(url + temp);
						filemanager.writeLine("Yes " + statusCode);
						break;	
					}
				}
				
				if(!check && !isCancelled()){
					filemanager.writeLine(temp);
					filemanager.writeLine("No");
				} 
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
			
		}
		
		filemanager.close();

		this.setTestRunning(false);
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
			
			if(driver.findElements(By.xpath("//script[.='alert(123)']")).size() > 0){
				alert = 2;
			}
			else if(driver.findElements(By.xpath("//*[@data-xss]")).size() > 0){
			
					alert++;
					
					WebElement element = driver.findElement(By.xpath("//*[@data-xss]"));
					js.executeScript("arguments[0].click();", element);
					
					String checkXss = "" +  js.executeScript("return window.alertXSSMsg");
					
					if(checkXss.equals("123")) alert++;
			}
			
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		return alert;
	}
	
	public void testXSSJoomla(String fullUrl, int numLog){
		this.setInitiate();
		this.setTestRunning(true);
		
		ArrayList<String> allPossibleUrlPath = extractUrl(fullUrl);
		
		FileManager filemanager = new FileManager();
		filemanager.setReader("Joomla//XSS");
		filemanager.setWriter("Joomla-log" + (numLog + 1) + ".txt");
		
		String temp;
		
		filemanager.writeLine("================================================================");
		filemanager.writeLine(allPossibleUrlPath.get(0) + " Cross-site Script " + getCurrentDateTime());
		filemanager.writeLine("================================================================");
		
		while((temp = filemanager.readLineAllFile()) != null && !isCancelled()){
			System.out.println(temp);
			
			try{
				int check = -1;
				String tempUrl = allPossibleUrlPath.get(0);
				
				for(String url : allPossibleUrlPath){
					driver.get(url + temp);
					
					int alert = isAlertPresent();
					
					if(isCancelled()) break;
					
					if(alert > check){
						check = alert;
						tempUrl = url + temp;
					}	
				}
				
				if(!isCancelled()){
					switch(check){
					case 0:
						filemanager.writeLine(temp);
						filemanager.writeLine("No-Risk");
						break;
					case 1:
						filemanager.writeLine(tempUrl);
						filemanager.writeLine("Risk No");
						break;
					case 2:
						filemanager.writeLine(tempUrl);
						filemanager.writeLine("Risk Yes");
						break;
					default: 
						filemanager.writeLine(temp);
						filemanager.writeLine("No-Risk");
						break;
					}
				}
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
					
		}
		
		filemanager.close();
		
		this.setTestRunning(false);
	}
	
	private int resultTesting(String url, String check, String typeQuery){
		
		int result = 0;
		int len = check.length();
		
		String[] specialChar = {",","%2C","%252C"};
		
		try{
			
			driver.get(url);
			String sourcePage = driver.getPageSource();		
			
			for(int index = sourcePage.indexOf(check), lastIndex = 0; index >= 0; index = sourcePage.indexOf(check, index + len)){
				
				String text = sourcePage.substring(lastIndex, index);

				if( text.indexOf("UNION") < 0 && text.indexOf("SELECT") < 0 
						&& text.lastIndexOf(specialChar[0]) < (text.length() - specialChar[0].length())
						&& text.lastIndexOf(specialChar[1]) < (text.length() - specialChar[1].length())
						&& text.lastIndexOf(specialChar[2]) < (text.length() - specialChar[2].length()) ){	
					
					switch(typeQuery){
					case "queryWithOutQuot":
						result = 1;
						break;
					case "queryWithQuot":
						result = 2;
						break;
					default :
					}
					
					break;			
				}
				
				lastIndex = index + len;
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
				
		return result;
	}
		
	public void testSQLiWordpress(String fullUrl, int numLog){
		this.setInitiate();
		this.setTestRunning(true);
		
		ArrayList<String> allPossibleUrlPath = extractUrl(fullUrl);
		
		FileManager filemanager = new FileManager();
		filemanager.setReader("Wordpress//SQLInjection");
		filemanager.setWriter("Wordpress-log" + (numLog + 1) + ".txt");
		
		String temp;
		String query = "-5+/*!12345UNION*/+/*!12345SELECT*/+";
		String queryWithQuot = "-5'+/*!12345UNION*/+/*!12345SELECT*/+";
		String endQuery = "+--";
		
		int numOfColumn = 50; // defined number of columns of table in database
		String checkWPSQLi = "999999999999999999999999999999";
		
		filemanager.writeLine("================================================================");
		filemanager.writeLine(allPossibleUrlPath.get(0) + " SQL Injection " + getCurrentDateTime());
		filemanager.writeLine("================================================================");
		
		while((temp = filemanager.readLineAllFile()) != null && !isCancelled()){
			System.out.println(temp);
			
			try {
				
				String tempColumn = "";
				String tempUrl = "";
				int result = 0;
				
				for(String url : allPossibleUrlPath){
					tempColumn = "";
					for(int i = 1; i <= numOfColumn; i++){
						
						tempColumn += checkWPSQLi;
						
						if(isCancelled()) break; 
						tempUrl = url + temp + query + tempColumn + endQuery;
						result = resultTesting(tempUrl, checkWPSQLi, "queryWithOutQuot");
						if(result > 0) break;
						
						if(isCancelled()) break; 
						tempUrl = url + temp + queryWithQuot + tempColumn + endQuery;
						result = resultTesting(tempUrl, checkWPSQLi, "queryWithQuot");
						if(result > 0) break;
						
						tempColumn += ",";
						
					}
					if(result > 0 || isCancelled()) break;
				}
				
				if(!isCancelled()){
					if(result == 0){
						filemanager.writeLine(temp);
						filemanager.writeLine("No");						
					} else if(result == 1){
						filemanager.writeLine(tempUrl);
						filemanager.writeLine("Yes");						
					} else {
						filemanager.writeLine(tempUrl);
						filemanager.writeLine("Yes");
					}
				}
				
			} catch (Exception e){
				e.printStackTrace();
			}
			
		}
		
		filemanager.close();
		
		this.setTestRunning(false);
	}
	
	public void testXSSWordpress(String fullUrl, int numLog){
		this.setInitiate();
		this.setTestRunning(true);
		
		ArrayList<String> allPossibleUrlPath = extractUrl(fullUrl);
		
		FileManager filemanager = new FileManager();
		filemanager.setReader("Wordpress//XSS");
		filemanager.setWriter("Wordpress-log" + (numLog + 1) + ".txt");
		
		String temp;
		
		filemanager.writeLine("================================================================");
		filemanager.writeLine(allPossibleUrlPath.get(0) + " Cross-site Script " + getCurrentDateTime());
		filemanager.writeLine("================================================================");
		
		while((temp = filemanager.readLineAllFile()) != null && !isCancelled()){
			System.out.println(temp);
			
			try{
				
				int check = -1;
				String tempUrl = allPossibleUrlPath.get(0);
				
				for(String url : allPossibleUrlPath){
					driver.get(url + temp);
					
					int alert = isAlertPresent();
					
					if(isCancelled()) break;
					
					if(alert > check){
						check = alert;
						tempUrl = url + temp;
					}	
				}
						
				if(!isCancelled()){
					switch(check){
					case 0:
						filemanager.writeLine(temp);
						filemanager.writeLine("No-Risk");
						break;
					case 1:
						filemanager.writeLine(tempUrl);
						filemanager.writeLine("Risk No");
						break;
					case 2:
						filemanager.writeLine(tempUrl);
						filemanager.writeLine("Risk Yes");
						break;
					default: 
						filemanager.writeLine(temp);
						filemanager.writeLine("No-Risk");
						break;
					}
				}
				
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
					
		}
		
		filemanager.close();
		
		this.setTestRunning(false);
	}
	
	public void testSQLiDrupal(String fullUrl, int numLog){
		System.out.println("This is testSQLiDrupal method but have no content.");
	}
	
	public void testXSSDrupal(String fullUrl, int numLog){
		this.setInitiate();
		this.setTestRunning(true);
		
		ArrayList<String> allPossibleUrlPath = extractUrl(fullUrl);
		
		FileManager filemanager = new FileManager();
		filemanager.setReader("Drupal//XSS");
		filemanager.setWriter("Drupal-log" + (numLog + 1) + ".txt");
		
		String temp;
		
		filemanager.writeLine("================================================================");
		filemanager.writeLine(allPossibleUrlPath.get(0) + " Cross-site Script " + getCurrentDateTime());
		filemanager.writeLine("================================================================");
		
		while((temp = filemanager.readLineAllFile()) != null && !isCancelled()){
			System.out.println(temp);
			
			try{
				
				int check = -1;
				String tempUrl = allPossibleUrlPath.get(0);
				
				for(String url : allPossibleUrlPath){
					driver.get(url + temp);
					
					int alert = isAlertPresent();
					
					if(isCancelled()) break;
					
					if(alert > check){
						check = alert;
						tempUrl = url + temp;
					}	
				}
						
				if(!isCancelled()){
					switch(check){
					case 0:
						filemanager.writeLine(temp);
						filemanager.writeLine("No-Risk");
						break;
					case 1:
						filemanager.writeLine(tempUrl);
						filemanager.writeLine("Risk No");
						break;
					case 2:
						filemanager.writeLine(tempUrl);
						filemanager.writeLine("Risk Yes");
						break;
					default: 
						filemanager.writeLine(temp);
						filemanager.writeLine("No-Risk");
						break;
					}
				}
				
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
					
		}
		
		filemanager.close();
		
		this.setTestRunning(false);
	}

}