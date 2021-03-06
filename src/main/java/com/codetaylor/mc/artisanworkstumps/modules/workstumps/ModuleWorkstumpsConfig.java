package com.codetaylor.mc.artisanworkstumps.modules.workstumps;

import com.codetaylor.mc.artisanworkstumps.ModArtisanWorkstumps;
import com.codetaylor.mc.athenaeum.integration.gamestages.Stages;
import com.codetaylor.mc.athenaeum.parser.recipe.item.MalformedRecipeItemException;
import com.codetaylor.mc.athenaeum.parser.recipe.item.ParseResult;
import com.codetaylor.mc.athenaeum.parser.recipe.item.RecipeItemParser;
import com.codetaylor.mc.athenaeum.util.OreDictHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;
import java.util.TreeMap;

@Config(modid = ModuleWorkstumps.MOD_ID, name = ModuleWorkstumps.MOD_ID + "/" + "module.Workstumps")
public class ModuleWorkstumpsConfig {

  @Config.Ignore
  public static Stages STAGES_WORKSTUMP = null;

  // ---------------------------------------------------------------------------
  // - Client
  // ---------------------------------------------------------------------------

  public static Client CLIENT = new Client();

  public static class Client {

    @Config.Comment({
        "Workstump interactions will give off some green particles to indicate",
        "that the recipe / tool combination is valid and recipe progress has",
        "incremented.",
        "",
        "Set to false to disable these progression particles."
    })
    public boolean SHOW_RECIPE_PROGRESSION_PARTICLES = true;
  }

  // ---------------------------------------------------------------------------
  // - Workstump
  // ---------------------------------------------------------------------------

  public static Workstump WORKSTUMP = new Workstump();

  public static class Workstump {

    @Config.Comment({
        "The tool required when the recipe does not specify a tool.",
        "A tool for each table is required.",
        "Syntax: (domain):(path):(meta)",
        " - meta is optional, supports oredict",
        "Default: ore:artisansHammer"
    })
    public Map<String, String> DEFAULT_RECIPE_TOOL = new TreeMap<String, String>() {{
      put("tailor", "ore:artisansNeedle");
      put("carpenter", "ore:artisansFramingHammer");
      put("mason", "ore:artisansChisel");
      put("blacksmith", "ore:artisansHammer");
      put("jeweler", "ore:artisansGemCutter");
      put("basic", "ore:artisansHammer");
      put("engineer", "ore:artisansSpanner");
      put("mage", "ore:artisansAthame");
      put("scribe", "ore:artisansQuill");
      put("chemist", "ore:artisansBeaker");
      put("farmer", "ore:artisansTrowel");
      put("chef", "ore:artisansCuttingBoard");
      put("designer", "ore:artisansTSquare");
      put("tanner", "ore:artisansGroover");
      put("potter", "ore:artisansCarver");
    }};

    public boolean isDefaultTool(String tableName, ItemStack heldItemStack) {

      try {
        String toolString = this.DEFAULT_RECIPE_TOOL.get(tableName);
        return Workstump.matches(heldItemStack, toolString);

      } catch (MalformedRecipeItemException e) {
        ModArtisanWorkstumps.LOGGER.error("Error parsing default recipe tool from workstump config", e);
        return false;
      }
    }

    @Config.Comment({
        "The tool required to repair a workstump.",
        "Syntax: (domain):(path):(meta)",
        " - meta is optional, supports oredict",
        "Default: ore:artisansFramingHammer"
    })
    public String REPAIR_TOOL = "ore:artisansFramingHammer";

    public boolean isRepairTool(ItemStack itemStack) {

      try {
        return Workstump.matches(itemStack, REPAIR_TOOL);

      } catch (MalformedRecipeItemException e) {
        ModArtisanWorkstumps.LOGGER.error("Error parsing repair tool from workstump config", e);
        return false;
      }
    }

    private static boolean matches(ItemStack itemStack, String itemString) throws MalformedRecipeItemException {

      ParseResult parseResult = RecipeItemParser.INSTANCE.parse(itemString);

      if ("ore".equals(parseResult.getDomain())) {
        return OreDictHelper.contains(parseResult.getPath(), itemStack);

      } else {
        Item item = itemStack.getItem();
        ResourceLocation registryName = item.getRegistryName();

        if (registryName == null) {
          return false;
        }

        return registryName.getResourceDomain().equals(parseResult.getDomain())
            && registryName.getResourcePath().equals(parseResult.getPath())
            && (parseResult.getMeta() == OreDictionary.WILDCARD_VALUE || parseResult.getMeta() == itemStack.getMetadata());
      }
    }

    @Config.Comment({
        "If true, the workstump can be repaired using ore:plankWood and the",
        "configured tool.",
        "Default: " + true
    })
    public boolean ALLOW_REPAIR = true;

    @Config.Comment({
        "The damage applied to the repair tool per repair.",
        "Default: " + 1
    })
    @Config.RangeInt(min = 0)
    public int REPAIR_TOOL_DAMAGE = 1;

    @Config.Comment({
        "The amount of workstump damage repaired per repair.",
        "Default: " + 4
    })
    @Config.RangeInt(min = 0)
    public int AMOUNT_OF_DAMAGE_REPAIRED_PER_REPAIR = 4;

    @Config.Comment({
        "The amount of planks consumed per repair.",
        "Default: " + 1
    })
    @Config.RangeInt(min = 0)
    public int AMOUNT_OF_PLANKS_CONSUMED_PER_REPAIR = 1;

    @Config.Comment({
        "The damage applied to the default tool when a recipe doesn't have a",
        "tool requirement.",
        "Default: " + 1
    })
    @Config.RangeInt(min = 0)
    public int DEFAULT_RECIPE_TOOL_DAMAGE = 1;

    @Config.Comment({
        "If this is true, a player will be allowed to sneak + click using an",
        "empty hand to remove all items from the workstump's crafting grid.",
        "The removed items will be placed into the player's inventory or on top",
        "of the workstump if the player's inventory is full.",
        "Default: " + false
    })
    public boolean ALLOW_RECIPE_CLEAR = false;

    @Config.Comment({
        "If this is true, a player will be allowed to sneak + click using a",
        "tool to automatically place items from their inventory into the",
        "workstump's crafting grid that match the ingredients for the last",
        "recipe completed. The tool will be damaged, see RECIPE_REPEAT_TOOL_DAMAGE.",
        "Default: " + false
    })
    public boolean ALLOW_RECIPE_REPEAT = false;

    @Config.Comment({
        "If ALLOW_RECIPE_REPEAT is enabled, this is the amount of damage that",
        "will be applied to the tool. Set to zero to disable.",
        "Default: " + 1
    })
    @Config.RangeInt(min = 0)
    public int RECIPE_REPEAT_TOOL_DAMAGE = 1;

    @Config.Comment({
        "The number of hits required per harvest level of the tool used.",
        "The index into the array is the harvest level, the value at that index",
        "is the required number of uses. The array can be expanded as needed.",
        "If the harvest level of the tool used exceeds the array length, the",
        "last element in the array is used.",
        "",
        "ie. {wood, stone, iron, diamond}",
        "Valid values are in the range: [1,+int]",
        "Default: {4, 3, 2, 1}"
    })
    public int[] HITS_PER_CRAFT = new int[]{4, 3, 2, 1};

    @Config.Comment({
        "The maximum stack size for each slot in the crafting grid.",
        "Default: " + 1
    })
    @Config.RangeInt(min = 1, max = 64)
    public int GRID_MAX_STACK_SIZE = 1;

    @Config.Comment({
        "The maximum stack size for each slot in the shelf.",
        "Default: " + 1
    })
    @Config.RangeInt(min = 1, max = 64)
    public int SHELF_MAX_STACK_SIZE = 1;

    @Config.Comment({
        "If true, the workstump has durability and will break after the configured",
        "number of crafts completed.",
        "Default: " + true
    })
    public boolean USES_DURABILITY = true;

    @Config.Comment({
        "The number of crafts that the workstump can perform before it breaks.",
        "This is only relevant if the `USES_DURABILITY` flag is true.",
        "Default: " + 64
    })
    @Config.RangeInt(min = 1)
    public int DURABILITY = 64;

    @Config.Comment({
        "How much exhaustion to apply per hit.",
        "Default: " + 1
    })
    @Config.RangeDouble(min = 0, max = 40)
    public double EXHAUSTION_COST_PER_HIT = 1;

    @Config.Comment({
        "How much exhaustion to apply per completed craft.",
        "Default: " + 0
    })
    @Config.RangeDouble(min = 0, max = 40)
    public double EXHAUSTION_COST_PER_CRAFT_COMPLETE = 0;

    @Config.Comment({
        "Minimum amount of hunger the player needs to use.",
        "Default: " + 3
    })
    @Config.RangeInt(min = 0, max = 20)
    public int MINIMUM_HUNGER_TO_USE = 3;
  }

}