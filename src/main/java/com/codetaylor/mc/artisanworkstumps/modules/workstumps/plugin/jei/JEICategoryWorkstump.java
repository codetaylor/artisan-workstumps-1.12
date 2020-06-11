package com.codetaylor.mc.artisanworkstumps.modules.workstumps.plugin.jei;

import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumps;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.OutputWeightPair;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class JEICategoryWorkstump
    implements IRecipeCategory {

  private final ICraftingGridHelper craftingGridHelper;

  private String uid;
  private String titleTranslateKey;
  private IDrawable background;

  public JEICategoryWorkstump(
      String titleTranslateKey,
      IDrawable background,
      String uid,
      IGuiHelper guiHelper
  ) {

    this.titleTranslateKey = titleTranslateKey;
    this.background = background;
    this.uid = uid;
    this.craftingGridHelper = guiHelper.createCraftingGridHelper(1, 0);
  }

  @Nonnull
  @Override
  public String getUid() {

    return this.uid;
  }

  @Nonnull
  @Override
  public String getTitle() {

    return I18n.format(this.titleTranslateKey);
  }

  @Nonnull
  @Override
  public String getModName() {

    return ModuleWorkstumps.MOD_NAME;
  }

  @Nonnull
  @Override
  public IDrawable getBackground() {

    return this.background;
  }

  @Override
  public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {

    // Show recipe ID in tooltip

    JEIRecipeWrapper wrapper = (JEIRecipeWrapper) recipeWrapper;
    ResourceLocation registryName = wrapper.getRegistryName();

    if (registryName != null) {
      IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

      guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

        if (slotIndex == this.getOutputSlotIndex()) {
          boolean showAdvanced = Minecraft.getMinecraft().gameSettings.advancedItemTooltips || GuiScreen.isShiftKeyDown();

          if (showAdvanced) {
            tooltip.add(TextFormatting.DARK_GRAY + Translator.translateToLocalFormatted("jei.tooltip.recipe.id", registryName.toString()));
          }
        }
      });
    }

    // Set recipe

    IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
    IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
    List<List<ItemStack>> tools = wrapper.getTools();
    List<List<ItemStack>> inputs = wrapper.getInputs();
    List<ItemStack> outputs = wrapper.getOutput();

    stacks.init(0, false, 111 - 4, 31 - 13);
    stacks.set(0, outputs);

    this.setupTooltip(stacks, wrapper.getWeightedOutput());

    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 3; x++) {
        int index = 1 + x + (y * 3);
        stacks.init(index, true, x * 18 + 16 - 4, y * 18 + 13 - 13);
      }
    }

    if (wrapper.isShaped()) {
      this.craftingGridHelper.setInputs(stacks, inputs, wrapper.getWidth(), wrapper.getHeight());

    } else {
      this.craftingGridHelper.setInputs(stacks, inputs);
    }

    stacks.init(10, true, 74 - 4, 31 - 13);

    if (tools.size() > 0) {
      stacks.set(10, tools.get(0));
    }

    stacks.init(11, false, 148 - 4, 13 - 13);
    stacks.init(12, false, 148 - 4, 31 - 13);
    stacks.init(13, false, 148 - 4, 49 - 13);

    ItemStack extraOutput = wrapper.getSecondaryOutput();

    if (!extraOutput.isEmpty()) {
      stacks.set(11, extraOutput);
    }

    extraOutput = wrapper.getTertiaryOutput();

    if (!extraOutput.isEmpty()) {
      stacks.set(12, extraOutput);
    }

    extraOutput = wrapper.getQuaternaryOutput();

    if (!extraOutput.isEmpty()) {
      stacks.set(13, extraOutput);
    }

    FluidStack fluidStack = wrapper.getFluidStack();

    if (fluidStack != null) {
      fluidStacks.init(14, true, 5 - 4, 14 - 13, 6, 52, fluidStack.amount * 2, false, null);
      fluidStacks.set(14, fluidStack);
    }
  }

  private int getOutputSlotIndex() {

    return 0;
  }

  private void setupTooltip(IGuiItemStackGroup stacks, List<OutputWeightPair> weightedOutput) {

    if (weightedOutput.size() > 1) {
      int sum = 0;

      for (OutputWeightPair pair : weightedOutput) {
        sum += pair.getWeight();
      }

      final int weightSum = sum;

      stacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

        if (slotIndex == 0) {

          for (OutputWeightPair pair : weightedOutput) {

            if (ItemStack.areItemStacksEqual(pair.getOutput().toItemStack(), ingredient)) {
              int chance = Math.round(pair.getWeight() / (float) weightSum * 100);

              List<String> result = new ArrayList<>();
              result.add(tooltip.get(0));
              result.add(I18n.format(
                  "jei.artisanworkstumps.tooltip.chance",
                  TextFormatting.GRAY,
                  String.valueOf(chance)
              ));

              for (int i = 1; i < tooltip.size(); i++) {
                result.add(tooltip.get(i));
              }

              tooltip.clear();
              tooltip.addAll(result);
            }
          }
        }
      });
    }
  }
}
