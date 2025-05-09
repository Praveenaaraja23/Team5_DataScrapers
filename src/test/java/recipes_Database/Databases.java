package recipes_Database;

import java.sql.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import recipeData.recipesdetailslocator;

public class Databases {

    private Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(Databases.class);
    private Statement stmt;
    private ResultSet rs;

    public void connectToDatabase() {
        String dbUrl = "jdbc:postgresql://localhost:5432/newfilterdatap"; 
        String dbUser = "postgres"; 
        String dbPassword = "postgres"; 
        
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            stmt = connection.createStatement();
            
            logger.info("Connected to PostgreSQL database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
    
    public void closeConnection() 
    {        
        try {
            connection.close();
            logger.info("Connected to PostgreSQL database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
     

     /*   try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            stmt = connection.createStatement();
        } catch (SQLException e) {
            logger.error("Database connection or creation error: {}", e.getMessage());
        }
    }*/

       
    public void filterdatap() {
        String fullSchema = "(" +
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
        try {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS lchfeliminate " + fullSchema);
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS lchfadd " + fullSchema);
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS lchfavoidto " + fullSchema);
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS lchffoodprocessing " + fullSchema); 
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS lfveliminate " + fullSchema); 
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS lfvadd " + fullSchema); 
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS lfvtoadd " + fullSchema); 
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS lfvavoidto " + fullSchema); 
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS lfvoptional " + fullSchema); 
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS allergies " + fullSchema); 
            
          
            logger.info("All filter tables created successfully with full schema.");
        } catch (SQLException e) {
            logger.error("Error creating filter tables: {}", e.getMessage());
        }
    }
    
 // Insert filtered recipes into a specific table including Recipe_ID
    public void insertFilteredRecipes(String tableName, List<recipesdetailslocator> recipes) {
        String insertSQL = "INSERT INTO " + tableName +
                " (Recipe_ID, Recipe_Name, Recipe_Category, Food_Category, Ingredients, Preparation_Time, Cooking_Time, Tag, No_of_servings, Cuisine_category, Recipe_Description, Preparation_method, Nutrient_values, Recipe_URL) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (Recipe_URL) DO NOTHING";

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            for (recipesdetailslocator recipe : recipes) {
                pstmt.setInt(1, recipe.getRecipeID());
                pstmt.setString(2, recipe.getRecipeName());
                pstmt.setString(3, recipe.getRecipeCategory());
                pstmt.setString(4, recipe.getFoodCategory());
                pstmt.setString(5, recipe.getIngredients());
                pstmt.setString(6, recipe.getPreparationTime());
                pstmt.setString(7, recipe.getCookingTime());
                pstmt.setString(8, recipe.getTag());
                pstmt.setString(9, recipe.getNoOfServings());
                pstmt.setString(10, recipe.getCuisineCategory());
                pstmt.setString(11, recipe.getRecipeDescription());
                pstmt.setString(12, recipe.getPreparationMethod());
                pstmt.setString(13, recipe.getNutrientValues());
                pstmt.setString(14, recipe.getRecipeURL());

                pstmt.addBatch();
            }
            pstmt.executeBatch();
            logger.info("Inserted " + recipes.size() + " recipes into table: " + tableName);
        } catch (SQLException e) {
            logger.error("Error inserting recipes into " + tableName + ": {}", e.getMessage());
        }
    }

    
    // Close connection
    public void close() {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (connection != null) connection.close();
            logger.info("Database connection closed.");
        } catch (SQLException e) {
            logger.error("Error closing DB resources: " + e.getMessage());
        }
    }
}