package recipeData;

import java.util.List;

public class Filterdata {

    private List<String> eliminate;
    private List<String> add;
    private List<String> recipesToAvoid;
    private List<String> foodProcessing;
    private List<String> toAdd;
    private List<String> optionalRecipes;
    private List<String> allergies;

    public List<String> getEliminate() {
        return eliminate;
    }

    public void setEliminate(List<String> eliminate) {
        this.eliminate = eliminate;
    }

    public List<String> getAdd() {
        return add;
    }

    public void setAdd(List<String> add) {
        this.add = add;
    }

    public List<String> getRecipesToAvoid() {
        return recipesToAvoid;
    }

    public void setRecipesToAvoid(List<String> recipesToAvoid) {
        this.recipesToAvoid = recipesToAvoid;
    }

    public List<String> getFoodProcessing() {
        return foodProcessing;
    }

    public void setFoodProcessing(List<String> foodProcessing) {
        this.foodProcessing = foodProcessing;
    }
    // Getter and Setter methods for ToAdd
    public List<String> getToAdd() {
        return toAdd;
    }

    public void setToAdd(List<String> toAdd) {
        this.toAdd = toAdd;
    }
    public List<String> getOptionalRecipes() {
        return optionalRecipes;
    }

    public void setOptionalRecipes(List<String> optionalRecipes) {
        this.optionalRecipes = optionalRecipes;
    }
    
    public List<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }
}
