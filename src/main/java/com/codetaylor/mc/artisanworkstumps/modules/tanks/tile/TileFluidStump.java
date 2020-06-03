package com.codetaylor.mc.artisanworkstumps.modules.tanks.tile;

import com.codetaylor.mc.artisanworkstumps.modules.tanks.ModuleTanksConfig;
import com.codetaylor.mc.athenaeum.integration.gamestages.Stages;

import javax.annotation.Nullable;

public class TileFluidStump
    extends TileTankBase {

  @Override
  protected int getTankCapacity() {

    return ModuleTanksConfig.FLUID_STUMP.CAPACITY;
  }

  @Override
  protected boolean canHoldHotFluids() {

    return ModuleTanksConfig.FLUID_STUMP.HOLDS_HOT_FLUIDS;
  }

  @Override
  protected int getHotFluidTemperature() {

    return ModuleTanksConfig.FLUID_STUMP.HOT_TEMPERATURE;
  }

  @Nullable
  @Override
  public Stages getStages() {

    return ModuleTanksConfig.STAGES_FLUID_STUMP;
  }
}
