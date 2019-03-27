package ru.teamdroid.recipecraft.data.model


class RecipeDetailEntityToRecipe : Mapper<Recipe, RecipeEntity>() {

    override fun map(value: Recipe): RecipeEntity = RecipeEntity(value.idRecipe, value.title, value.time, value.portion, value.isBookmarked)
    override fun reverseMap(value: RecipeEntity) = Recipe(value.idRecipe, value.title, value.time, value.portion)

    fun mapDetailRecipe(value: RecipeEntity, ingredientEntities: List<IngredientEntity>): Recipe {

        val recipe = Recipe(value.idRecipe, value.title, value.time, value.portion, value.isBookmarked)

        ingredientEntities.forEach { ingredientEntity ->
            recipe.ingredients.add(Ingredient(ingredientEntity.idIngredient, ingredientEntity.title))
        }

        return recipe
    }

    fun mapIngredients(listIngredients: MutableList<Ingredient>): MutableList<IngredientEntity> {
        val ingredientsEntities: MutableList<IngredientEntity> = arrayListOf()
        for (ingredient in listIngredients) {
            val ingredientEntity = IngredientEntity(ingredient.idIngredient, ingredient.title)
            ingredientsEntities.add(ingredientEntity)
        }
        return ingredientsEntities
    }

    fun mapRecipeIngredients(listRecipeIngredients: MutableList<RecipeIngredients>): MutableList<RecipeIngredientsEntity> {
        val recipeIngredientsEntity: MutableList<RecipeIngredientsEntity> = arrayListOf()
        for (recipeIngredient in listRecipeIngredients) {
            val recipeIngredientEntity = RecipeIngredientsEntity(recipeIngredient.id, recipeIngredient.idRecipe, recipeIngredient.idIngredient)
            recipeIngredientsEntity.add(recipeIngredientEntity)
        }

        return recipeIngredientsEntity
    }

    fun mapRecipe(recipes: MutableList<Recipe>): MutableList<RecipeEntity> {
        val recipesEntities: MutableList<RecipeEntity> = arrayListOf()
        for (recipe in recipes) {
            val recipeEntity = RecipeEntity(recipe.idRecipe, recipe.title, recipe.time, recipe.portion, recipe.isBookmarked)
            recipesEntities.add(recipeEntity)
        }
        return recipesEntities
    }
}