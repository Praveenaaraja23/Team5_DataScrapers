package recipeData;


import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import recipes_Database.Databases;

public class Recipefilter {
	
	 private static Databases DB = new Databases(); 
	 private static final Logger logger = LoggerFactory.getLogger(Recipefilter.class);
	    public static void applyFilter(List<recipesdetailslocator> sourceRecipes,
	                                   List<String> keywords,
	                                   String columnName,
	                                   String tableName,
	                                   boolean skipIfEliminated,
	                                   Set<String> eliminatedTitles) {
	    	
	    	DB.connectToDatabase();
	    	
	        for (recipesdetailslocator recipe : sourceRecipes) {
	            String title = recipe.getTitle();

	            // Skip if already eliminated and flag is set
	            if (skipIfEliminated && eliminatedTitles.contains(title)) {
	                continue;
	            }

	            String combinedText = (recipe.getIngredients() + " " +
	                                   recipe.getRecipeDescription() + " " +
	                                   recipe.getPreparationMethod() + " " +
	                                   recipe.getTag()).toLowerCase()+ " " +
	            		               recipe.getRecipeName().toLowerCase();

	            for (String keyword : keywords) {
	                if (keyword != null && !keyword.isEmpty() &&
	                    combinedText.contains(keyword.toLowerCase())) {

	                	 logger.info(columnName + " Match: " + keyword + " | Recipe: " + title);
	                	 System.out.println(columnName + " Match: " + keyword + " | Recipe: " + title);
	                	
	                	 
	                	 DB.insertFilteredRecipes(tableName, List.of(recipe));
	                	 

	                    // Track eliminated titles to skip in future filters
	                    if (tableName.equalsIgnoreCase("lchfeliminate") ||
	                        tableName.equalsIgnoreCase("lfveliminate")){
	                        eliminatedTitles.add(title);
	                    }
	                    break; 
	                }
	            }
	        }
	        
	        DB.close();
	    }

}