package com.codetaylor.mc.artisanworkstumps.modules.workstumps.plugin.jei;

import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumps;
import com.codetaylor.mc.artisanworktables.api.ArtisanAPI;
import com.codetaylor.mc.artisanworktables.api.event.ArtisanUpdateJEIRecipeVisibilityEvent;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.RecipeRegistry;
import com.codetaylor.mc.artisanworktables.api.internal.reference.EnumTier;
import com.codetaylor.mc.artisanworktables.api.recipe.ArtisanRecipe;
import com.codetaylor.mc.artisanworktables.api.recipe.IArtisanRecipe;
import com.codetaylor.mc.artisanworktables.api.recipe.requirement.IRequirement;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PluginJEI
    implements IModPlugin {

  public static IRecipeRegistry RECIPE_REGISTRY;

  public PluginJEI() {

    MinecraftForge.EVENT_BUS.register(this);
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registry) {

    JEICategoryFactory factory = new JEICategoryFactory(registry.getJeiHelpers().getGuiHelper());

    for (String name : ArtisanAPI.getWorktableNames()) {
      registry.addRecipeCategories(factory.createCategory(name));
    }
  }

  @Override
  public void register(IModRegistry registry) {

    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_TAILOR), PluginJEI.createUID("tailor"));
    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_CARPENTER), PluginJEI.createUID("carpenter"));
    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_MASON), PluginJEI.createUID("mason"));
    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_BLACKSMITH), PluginJEI.createUID("blacksmith"));
    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_JEWELER), PluginJEI.createUID("jeweler"));
    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_BASIC), PluginJEI.createUID("basic"));
    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_ENGINEER), PluginJEI.createUID("engineer"));
    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_MAGE), PluginJEI.createUID("mage"));
    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_SCRIBE), PluginJEI.createUID("scribe"));
    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_CHEMIST), PluginJEI.createUID("chemist"));
    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_FARMER), PluginJEI.createUID("farmer"));
    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_CHEF), PluginJEI.createUID("chef"));
    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_DESIGNER), PluginJEI.createUID("designer"));
    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_TANNER), PluginJEI.createUID("tanner"));
    registry.addRecipeCatalyst(new ItemStack(ModuleWorkstumps.Blocks.WORKSTUMP_POTTER), PluginJEI.createUID("potter"));

    for (String name : ArtisanAPI.getWorktableNames()) {
      // Create the handlers
      registry.handleRecipes(ArtisanRecipe.class, recipe -> new JEIRecipeWrapper(name, recipe), PluginJEI.createUID(name));
      // Add the recipes
      List<IArtisanRecipe> recipeList = new ArrayList<>();
      RecipeRegistry recipeRegistry = ArtisanAPI.getWorktableRecipeRegistry(name);
      recipeList = recipeRegistry.getRecipeListByTier(EnumTier.WORKTABLE, recipeList);
      registry.addRecipes(recipeList, PluginJEI.createUID(name));
    }
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

    // Expose the recipe registry for use in the game stages event handler.
    RECIPE_REGISTRY = jeiRuntime.getRecipeRegistry();

    this.hideRecipes();
  }

  private void hideRecipes() {

    // Hide recipes that should be hidden.
    for (String name : ArtisanAPI.getWorktableNames()) {
      RecipeRegistry registry = ArtisanAPI.getWorktableRecipeRegistry(name);
      List<IArtisanRecipe> recipeList = registry.getRecipeListByTier(EnumTier.WORKTABLE, new ArrayList<>());

      for (IArtisanRecipe recipe : recipeList) {

        if (recipe.isHidden() || this.shouldHideRecipe(recipe)) {
          String uid = PluginJEI.createUID(name);
          IRecipeWrapper recipeWrapper = RECIPE_REGISTRY.getRecipeWrapper(recipe, uid);

          if (recipeWrapper != null) {
            RECIPE_REGISTRY.hideRecipe(recipeWrapper, uid);
          }
        }
      }
    }
  }

  private boolean shouldHideRecipe(IArtisanRecipe recipe) {

    Collection<IRequirement> values = recipe.getRequirements().values();

    for (IRequirement requirement : values) {

      if (requirement.shouldJEIHideOnLoad()) {
        return true;
      }
    }

    return false;
  }

  @SubscribeEvent
  public void on(ArtisanUpdateJEIRecipeVisibilityEvent event) {

    if (!FMLCommonHandler.instance().getEffectiveSide().isClient()) {
      return;
    }

    if (PluginJEI.RECIPE_REGISTRY == null) {
      return;
    }

    // loop through each worktable type, each tier, each recipe

    for (String name : ArtisanAPI.getWorktableNames()) {
      RecipeRegistry registry = ArtisanAPI.getWorktableRecipeRegistry(name);

      List<IArtisanRecipe> recipeList = registry.getRecipeListByTier(EnumTier.WORKTABLE, new ArrayList<>());
      String uid = PluginJEI.createUID(name);

      for (IArtisanRecipe recipe : recipeList) {
        IRecipeWrapper recipeWrapper = PluginJEI.RECIPE_REGISTRY.getRecipeWrapper(recipe, uid);

        if (recipeWrapper == null) {
          continue;
        }

        boolean shouldHide = false;

        for (IRequirement requirement : recipe.getRequirements().values()) {

          if (requirement.shouldJEIHideOnUpdate()) {
            shouldHide = true;
            break;
          }
        }

        //noinspection unchecked
        if (shouldHide) {
          PluginJEI.RECIPE_REGISTRY.hideRecipe(recipeWrapper, uid);

        } else {
          PluginJEI.RECIPE_REGISTRY.unhideRecipe(recipeWrapper, uid);
        }
      }
    }
  }

  public static String createUID(String name) {

    return ModuleWorkstumps.MOD_ID + "_" + name;
  }

}
