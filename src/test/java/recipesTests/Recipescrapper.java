package recipesTests;

import recipeData.recipesdetailslocator;
import recipes_Database.Databases;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baseclass.Baseclass;
import Utils.ExcelReader;
import Utils.ConfigReader;

public class Recipescrapper extends Baseclass{

    private WebDriver driver;
    private WebDriverWait wait;
    Databases DB;        
    private List<recipesdetailslocator> allRecipes = new ArrayList<>();
    JavascriptExecutor js;
    private static final Logger logger = LoggerFactory.getLogger(Recipescrapper.class);

    @BeforeMethod
    public void setUp() {
    	driver = Baseclass.getDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        js = (JavascriptExecutor) driver;
        DB = new Databases();        
        DB.connectToDatabase();
        DB.createRecipeTable(); 
    }

    @Test
    public void scrapeAndStoreRecipes() throws InterruptedException {

        // Find all recipe links       
        //WebElement searchIcon = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//i[@class='fa fa-search'])[1]")));
    	WebElement searchIcon = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Recipes List']")));
    	js.executeScript("arguments[0].click();", searchIcon);
        
    	 	
        Set<String> seenHrefs = new HashSet<>();
        List<String> allRecipeHrefs = collectAllRecipeLinks();

        for (String recipeHref : allRecipeHrefs) {        	                        
            if (!seenHrefs.contains(recipeHref)) {
            	seenHrefs.add(recipeHref);
            	logger.info(recipeHref);
                scrapeRecipeDetails(recipeHref);
                //break;
            }            
        }

        DB.insertRecipesIntoDatabase(allRecipes);
    }
    
    @Test
    public void scrapeAndStoreRecipe_single() throws InterruptedException {

        // For Single Recipe 
    	String recipeHref = "https://www.tarladalal.com/sprouts-misal-protein-rich-recipes-33239r";
        driver.get(recipeHref);      
        
    	logger.info(recipeHref);
        scrapeRecipeDetails(recipeHref);                

        DB.insertRecipesIntoDatabase(allRecipes);
    }
    
    @Test(priority = 2)
	public void Queries() {		
		
    	Object Data[][] = ExcelReader.readExcelData("Final list for LCHFElimination ");
		// To get Eliminate - Data[][0]
    	// To get Add 		- Data[][1]
    	    
	}
    
    public List<String> collectAllRecipeLinks() throws InterruptedException {
    	
    	By recipeContainerLocator = By.xpath("(//div[@class='container'])[3]");
        By recipeLinkInContainerLocator = By.xpath("//div[@class='img-block']/a");
        //By nextPageButtonLocator = By.xpath("//a[contains(text(), 'Next')]");
        List<String> allHrefs = new ArrayList<>();
        Set<String> uniqueHrefs = new HashSet<>();     
        Integer count = 1;	// Start Page
        String PageUrl = ConfigReader.getPaginationUrlPattern();
        String TotalPages = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Next']/../preceding-sibling::li[1]/a"))).getText();
    	String Url = null;
        
    	//TotalPages = "100";	//Last Page
    	while (true) {            
    		try {
    			Url = PageUrl + count.toString();
    			driver.get(Url);
    			
    			String pageSource = driver.getPageSource();
    			if (pageSource.contains("Server Error")) {
    			    logger.info("Server Error 500 encountered!");
    			    count++;
    			    continue;    			    
    			}    			
    			
    			// 1. Wait for the recipe container to be present
    			WebElement recipeContainer = wait.until(ExpectedConditions.presenceOfElementLocated(recipeContainerLocator));
    		
	            // 2. Find all recipe links within the container
	            List<WebElement> recipeLinkElements = recipeContainer.findElements(recipeLinkInContainerLocator);
	            if (count >= 1)	// start page
	            for (WebElement linkElement : recipeLinkElements) {
	                String href = linkElement.getAttribute("href");
	                if (href != null && !href.isEmpty() && uniqueHrefs.add(href)) {
	                    allHrefs.add(href);
	                }
	            }  
	            	
    		}
    		catch (Exception e) {
            	//Check for Ads
    			checkforAd();
            }     
    		count++;
        	if(count == Integer.parseInt(TotalPages) + 1) // No of pages to load
        		break;
        }

    	logger.info("Finished collecting all recipe links. Total unique: " + allHrefs.size());
        return allHrefs;
    }
    
    public void checkforAd() {
    	try {
			WebElement adOverlay = driver.findElement(By.xpath("//div[@id='card']"));
			logger.info("Ad overlay is visible.");			
	    	WebElement closeButton = adOverlay.findElement(By.xpath("//span[contains(text(), 'Close')]")); // Adjust locator
	    	closeButton.click();
		}
		catch (Exception e) {
        	//No ad visible
			logger.info("Ad error: "+ e.getMessage());
        }
    }

    public void scrapeRecipeDetails(String recipeURL) {    	
        driver.get(recipeURL);
        recipesdetailslocator recipe = new recipesdetailslocator();
        recipe.setRecipeURL(recipeURL);
        
        // Recipe id
        Pattern pattern = Pattern.compile("([0-9]+)r$");
        Matcher matcher = pattern.matcher(recipeURL);        
        String extractedDigits = null; // Initialize extractedDigits
        if (matcher.find()) {
            extractedDigits = matcher.group(1);            
            recipe.setRecipeID(Integer.parseInt(extractedDigits));
        } else {        	
        	recipe.setRecipeID(999999);
        }

        try {
        	int lastSlashIndex = recipeURL.lastIndexOf('/');
        	String pathComponent = recipeURL.substring(lastSlashIndex + 1);
        	String namePart = pathComponent.replaceAll("-[0-9]+r$", "");
            recipe.setRecipeName(namePart.replace('-', ' '));

            // Recipe categories
            String RecipeText = driver.findElement(By.xpath("//ul[@class='tags-list']")).getText().toLowerCase();
            
            if (RecipeText.contains("breakfast")) {
                recipe.setRecipeCategory("Breakfast");
            } else if (RecipeText.contains("lunch")) {
                recipe.setRecipeCategory("Lunch");
            } else if (RecipeText.contains("dinner")) {
                recipe.setRecipeCategory("Dinner");
            } else if (RecipeText.contains("snacks")) {
                recipe.setRecipeCategory("Snacks");
            } else {
            	recipe.setRecipeCategory("Lunch");
            }
                   
            // Ingredients
            try {
	            WebElement ingredientsElement = driver.findElement(By.id("ingredients"));
	            List<WebElement> ingredientListItems = ingredientsElement.findElements(By.tagName("p"));
	            StringBuilder ingredients = new StringBuilder();
	            for (WebElement li : ingredientListItems) {
	                ingredients.append(li.getText()).append("\n");
	            }
	            recipe.setIngredients(ingredients.toString().trim());
            }
            catch(Exception e) {
            	recipe.setIngredients(" ");
            }
            // Food Category
            String FoodText = recipe.getIngredients().toLowerCase();
            
            if (FoodText.contains("fish") || FoodText.contains("chicken") || FoodText.contains("meat")) {
            	recipe.setFoodCategory("Non-veg");
            }
            else if (FoodText.contains("egg")) {
            	recipe.setFoodCategory("Eggitarian");
            }
        	else if (FoodText.contains("butter") || FoodText.contains("ghee")) {
        		recipe.setFoodCategory("Vegetarian");
        	}            	
        	else {            	
        		recipe.setFoodCategory("Vegan");
        	}
            
            // Preparation and Cooking Time
            WebElement Preptime = driver.findElement(By.xpath("//*[text()='Preparation Time']/../p"));
            recipe.setPreparationTime(Preptime.getText());
            
            try {
            	WebElement Cooktime = driver.findElement(By.xpath("//*[text()='Cooking Time']/../p"));
            	recipe.setCookingTime(Cooktime.getText());
            }
            catch(Exception e) {
            	recipe.setCookingTime("25 Mins");
            }
            
            // Tag
            WebElement tagElement = driver.findElement(By.xpath("//*[text()='Tags']/../following-sibling::div/ul"));
            List<WebElement> TagListItems = tagElement.findElements(By.tagName("li"));
            StringBuilder Tags = new StringBuilder();
            for (WebElement li : TagListItems) {
                Tags.append(li.getText()).append("\n");
            }
            recipe.setTag(Tags.toString().trim());                         

            // No of Servings
            WebElement servingsElement = driver.findElement(By.xpath("//*[text()='Makes ']/../p"));
            if (servingsElement != null) {
                recipe.setNoOfServings(servingsElement.getText());
            }

            // Cuisine Category
            try {
	            WebElement cuisineElement = driver.findElement(By.xpath("//*[contains(text(),'Cuisine')]/../following-sibling::span[1]/a"));
	            if (cuisineElement != null) {
	                recipe.setCuisineCategory(cuisineElement.getText());
	            }
            }
            catch(Exception e){
            	recipe.setCuisineCategory("Indian");  
            }

            // Recipe Description
            WebElement descriptionElement = driver.findElement(By.id("aboutrecipe"));
            List<WebElement> descListItems = descriptionElement.findElements(By.tagName("p"));
            StringBuilder desc = new StringBuilder();
            for (WebElement p : descListItems) {            	
            	String paragraphContent = p.getText();
                // Remove <strong> tags
                paragraphContent = paragraphContent.replaceAll("<strong>", "").trim();
                paragraphContent = paragraphContent.replaceAll("</strong>", "").trim();
                if (!paragraphContent.isEmpty()) {
                	desc.append(paragraphContent).append("\n\n");
                }
            }
            
            recipe.setRecipeDescription(desc.toString().trim());            

            // Preparation Method
            WebElement methodElement = driver.findElement(By.id("methods"));
            List<WebElement> methodListItems = methodElement.findElements(By.tagName("li"));
            StringBuilder method = new StringBuilder();
            for (WebElement li : methodListItems) {
                method.append(li.getText()).append("\n");
            }
            recipe.setPreparationMethod(method.toString().trim());

            // Nutrient Values
            try {
	            WebElement nutrientsTable = driver.findElement(By.id("rcpnutrients"));
	            WebElement nutrientsTbody = nutrientsTable.findElement(By.tagName("tbody"));
	            List<WebElement> nutrientRows = nutrientsTbody.findElements(By.tagName("tr"));
	
	            StringBuilder nutrientValues = new StringBuilder();
	            for (WebElement row : nutrientRows) {
	                List<WebElement> cells = row.findElements(By.tagName("td"));
	                if (cells.size() == 2) {
	                    String nutrientName = cells.get(0).getText().trim();
	                    String nutrientValue = cells.get(1).getText().trim();
	                    nutrientValues.append(nutrientName).append(": ").append(nutrientValue).append("\n");
	                }
	            }
	            recipe.setNutrientValues(nutrientValues.toString().trim());	            
            }
            catch(Exception e){
            	recipe.setNutrientValues("");  
            }

            allRecipes.add(recipe);

        } catch (Exception e) {
        	logger.info("Error scraping details for: " + recipeURL + " - " + e.getMessage());
        }
    }
    

    @AfterMethod
    public void tearDown() {
        DB.close();
        if (driver != null) {
            driver.quit();
        }
    }
}
