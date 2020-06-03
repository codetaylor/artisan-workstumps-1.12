package com.codetaylor.mc.artisanworkstumps.modules.tanks;

import com.codetaylor.mc.athenaeum.integration.gamestages.Stages;
import net.minecraftforge.common.config.Config;

@Config(modid = ModuleTanks.MOD_ID, name = ModuleTanks.MOD_ID + "/" + "module.Tanks")
public class ModuleTanksConfig {

  @Config.Ignore
  public static Stages STAGES_FLUID_STUMP = null;

  // ---------------------------------------------------------------------------
  // - Fluid Stump
  // ---------------------------------------------------------------------------

  public static FluidStump FLUID_STUMP = new FluidStump();

  public static class FluidStump {

    @Config.Comment({
        "The amount of fluid this container can hold in mB.",
        "Default: " + 1000
    })
    public int CAPACITY = 1000;

    @Config.Comment({
        "The temperature that the container considers hot.",
        "The temperature of lava is 1300 and water is 300",
        "Default: " + 450
    })
    @Config.RangeInt
    public int HOT_TEMPERATURE = 450;

    @Config.Comment({
        "If false, the container will break when a hot fluid is placed inside,",
        "and the fluid will spawn in the world where the tank was.",
        "Default: " + false
    })
    public boolean HOLDS_HOT_FLUIDS = false;

    @Config.Comment({
        "True if the tank holds its contents when broken.",
        "Default: " + false
    })
    public boolean HOLDS_CONTENTS_WHEN_BROKEN = false;
  }

}
