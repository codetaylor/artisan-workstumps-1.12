package com.codetaylor.mc.artisanworkstumps.modules.tanks.block;

import com.codetaylor.mc.artisanworkstumps.modules.tanks.ModuleTanksConfig;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.tile.TileStoneBasin;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockStoneBasin
    extends BlockTankBase {

  public static final String NAME = "stone_basin";

  public BlockStoneBasin() {

    super(Material.ROCK);
    this.setHarvestLevel("pickaxe", 0);
    this.setHardness(2);
  }

  // ---------------------------------------------------------------------------
  // - Drops
  // ---------------------------------------------------------------------------

  @Override
  protected boolean canHoldContentsWhenBroken() {

    return ModuleTanksConfig.STONE_BASIN.HOLDS_CONTENTS_WHEN_BROKEN;
  }

  // ---------------------------------------------------------------------------
  // - Tile Entity
  // ---------------------------------------------------------------------------

  @Nullable
  @Override
  public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {

    return new TileStoneBasin();
  }

}
