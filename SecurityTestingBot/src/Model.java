import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Model {

	private WebDriver driver;
	private WebDriverWait driverWait;
	private JavascriptExecutor jse;
	
	private void setInitiate(){
		driver = new FirefoxDriver();
		driverWait = new WebDriverWait(driver, 30);
		jse = (JavascriptExecutor)driver;
	}
	
	private void waitForLoad() {
//	    driverWait.until((ExpectedCondition<Boolean>) d ->
//	            ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
		
		
		ExpectedCondition<Boolean> pageLoader = new ExpectedCondition<Boolean>() {
			boolean status = false;
			

			public Boolean apply(WebDriver wd){
				String state = (String)(((JavascriptExecutor) wd).executeScript("return document.readyState"));	
				
				
				
//				String a = ""+(jse.executeScript("var xhttp = new XMLHttpRequest();"
//						+ "return xhttp.getAllResponseHeaders();"
//						+ "if (xhttp.readyState == 4 && xhttp.status == 200) {"
//						+ "return this.responseText;"
//						+ "}"));
//				System.out.println(a);
				
				
				
				if((!status && state.equals("loading")) || (!status && state.equals("interactive"))){
					status = true;
				}else if(status && state.equals("complete")){
					return true;
				}
					
				return false;
			}
		};
		driverWait.until(pageLoader);
	}
	
	public void runSecurityTesting(String url, String framework, HashSet<String> attack){
		if(driver == null){
			this.setInitiate();
		}
		driver.navigate().to(url);
		
		FileManager filemanager = new FileManager();
		ArrayList<String> filepath = filemanager.getSelectedFilePath("all");
		
		if(filepath.size() <= 0){
			System.out.println("File cannot found!!");
		}else{
			try {
				int counterFormTag;
				int[] counterInputTag;
				ArrayList<String> script = new ArrayList<String>();
						
				BufferedReader reader;
				String temp;
				
				List<WebElement> formTag;
				List<WebElement> inputTag;
				
				for(String path : filepath){				
					reader = new BufferedReader(new FileReader(path));				
					while((temp = reader.readLine()) != null){
						script.add(temp);
					}
					reader.close();
					
					counterFormTag = 0;
					counterInputTag = null;
					
					while(true){
						formTag = driver.findElements(By.tagName("form"));
						
						if(counterFormTag >= formTag.size()) break;
						
						inputTag = formTag.get(counterFormTag).findElements(By.cssSelector("input[type='text'],input[type='password'],textarea"));
						
						if(inputTag.size() <= 0){
							counterFormTag++;
							continue;
						}
						
						if(counterInputTag == null){
							counterInputTag = new int[inputTag.size()];
						}
											
						System.out.println(inputTag.size());
						for(int i = 0; i < inputTag.size(); i++){
							System.out.println(inputTag.get(i).getTagName());
							inputTag.get(i).sendKeys(script.get(counterInputTag[i]));
							System.out.println(script.get(counterInputTag[i]));
						}
						System.out.println("");
						
						formTag.get(counterFormTag).submit();
						
						for(int i = counterInputTag.length-1; i >= 0; i--){
							if(counterInputTag[i] == script.size()-1){
								counterInputTag[i] = 0;
								if(i == 0){
									counterFormTag++;
									counterInputTag = null;
								}
							}else{
								counterInputTag[i]++;
								break;
							}
										
						}
						
						try{
							this.waitForLoad();
						} catch(TimeoutException e) {
							System.out.println("Timeout for loading page");
						}
												
						driver.navigate().to(url);					
					}
					
					script.clear();
								
				}	

				//driver.quit();
				
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}	
	}

}