package com.codetaylor.mc.artisanworkstumps.modules.tanks.tile;

import com.codetaylor.mc.artisanworkstumps.modules.tanks.ModuleTanksConfig;
import com.codetaylor.mc.athenaeum.integration.gamestages.Stages;

import javax.annotation.Nullable;

public class TileStoneBasin
    extends TileTankBase {

  @Override
  protected int getTankCapacity() {

    return ModuleTanksConfig.STONE_BASIN.CAPACITY;
  }

  @Override
  protected boolean canHoldHotFluids() {

    return ModuleTanksConfig.STONE_BASIN.HOLDS_HOT_FLUIDS;
  }

  @Override
  protected int getHotFluidTemperature() {

    return ModuleTanksConfig.STONE_BASIN.HOT_TEMPERATURE;
  }

  @Nullable
  @Override
  public Stages getStages() {

    return ModuleTanksConfig.STAGES_STONE_BASIN;
  }
}
