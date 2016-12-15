import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Model {

	private WebDriver driver;
	private WebDriverWait driverWait;
	private JavascriptExecutor jse;
	
	public Model(){
		System.setProperty("webdriver.gecko.driver", "C:\\My Program\\SeleniumHQ\\SeleniumDriver\\geckodriver-v0.11.1-win64\\geckodriver.exe");    	
	}
	
	private void setInitiate(){
		driver = new FirefoxDriver();
		driverWait = new WebDriverWait(driver, 5);
		jse = (JavascriptExecutor)driver;
	}
	
	private ArrayList<String> getFilePathFromType(String path){
		File folder = new File(path);
		ArrayList<String> filepath = new ArrayList<String>();
		
		if(folder.exists()){
			for(File file : folder.listFiles()){
				if(file.isDirectory()){
					filepath.addAll(getFilePathFromType(file.getPath()));
				}else if(file.isFile()){
					filepath.add(file.getPath());
				}
			}
		}
				
		return filepath;		
	}
	
	private ArrayList<String> getFilePath(String type){
		ArrayList<String> filepath = new ArrayList<String>();
		
		switch(type){
		case "sqli": 
			filepath  = getFilePathFromType(Paths.get("").toAbsolutePath().toString() 
					+ "//src//PenetrationScript//SQLInjection");
			break;
		case "xss":
			filepath  = getFilePathFromType(Paths.get("").toAbsolutePath().toString() 
					+ "//src//PenetrationScript//XSS");
			break;
		case "all":
			filepath  = getFilePathFromType(Paths.get("").toAbsolutePath().toString() 
					+ "//src//PenetrationScript");
			break;
		}
		
		return filepath;				
	}
	
	private void waitForLoad() {
	    driverWait.until((ExpectedCondition<Boolean>) d ->
	            ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
		
//		ExpectedCondition<Boolean> pageLoader = new ExpectedCondition<Boolean>() {
//			public Boolean apply(WebDriver wd){
//				String state = (String)(((JavascriptExecutor) wd).executeScript("return document.readyState"));
//				boolean status = false;
//				int i = 0;
//				System.out.println(i++);
//				if(!status && state.equals("interactive")){
//					status = true;
//				}else if(status && state.equals("complete")){
//					return true;
//				}
//					
//				return false;
//			}
//		};
//		driverWait.until(pageLoader);
	}
	
	public void runSecurityTesting(String url){
		if(driver == null){
			this.setInitiate();
		}
		driver.navigate().to(url);
		
		ArrayList<String> filepath = this.getFilePath("all");
		
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
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
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
												
						this.waitForLoad();
						
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
	
//    public long calculate(long number1, long number2, String operator) {
//        switch (operator) {
//            case "+":
//                return number1 + number2;
//            case "-":
//                return number1 - number2;
//            case "*":
//                return number1 * number2;
//            case "/":
//                if (number2 == 0)
//                    return 0;
//
//                return number1 / number2;
//        }
//
//        System.out.println("Unknown operator - " + operator);
//        return 0;
//    }
}