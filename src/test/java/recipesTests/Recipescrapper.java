package recipesTests;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import Utils.ConfigReader;
import baseclass.Baseclass;
import recipeData.recipesdetailslocator;

public class Recipescrapper extends Baseclass {
	
	 private recipesdetailslocator rl;
	 // String paginationUrlPattern = ConfigReader.getPaginationUrlPattern();

	    @BeforeClass
	    public void initLocator() {
	        rl = new recipesdetailslocator(); // safe to initialize now since driver is initialized
	        rl.removeAds();
	      
	    }

	    @Test
	    public void navigateAndScrapeRecipes() throws InterruptedException {
	    
	    	
	        JavascriptExecutor js = (JavascriptExecutor) driver;
	        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
	        Thread.sleep(1000);
	       
	        WebElement recipeLink = driver.findElement(By.xpath("//a[text()='Recipes List']"));
	         recipeLink.click(); 
	        Thread.sleep(1000);  
	        String recipelisttext = driver.findElement(By.xpath("//h4[@class='rec-heading-sec']")).getText();
	        System.out.println("Recipe list page" + recipelisttext);
	       
	        
	   
	    }
	        
	   
	    }
	
	      
	    
	
	    
 

