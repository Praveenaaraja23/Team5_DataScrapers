	package baseclass;
	
	import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
	import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
	import org.testng.annotations.AfterClass;
	import org.testng.annotations.BeforeClass;
	
	import Utils.ConfigReader;
	
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.IOException;
	import java.time.Duration;
	import java.util.Arrays;
	import java.util.HashMap;
	import java.util.Map;
	import java.util.Properties;
	
	public class Baseclass {
		
		 public static WebDriver driver;
		 protected static WebDriverWait wait;
	
		 public static WebDriver getDriver() {
		        return driver;
		    }
	
		  @BeforeClass(alwaysRun = true)
		    public void setupChromeDriver() {			  	
		        String browser = ConfigReader.getBrowser();
		        if (!browser.equalsIgnoreCase("chrome")) {
		            throw new IllegalArgumentException("Unsupported browser: " + browser);
		        }

		        boolean headless = ConfigReader.isHeadless();

		        ChromeOptions options = new ChromeOptions();
		        if (headless) {
		            options.addArguments("--headless=new");
		        }
		        
		        Map<String, Object> prefs = new HashMap<>();
		       
		        options.setExperimentalOption("prefs", prefs);
		       options.addArguments("--disable-popup-blocking");
		       options.addArguments("--disable-notifications");
		        options.addArguments("--disable-extensions");
		        options.addArguments("--disable-dev-shm-usage");
		       options.addArguments("--disable-software-rasterizer");
		        options.addArguments("--disable-gpu");
		       options.addArguments("--no-sandbox");
		      // options.addArguments("--headless"); // Optional: for headless execution
		        options.addArguments("--remote-allow-origins=*");
		        


		        driver = new ChromeDriver(options);
		        driver.manage().window().maximize();
		        driver.get(ConfigReader.getBaseUrl());
		       
		      
		      
		    }
		  
		    @AfterClass(alwaysRun = true)
		    public void tearDown() {
		        if (driver != null) {
		            driver.quit();
		        }
		    }
		}