package recipesTests;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import Utils.ConfigReader;
import baseclass.Baseclass;
import recipeData.recipesdetailslocator;

public class Recipescrapper extends Baseclass {
	Handling_Ads ads = new Handling_Ads();

	/*
	 * Praveena private recipesdetailslocator rl;
	 * 
	 * @BeforeClass public void initLocator() { rl = new recipesdetailslocator(); //
	 * safe to initialize now since driver is initialized rl.removeAds(); }
	 * 
	 * @Test public void navigateAndScrapeRecipes() throws InterruptedException {
	 * 
	 * Praveena JavascriptExecutor js = (JavascriptExecutor) driver;
	 * js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
	 * Thread.sleep(1000);
	 * 
	 * WebElement recipeLink =
	 * driver.findElement(By.xpath("//a[text()='Recipes List']"));
	 * recipeLink.click(); Thread.sleep(1000); String recipelisttext =
	 * driver.findElement(By.xpath("//h4[@class='rec-heading-sec']")).getText();
	 * System.out.println("Recipe list page" + recipelisttext);
	 */
	@Test
	public void navigateAndScrapeRecipes() throws InterruptedException {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
		// Scroll all the way down
		JavascriptExecutor js = (JavascriptExecutor) driver;
		long initialHeight = ((Number) js.executeScript("return document.body.scrollHeight")).longValue();
		while (true) {
			js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			Thread.sleep(1000); // adjust sleep based on load time
			long newHeight = ((Number) js.executeScript("return document.body.scrollHeight")).longValue();
			if (newHeight == initialHeight) {
				break; // stop if height hasn't changed
			}
			initialHeight = newHeight;
		}
		WebElement recipeslink = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Recipes List')]")));
		Assert.assertTrue(recipeslink.isDisplayed(), "Element is not visible");
		if (driver.getCurrentUrl().equals("https://www.tarladalal.com/#google_vignette")) {
			WebElement adx = driver.findElement(By.xpath("contains(text(),'Close')]"));
			adx.click();
			System.out.println("closed the ad before clicking the Recipes link");
		}
		recipeslink.click();

		if (!driver.getCurrentUrl().equals("https://www.tarladalal.com/recipes/")) {
			System.out.println("URL mismatch! Current URL is: " + driver.getCurrentUrl());
			ads.closeAdIfPresent(driver);
			System.out.println("current page is: " + driver.getCurrentUrl());
		}
//pagination
//Get list of Pages elements
		List<WebElement> totalpages = driver.findElements(
				By.xpath("//ul[@class='pagination justify-content-center align-items-center']//*[@class='page-item']"));
		System.out.println("Total pages in pagination are:" + totalpages.size());
		int lastpage = 1;

//Extract highest page number
		for (int i = 0; i < totalpages.size(); i++) {
			WebElement page = totalpages.get(i);
			String text = page.getText().trim();
			if (text.matches("\\d+")) {
				int pagenumber = Integer.parseInt(text);
				if (pagenumber > lastpage) {
					lastpage = pagenumber;
				}
			}
		}
		System.out.println("Total pages are: " + lastpage);
//iterate all the pages
		for (int pnumber = 1; pnumber <= lastpage; pnumber++) {
			String pageUrl = "https://www.tarladalal.com/recipes/?page=" + pnumber;
			driver.get(pageUrl);
			// Fetch the parent web element
			WebElement parentelement = driver
					.findElement(By.xpath("//div[@class='col-md-12']//div[contains(@class, 'recipe-list')]"));
			// Fetch all the recipes using the parent web element
			List<WebElement> allrecipes = parentelement
					.findElements(By.xpath("//div[@class='recipe-block brd-radius-5 mb-3']"));
			// Iterate all recipes in current page
			for (int i = 0; i < allrecipes.size(); i++) {
				WebElement recipe = allrecipes.get(i);
				try {
					WebElement recipelink = wait.until(ExpectedConditions
							.visibilityOf(recipe.findElement(By.xpath(".//h5[contains(@class, 'two-line-text')]/a"))));

					try {
						recipelink.click(); // Try clicking
					} catch (ElementClickInterceptedException e) {
						// Scroll down and retry if not clickable
						js.executeScript("arguments[0].scrollIntoView(true); window.scrollBy(0, 700);", recipelink);
						Thread.sleep(1000); // Small wait for smooth scrolling, optional
						recipelink.click(); // Retry clicking
					}

				} catch (TimeoutException e) {
					System.out.println("Recipe link not found or not visible in time.");
				}
				WebElement cuisinename = driver
						.findElement(By.xpath("(//div[@class='col-md-9 content-body']//p//span)[2]/a"));
				System.out.println("Cuisine: " + cuisinename.getText());
				String Cuisine = cuisinename.getText();
				// WebElement recipeheader=driver.findElement(By.xpath("//div[@class='col-md-9
				// content-body']//p//span/a"));
				List<WebElement> recipeHeaders = driver
						.findElements(By.xpath("//div[@class='col-md-9 content-body']//p//span/a"));
				StringBuilder result = new StringBuilder();
				for (WebElement header : recipeHeaders) {
					result.append(header.getText().trim()).append(" ");
				}
				System.out.println("Recipe Header:" + result.toString().trim());
				// System.out.println("Recipe Header: "+recipeheader.getText());
				WebElement recipename = driver.findElement(By.xpath("//h4[@class='rec-heading']/span"));
				System.out.println("Recipe Name: " + recipename.getText());
				WebElement recipedescription = driver.findElement(By.xpath("//div[@id='aboutrecipe']//p[strong]"));
				System.out.println("Recipe description is: " + recipedescription.getText());
				WebElement recipemethod = driver.findElement(By.xpath("//div[@id='methods']"));
				System.out.println("Recipe Method: " + recipemethod.getText());
				String recipeURL = driver.getCurrentUrl();
				System.out.println("Recipe URL: " + recipeURL);
				WebElement recipeIngredients = driver.findElement(By.xpath("//div[@id='ingredients']"));
				System.out.println("Recipe Ingredients: " + recipeIngredients.getText());
				WebElement nutrientvalue = driver.findElement(By.xpath("//div[@id='nutrients']"));
				System.out.println("Nutritional value: " + nutrientvalue.getText());
				WebElement Preptime = driver.findElement(By.xpath("(//div[@class='row border-bottom']/div/div/p)[1]"));
				System.out.println("Prep time: " + Preptime.getText());
				WebElement cooktime = driver.findElement(By.xpath("(//div[@class='row border-bottom']/div/div/p)[2]"));
				System.out.println("Cooking time: " + cooktime.getText());
				WebElement servings = driver.findElement(By.xpath("(//div[@class='row border-bottom']/div/div/p)[4]"));
				System.out.println("No. of servings: " + servings.getText());
				// go back to the allrecipes page
				driver.navigate().back();
			}
		}

	}

}
