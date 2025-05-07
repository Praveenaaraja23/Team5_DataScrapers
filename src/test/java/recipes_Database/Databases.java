package recipes_Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recipeData.recipesdetailslocator;
import recipesTests.Recipescrapper;

public class Databases {

	private Connection connection;
	private static final Logger logger = LoggerFactory.getLogger(Databases.class);
	
	public void connectToDatabase() {
        String dbUrl = "jdbc:postgresql://localhost:5432/RecipeScrapper"; 
        String dbUser = "postgres"; 
        String dbPassword = "postgres"; 

        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            logger.info("Connected to PostgreSQL database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	public void createRecipeTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Recipes (" +
                "Recipe_ID SERIAL PRIMARY KEY," +
                "Recipe_Name VARCHAR(255)," +
                "Recipe_Category VARCHAR(100)," +
                "Food_Category VARCHAR(100)," +
                "Ingredients TEXT," +
                "Preparation_Time VARCHAR(50)," +
                "Cooking_Time VARCHAR(50)," +
                "Tag VARCHAR(255)," +
                "No_of_servings VARCHAR(50)," +
                "Cuisine_category VARCHAR(100)," +
                "Recipe_Description TEXT," +
                "Preparation_method TEXT," +
                "Nutrient_values TEXT," +
                "Recipe_URL VARCHAR(255) UNIQUE" +
                ")";
        try (java.sql.Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
            logger.info("Table 'Recipes' created successfully (if it didn't exist).");
        }
        catch(SQLException e) {
        	// error
        	logger.info("SQL error " + e.getMessage());
        }
    }
	
	public void insertRecipesIntoDatabase(List<recipesdetailslocator> allRecipes) {
        String insertQuery = "INSERT INTO recipes (Recipe_Name, Recipe_Category, Food_Category, Ingredients, Preparation_Time, Cooking_Time, Tag, No_of_servings, Cuisine_category, Recipe_Description, Preparation_method, Nutrient_values, Recipe_URL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (Recipe_URL) DO NOTHING";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            for (recipesdetailslocator recipe : allRecipes) {
                preparedStatement.setString(1, recipe.getRecipeName());
                preparedStatement.setString(2, recipe.getRecipeCategory());
                preparedStatement.setString(3, recipe.getFoodCategory());
                preparedStatement.setString(4, recipe.getIngredients());
                preparedStatement.setString(5, recipe.getPreparationTime());
                preparedStatement.setString(6, recipe.getCookingTime());
                preparedStatement.setString(7, recipe.getTag());
                preparedStatement.setString(8, recipe.getNoOfServings());
                preparedStatement.setString(9, recipe.getCuisineCategory());
                preparedStatement.setString(10, recipe.getRecipeDescription());
                preparedStatement.setString(11, recipe.getPreparationMethod());
                preparedStatement.setString(12, recipe.getNutrientValues());
                preparedStatement.setString(13, recipe.getRecipeURL());
                preparedStatement.executeUpdate();
            }
            logger.info("Successfully inserted " + allRecipes.size() + " recipes into the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	public void close() {
		if (connection != null) {
            try {
                connection.close();
                logger.info("Disconnected from PostgreSQL database");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
	
}
