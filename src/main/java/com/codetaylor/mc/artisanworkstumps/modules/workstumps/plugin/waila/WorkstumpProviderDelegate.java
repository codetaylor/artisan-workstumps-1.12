package com.codetaylor.mc.artisanworkstumps.modules.workstumps.plugin.waila;

import com.codetaylor.mc.artisanworkstumps.ModArtisanWorkstumps;
import com.codetaylor.mc.artisanworkstumps.lib.spi.plugin.ProviderDelegateBase;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumpsConfig;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.TileWorkstump;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.IArtisanItemStack;
import com.codetaylor.mc.artisanworktables.api.recipe.IArtisanRecipe;
import com.codetaylor.mc.athenaeum.interaction.spi.IInteraction;
import com.codetaylor.mc.athenaeum.interaction.spi.IInteractionItemStack;
import com.codetaylor.mc.athenaeum.interaction.util.InteractionRayTraceData;
import com.codetaylor.mc.athenaeum.parser.recipe.item.MalformedRecipeItemException;
import com.codetaylor.mc.athenaeum.parser.recipe.item.ParseResult;
import com.codetaylor.mc.athenaeum.parser.recipe.item.RecipeItemParser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class WorkstumpProviderDelegate
    extends ProviderDelegateBase<WorkstumpProviderDelegate.IWorkstumpDisplay, TileWorkstump> {

  public static final String LANG_KEY_HOVERED_ITEM_QUANTITY = "gui.artisanworkstumps.waila.quantity";

  private Map<String, List<Item>> defaultTools;

  public WorkstumpProviderDelegate(IWorkstumpDisplay display) {

    super(display);
    this.defaultTools = new HashMap<>();
  }

  @Override
  public void display(TileWorkstump tile) {

    this.display(tile, null);
  }

  public void display(TileWorkstump tile, @Nullable EntityPlayer player) {

    float progress = tile.getRecipeProgress();

    ItemStackHandler stackHandler = tile.getStackHandlerInput();
    boolean notEmpty = false;

    for (int i = 0; i < 9; i++) {

      if (!stackHandler.getStackInSlot(i).isEmpty()) {
        notEmpty = true;
        break;
      }
    }

    if (notEmpty) {

      // Display input item and recipe output.
      IArtisanRecipe workstumpRecipe = tile.getWorkstumpRecipe(player);

      if (workstumpRecipe != null && !workstumpRecipe.getBaseOutput(tile.createCraftingContext(player)).isEmpty()) {
        ItemStack recipeOutput = workstumpRecipe.getOutputWeightPairList().get(0).getOutput().toItemStack();

        if (!recipeOutput.isEmpty()) {

          List<Item> toolList;

          if (workstumpRecipe.getToolCount() > 0) {
            toolList = Arrays.stream(workstumpRecipe.getToolEntries()[0].getToolStacks())
                .map(IArtisanItemStack::getItem)
                .collect(Collectors.toList());

          } else {

            toolList = this.defaultTools.computeIfAbsent(tile.getTableName(), k -> {

              String itemString = ModuleWorkstumpsConfig.WORKSTUMP.DEFAULT_RECIPE_TOOL.get(tile.getTableName());

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
          }

          if (toolList.isEmpty()) {
            this.display.setRecipeProgress(new ItemStack(Blocks.CRAFTING_TABLE), recipeOutput, (int) (100 * progress), 100);

          } else {
            int index = (int) ((tile.getWorld().getTotalWorldTime() / 29) % toolList.size());
            Item item = toolList.get(index);
            this.display.setRecipeProgress(new ItemStack(item), recipeOutput, (int) (100 * progress), 100);
          }

          this.display.setRecipeOutputName(recipeOutput);
        }
      }
    }

    // Add condition

    int remainingDurability = tile.getRemainingDurability();
    int durability = tile.getDurability();

    float d = remainingDurability / (float) durability;

    if (d <= 0.33) {
      this.display.setCondition(
          "gui.artisanworkstumps.waila.workstump.condition",
          TextFormatting.RED.toString(),
          "gui.artisanworkstumps.waila.workstump.condition.fractured"
      );

    } else if (d < 0.50) {
      this.display.setCondition(
          "gui.artisanworkstumps.waila.workstump.condition",
          TextFormatting.YELLOW.toString(),
          "gui.artisanworkstumps.waila.workstump.condition.used"
      );

    } else if (d < 0.75) {
      this.display.setCondition(
          "gui.artisanworkstumps.waila.workstump.condition",
          TextFormatting.GOLD.toString(),
          "gui.artisanworkstumps.waila.workstump.condition.fair"
      );

    } else {
      this.display.setCondition(
          "gui.artisanworkstumps.waila.workstump.condition",
          TextFormatting.GREEN.toString(),
          "gui.artisanworkstumps.waila.workstump.condition.good"
      );
    }

    if (player != null) { // Add look-at item
      int distance = 5;
      Vec3d posVec = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
      RayTraceResult rayTraceResult = tile.getWorld().rayTraceBlocks(posVec, posVec.add(player.getLookVec().scale(distance)), false);

      if (rayTraceResult == null) {
        return;
      }

      if (rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) {
        return;
      }

      if (rayTraceResult.hitInfo instanceof InteractionRayTraceData.List) {
        InteractionRayTraceData.List list = (InteractionRayTraceData.List) rayTraceResult.hitInfo;

        for (InteractionRayTraceData data : list) {
          IInteraction interaction = data.getInteraction();

          if (interaction.isEnabled()
              && interaction instanceof IInteractionItemStack) {
            ItemStack stackInSlot = ((IInteractionItemStack) interaction).getStackInSlot();

            if (!stackInSlot.isEmpty()) {
              this.display.setHoveredItem(stackInSlot);
            }
          }
        }
      }
    }
  }

  public interface IWorkstumpDisplay {

    void setRecipeProgress(ItemStack input, ItemStack output, int progress, int maxProgress);

    void setRecipeOutputName(ItemStack itemStack);

    void setCondition(String langKey, String textColorString, String conditionLangKey);

    void setHoveredItem(ItemStack stackInSlot);
  }
}
