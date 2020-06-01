package com.codetaylor.mc.artisanworkstumps.modules.workstumps;

import com.codetaylor.mc.athenaeum.integration.gamestages.Stages;
import net.minecraftforge.common.config.Config;

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
        "If this list is not empty, only the recipes listed here will be allowed.",
        "",
        "The whitelist takes priority over the blacklist.",
        "",
        "String format is a recipe resource location: (domain):(path) or (domain):*"
    })
    public String[] RECIPE_WHITELIST = new String[0];

    @Config.Comment({
        "If this list is not empty, recipes listed here will be disallowed.",
        "",
        "The whitelist takes priority over the blacklist.",
        "",
        "String format is a recipe resource location: (domain):(path) or (domain):*"
    })
    public String[] RECIPE_BLACKLIST = new String[0];

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
        "The number of tool hits required to complete a craft.",
        "Default: " + 4
    })
    @Config.RangeInt(min = 1)
    public int HITS_PER_CRAFT = 4;

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