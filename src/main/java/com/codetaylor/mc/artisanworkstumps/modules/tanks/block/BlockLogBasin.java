package com.codetaylor.mc.artisanworkstumps.modules.tanks.block;

import com.codetaylor.mc.artisanworkstumps.modules.tanks.ModuleTanksConfig;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.tile.TileLogBasin;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockLogBasin
    extends BlockTankBase {

  public static final String NAME = "log_basin";

  public BlockLogBasin() {

    super(Material.WOOD);
    this.setHarvestLevel("axe", 0);
    this.setHardness(2);
  }

  // ---------------------------------------------------------------------------
  // - Configuration
  // ---------------------------------------------------------------------------

  @Override
  protected int getCapacity() {

    return ModuleTanksConfig.FLUID_STUMP.CAPACITY;
  }

  @Override
  protected boolean holdsHotFluids() {

    return ModuleTanksConfig.FLUID_STUMP.HOLDS_HOT_FLUIDS;
  }

  @Override
  protected boolean holdsContentsWhenBroken() {

    return ModuleTanksConfig.FLUID_STUMP.HOLDS_CONTENTS_WHEN_BROKEN;
  }

  // ---------------------------------------------------------------------------
  // - Tile Entity
  // ---------------------------------------------------------------------------

  @Nullable
  @Override
  public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {

    return new TileLogBasin();
  }

}
