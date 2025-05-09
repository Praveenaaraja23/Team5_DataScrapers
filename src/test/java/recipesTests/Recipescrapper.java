package recipesTests;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;
import Utils.ExcelReader;
import baseclass.Baseclass;
import recipeData.Filterdata;
import recipeData.Recipefilter;
import recipeData.recipesdetailslocator;
import recipes_Database.Databases;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Recipescrapper {

	

	    private WebDriver driver;
	    private WebDriverWait wait;
	    private Databases DB;
	    private List<recipesdetailslocator> allRecipes = new ArrayList<>();
	    private JavascriptExecutor js;
	    private static final Logger logger = LoggerFactory.getLogger(Recipescrapper.class);

	    @BeforeMethod
	    public void setUp() {
	    	 Baseclass base = new Baseclass();
	         base.setupChromeDriver(); // manually call setup 
	        driver = Baseclass.getDriver();
	        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	        js = (JavascriptExecutor) driver;
	        DB = new Databases();
	        DB.connectToDatabase();
	        DB.filterdatap();
	    }

	    @Test
	    public void scrapeAndStoreRecipes() throws InterruptedException {
	       
	     
             
			// Find all recipe links   
	        WebElement searchIcon = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//i[@class='fa fa-search'])[1]")));
	        js.executeScript("arguments[0].click();", searchIcon);

	        Set<String> seenHrefs = new HashSet<>();
	        List<String> allRecipeHrefs = collectAllRecipeLinks();

	        for (String recipeHref : allRecipeHrefs) {
	            if (!seenHrefs.contains(recipeHref)) {
	                seenHrefs.add(recipeHref);
	                logger.info(recipeHref);
	                scrapeRecipeDetails(recipeHref);
	            }
	        }
	        
	     //After scraping all recipes, apply filters
	        applyFiltersOnAllRecipes();
	    }
	    
	    private void applyFiltersOnAllRecipes() {
	        // Define the sheet names
	        String[] sheetNames = {
	            "Final list for LFV Elimination",
	            "Final list for LCHFElimination",
	            "Filter -1 Allergies - Bonus Poi"
	        };

	        for (String sheetNameRaw : sheetNames) {
	            String sheetName = sheetNameRaw.trim(); // Trim extra spaces to match reliably

	            try {
	                logger.info("Reading sheet: " + sheetName);
	                Filterdata filterData = ExcelReader.read("src/test/resources/data/IngredientsAndComorbidities-ScrapperHackathon.xlsx", sheetName);

	                // Apply filters based on the sheet name
	                switch (sheetName) {
	                    case "Final list for LFV Elimination":
	                        applyLFVEliminationFilters(filterData);
	                        break;
	                    case "Final list for LCHFElimination":
	                        applyLCHFEliminationFilters(filterData);
	                        break;
	                    case "Filter -1 Allergies - Bonus Poi":
	                        applyAllergyFilters(filterData);
	                        break;
	                    default:
	                        logger.warn("Unknown sheet name: " + sheetName);
	                }

	                logger.info("Completed filtering for sheet: " + sheetName);
	            } catch (Exception e) {
	                logger.error("Error while applying filters for sheet: " + sheetName, e);
	            }
	        }
	    }

	        
	        private void applyLFVEliminationFilters(Filterdata filterData) {
	            Set<String> eliminatedTitles = new HashSet<>();
	            
	            logger.info("Eliminated Recipes are" +eliminatedTitles );
	            // Apply filter for LFV Eliminate, Add, ToAdd, Recipes to avoid, Optional recipes
	            Recipefilter.applyFilter(allRecipes, filterData.getEliminate(), "Eliminate", "lfveliminate", true, eliminatedTitles);
	            Recipefilter.applyFilter(allRecipes, filterData.getAdd(), "Add", "lfvadd", true, eliminatedTitles);
	            Recipefilter.applyFilter(allRecipes, filterData.getToAdd(), "ToAdd", "lfvtoadd", true, eliminatedTitles);
	            Recipefilter.applyFilter(allRecipes, filterData.getRecipesToAvoid(), "Recipes to avoid", "lfvavoidto", true, eliminatedTitles);
	            Recipefilter.applyFilter(allRecipes, filterData.getOptionalRecipes(), "Optional recipes", "lfvoptional", true, eliminatedTitles);
	        }

	        private void applyLCHFEliminationFilters(Filterdata filterData) {
	            Set<String> eliminatedTitles = new HashSet<>();
	            
	            // Apply filter for LCHF Eliminate, Add, Recipes to avoid, Food processing
	            Recipefilter.applyFilter(allRecipes, filterData.getEliminate(), "Eliminate", "lchfeliminate", true, eliminatedTitles);
	            Recipefilter.applyFilter(allRecipes, filterData.getAdd(), "Add", "lchfadd", true, eliminatedTitles);
	            Recipefilter.applyFilter(allRecipes, filterData.getRecipesToAvoid(), "Recipes to avoid", "lchfavoidto", true, eliminatedTitles);
	            Recipefilter.applyFilter(allRecipes, filterData.getFoodProcessing(), "Food processing", "lchffoodprocessing", true, eliminatedTitles);
	        }

	        private void applyAllergyFilters(Filterdata filterData) {
	            Set<String> eliminatedTitles = new HashSet<>();
	            Recipefilter.applyFilter(allRecipes, filterData.getAllergies(), "Allergies", "allergies", true, eliminatedTitles);
	        }

	    
	    
	    
	    public List<String> collectAllRecipeLinks() throws InterruptedException {
	        By recipeContainerLocator = By.xpath("(//div[@class='container'])[3]");
	        By recipeLinkInContainerLocator = By.xpath("//div[@class='img-block']/a");
	        By nextPageButtonLocator = By.xpath("//a[contains(text(), 'Next')]");
	        List<String> allHrefs = new ArrayList<>();
	        Set<String> uniqueHrefs = new HashSet<>();
	        int count = 0;

	        while (true) {
	            try {
	                WebElement recipeContainer = wait.until(ExpectedConditions.presenceOfElementLocated(recipeContainerLocator));
	                List<WebElement> recipeLinkElements = recipeContainer.findElements(recipeLinkInContainerLocator);
	                for (WebElement linkElement : recipeLinkElements) {
	                    String href = linkElement.getAttribute("href");
	                    if (href != null && !href.isEmpty() && uniqueHrefs.add(href)) {
	                        allHrefs.add(href);
	                    }
	                }
	                WebElement nextPageButton = driver.findElement(nextPageButtonLocator);
	                if (nextPageButton == null) break;

	                try {
	                    js.executeScript("arguments[0].click();", nextPageButton);
	                } catch (Exception e) {
	                    logger.info("Error clicking 'Next': " + e.getMessage());
	                }

	                count++;
	                if (count == 1) break;
	            } catch (Exception e) {
	                checkforAd();
	            }
	        }

	        logger.info("Collected recipe links: " + allHrefs.size());
	        return allHrefs;
	    }
/*By recipeContainerLocator = By.xpath("(//div[@class='container'])[3]");
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
    } */
	    public void checkforAd() {
	        try {
	            WebElement adOverlay = driver.findElement(By.xpath("//div[@id='card']"));
	            WebElement closeButton = adOverlay.findElement(By.xpath("//span[contains(text(), 'Close')]"));
	            closeButton.click();
	        } catch (Exception e) {
	            logger.info("Ad not found or already closed.");
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
	            recipe.setPreparationTime(driver.findElement(By.xpath("//*[text()='Preparation Time']/../p")).getText());

	            try {
	                recipe.setCookingTime(driver.findElement(By.xpath("//*[text()='Cooking Time']/../p")).getText());
	            } catch (Exception e) {
	                recipe.setCookingTime("25 Mins");
	            }
              //Tag
	            WebElement tagElement = driver.findElement(By.xpath("//*[text()='Tags']/../following-sibling::div/ul"));
	            List<WebElement> tagItems = tagElement.findElements(By.tagName("li"));
	            StringBuilder tagBuilder = new StringBuilder();
	            for (WebElement tag : tagItems) tagBuilder.append(tag.getText()).append("\n");
	            recipe.setTag(tagBuilder.toString().trim());
               // No of Servings
	            recipe.setNoOfServings(driver.findElement(By.xpath("//*[text()='Makes ']/../p")).getText());
               
				// Cuisine Category
	            
	            try {
	                WebElement cuisineElement = driver.findElement(By.xpath("//*[text()='Cuisine  ']/../following-sibling::span[1]/a"));
	                recipe.setCuisineCategory(cuisineElement.getText());
	            } catch (Exception e) {
	                recipe.setCuisineCategory("Indian");
	            }
	            
               // Recipe Description
	            
	            WebElement descElement = driver.findElement(By.id("aboutrecipe"));
	            List<WebElement> descList = descElement.findElements(By.tagName("p"));
	            StringBuilder desc = new StringBuilder();
	            for (WebElement p : descList) {
	                desc.append(p.getText().trim()).append("\n\n");
	            }
	            recipe.setRecipeDescription(desc.toString().trim());
	            
               // Preparation Method
	            
	            WebElement methodElement = driver.findElement(By.id("methods"));
	            List<WebElement> methodList = methodElement.findElements(By.tagName("li"));
	            StringBuilder method = new StringBuilder();
	            for (WebElement li : methodList) method.append(li.getText()).append("\n");
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
	                        nutrientValues.append(cells.get(0).getText().trim()).append(": ")
	                                .append(cells.get(1).getText().trim()).append("\n");
	                    }
	                }
	                recipe.setNutrientValues(nutrientValues.toString().trim());
	            } catch (Exception e) {
	                recipe.setNutrientValues("");
	            }

	            allRecipes.add(recipe);

	        } catch (Exception e) {
	            logger.info("Error scraping " + recipeURL + " - " + e.getMessage());
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

