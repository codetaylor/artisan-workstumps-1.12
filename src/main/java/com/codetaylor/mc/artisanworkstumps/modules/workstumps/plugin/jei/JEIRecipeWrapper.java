package com.codetaylor.mc.artisanworkstumps.modules.workstumps.plugin.jei;

import com.codetaylor.mc.artisanworkstumps.ModArtisanWorkstumps;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumps;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumpsConfig;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.IArtisanIngredient;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.IArtisanItemStack;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.OutputWeightPair;
import com.codetaylor.mc.artisanworktables.api.recipe.ArtisanRecipe;
import com.codetaylor.mc.athenaeum.gui.GuiHelper;
import com.codetaylor.mc.athenaeum.parser.recipe.item.MalformedRecipeItemException;
import com.codetaylor.mc.athenaeum.parser.recipe.item.ParseResult;
import com.codetaylor.mc.athenaeum.parser.recipe.item.RecipeItemParser;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.stream.Collectors;

public class JEIRecipeWrapper
    implements IRecipeWrapper {

  private static final ResourceLocation SHAPELESS_ICON = new ResourceLocation(
      ModuleWorkstumps.MOD_ID,
      "textures/gui/shapeless_icon.png"
  );

  private static Map<String, List<Item>> DEFAULT_TOOLS = new HashMap<>();

  private final String tableName;
  private ArtisanRecipe artisanRecipe;
  private List<List<ItemStack>> inputs;
  private List<List<ItemStack>> tools;
  private List<ItemStack> output;

  public JEIRecipeWrapper(String tableName, ArtisanRecipe artisanRecipe) {

    this.tableName = tableName;
    this.artisanRecipe = artisanRecipe;
    this.inputs = new ArrayList<>();
    this.tools = new ArrayList<>();

    for (IArtisanIngredient input : this.artisanRecipe.getIngredientList()) {
      ItemStack[] matchingStacks = input.toIngredient().getMatchingStacks();
      List<ItemStack> list = new ArrayList<>(matchingStacks.length);

      for (ItemStack matchingStack : matchingStacks) {
        list.add(matchingStack.copy());
      }
      this.inputs.add(list);
    }

    for (int i = 0; i < artisanRecipe.getToolCount(); i++) {
      IArtisanItemStack[] tools = this.artisanRecipe.getTools(i);
      List<ItemStack> itemStackList = new ArrayList<>(tools.length);

      for (IArtisanItemStack tool : tools) {
        itemStackList.add(tool.toItemStack().copy());
      }

      this.tools.add(itemStackList);
    }

    if (this.tools.isEmpty()) {
      // Here we need to add the tool from the config.

      List<Item> toolList = DEFAULT_TOOLS.computeIfAbsent(this.tableName, k -> {

        String itemString = ModuleWorkstumpsConfig.WORKSTUMP.DEFAULT_RECIPE_TOOL.get(this.tableName);

        try {
          ParseResult parseResult = RecipeItemParser.INSTANCE.parse(itemString);

          if ("ore".equals(parseResult.getDomain())) {
            return OreDictionary.getOres(parseResult.getPath()).stream()
                .map(ItemStack::getItem)
                .collect(Collectors.toList());

          } else {
            ResourceLocation resourceLocation = new ResourceLocation(parseResult.getDomain(), parseResult.getPath());
            Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);

            if (item == null) {
              ModArtisanWorkstumps.LOGGER.error("Couldn't find registered item for resource location " + resourceLocation);
              return Collections.emptyList();
            }

            return Collections.singletonList(item);
          }

        } catch (MalformedRecipeItemException e) {
          ModArtisanWorkstumps.LOGGER.error("", e);
          return Collections.emptyList();
        }
      });

      this.tools.add(toolList.stream().map(ItemStack::new).collect(Collectors.toList()));
    }

    List<OutputWeightPair> output = this.artisanRecipe.getOutputWeightPairList();
    this.output = new ArrayList<>(output.size());

    for (OutputWeightPair pair : output) {
      this.output.add(pair.getOutput().toItemStack().copy());
    }
  }

  public ResourceLocation getRegistryName() {

    return new ResourceLocation(this.artisanRecipe.getName());
  }

  public List<List<ItemStack>> getInputs() {

    return this.inputs;
  }

  public FluidStack getFluidStack() {

    return this.artisanRecipe.getFluidIngredient();
  }

  public List<OutputWeightPair> getWeightedOutput() {

    return this.artisanRecipe.getOutputWeightPairList();
  }

  public List<ItemStack> getOutput() {

    return this.output;
  }

  public boolean isShaped() {

    return this.artisanRecipe.isShaped();
  }

  public int getWidth() {

    return this.artisanRecipe.getWidth();
  }

  public int getHeight() {

    return this.artisanRecipe.getHeight();
  }

  public List<List<ItemStack>> getTools() {

    return this.tools;
  }

  public ItemStack getSecondaryOutput() {

    return this.artisanRecipe.getSecondaryOutput().toItemStack();
  }

  public ItemStack getTertiaryOutput() {

    return this.artisanRecipe.getTertiaryOutput().toItemStack();
  }

  public ItemStack getQuaternaryOutput() {

    return this.artisanRecipe.getQuaternaryOutput().toItemStack();
  }

  @Override
  public void getIngredients(IIngredients ingredients) {

    List<List<ItemStack>> inputs = new ArrayList<>();
    inputs.addAll(this.inputs);
    inputs.addAll(this.tools);
    ingredients.setInputLists(ItemStack.class, inputs);

    FluidStack fluidIngredient = this.artisanRecipe.getFluidIngredient();

    if (fluidIngredient != null) {
      ingredients.setInput(FluidStack.class, fluidIngredient);
    }

    List<ItemStack> output = new ArrayList<>();
    output.addAll(this.output);
    output.add(this.getSecondaryOutput());
    output.add(this.getTertiaryOutput());
    output.add(this.getQuaternaryOutput());
    ingredients.setOutputs(ItemStack.class, output);
  }

  @Override
  public void drawInfo(
      Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY
  ) {

    GlStateManager.pushMatrix();
    GlStateManager.translate(0, 0, 1000);

    String experienceString = null;

    if (this.artisanRecipe.getExperienceRequired() > 0) {

      if (this.artisanRecipe.consumeExperience()) {
        experienceString = I18n.format("jei.artisanworkstumps.xp.cost", this.artisanRecipe.getExperienceRequired());

      } else {
        experienceString = I18n.format(
            "jei.artisanworkstumps.xp.required",
            this.artisanRecipe.getExperienceRequired()
        );
      }

    } else if (this.artisanRecipe.getLevelRequired() > 0) {

      if (this.artisanRecipe.consumeExperience()) {
        experienceString = I18n.format("jei.artisanworkstumps.level.cost", this.artisanRecipe.getLevelRequired());

      } else {
        experienceString = I18n.format("jei.artisanworkstumps.level.required", this.artisanRecipe.getLevelRequired());
      }
    }

    if (experienceString != null) {
      this.drawExperienceString(minecraft, recipeHeight, experienceString);
    }

    this.drawToolDamageString(minecraft, 83 - 4, 52 - 13);

    GlStateManager.popMatrix();

    GlStateManager.pushMatrix();
    GlStateManager.scale(0.5, 0.5, 1);
    GlStateManager.translate(0, 0, 1000);
    GlStateManager.enableDepth();
    GlStateManager.pushMatrix();

    int xPos = 331 - 4;
    int yPos = 32 - 13;

    if (!this.artisanRecipe.getSecondaryOutput().isEmpty()) {
      float chance = this.artisanRecipe.getSecondaryOutputChance();
      this.drawSecondaryOutputChanceString(minecraft, chance, xPos, yPos);
    }

    if (!this.artisanRecipe.getTertiaryOutput().isEmpty()) {
      float chance = this.artisanRecipe.getTertiaryOutputChance();
      this.drawSecondaryOutputChanceString(minecraft, chance, xPos, (yPos + 36));
    }

    if (!this.artisanRecipe.getQuaternaryOutput().isEmpty()) {
      float chance = this.artisanRecipe.getQuaternaryOutputChance();
      this.drawSecondaryOutputChanceString(minecraft, chance, xPos, (yPos + 72));
    }

    GlStateManager.popMatrix();

    if (!this.artisanRecipe.isShaped()) {
      GuiHelper.drawTexturedRect(minecraft, SHAPELESS_ICON, 234, 8, 18, 17, 0, 0, 0, 1, 1);
    }

    GlStateManager.popMatrix();

    // TODO: attempt to move the following tooltip to IRecipeCategory#getTooltipStrings
    GlStateManager.pushMatrix();
    GlStateManager.translate(0, -8, 0);

    if (!this.artisanRecipe.isShaped()) {

      int x = 117;
      int y = 4;

      if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 9) {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format("jei.artisanworkstumps.tooltip.shapeless.recipe"));
        GuiUtils.drawHoveringText(
            tooltip,
            mouseX,
            mouseY,
            minecraft.displayWidth,
            minecraft.displayHeight,
            200,
            minecraft.fontRenderer
        );
      }
    }
    GlStateManager.popMatrix();
  }

  private void drawSecondaryOutputChanceString(
      Minecraft minecraft,
      float secondaryOutputChance,
      int positionX,
      int positionY
  ) {

    String label = (int) (secondaryOutputChance * 100) + "%";
    minecraft.fontRenderer.drawString(
        label,
        positionX - minecraft.fontRenderer.getStringWidth(label) * 0.5f,
        positionY,
        0xFFFFFFFF,
        true
    );
  }

  private void drawExperienceString(Minecraft minecraft, int recipeHeight, String experienceString) {

    minecraft.fontRenderer.drawString(
        experienceString,
        0,
        recipeHeight - 8,
        0xFF80FF20,
        true
    );
  }

  private void drawToolDamageString(Minecraft minecraft, int offsetX, int offsetY) {

    if (this.artisanRecipe.getToolCount() > 0) {

      for (int i = 0; i < this.artisanRecipe.getToolCount(); i++) {
        String label = "-" + this.artisanRecipe.getToolDamage(i);
        minecraft.fontRenderer.drawString(
            label,
            offsetX - minecraft.fontRenderer.getStringWidth(label) * 0.5f,
            offsetY + (22 * i),
            0xFFFFFFFF,
            true
        );
      }

    } else {
      String label = "-" + ModuleWorkstumpsConfig.WORKSTUMP.DEFAULT_RECIPE_TOOL_DAMAGE;
      minecraft.fontRenderer.drawString(
          label,
          offsetX - minecraft.fontRenderer.getStringWidth(label) * 0.5f,
          offsetY,
          0xFFFFFFFF,
          true
      );
    }
  }
}
