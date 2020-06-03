package com.codetaylor.mc.artisanworkstumps.modules.tanks.client.render;

import com.codetaylor.mc.artisanworkstumps.modules.tanks.ModuleTanks;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.tile.TileTankBase;
import com.codetaylor.mc.athenaeum.util.Properties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nonnull;

public class TESRFluidStump
    extends FastTESR<TileTankBase> {

  private static final float PX = 0.0625f;
  private static final float INSET = PX * 0.1f;

  @Override
  public void renderTileEntityFast(
      @Nonnull TileTankBase tile,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float partial,
      @Nonnull BufferBuilder buffer
  ) {

    FluidTank fluidTank = tile.getFluidTank();
    FluidStack fluidStack = fluidTank.getFluid();

    if (fluidStack != null) {

      Fluid fluid = fluidStack.getFluid();
      TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
      TextureAtlasSprite still = textureMapBlocks.getAtlasSprite(fluid.getStill(fluidStack).toString());

      int color = fluid.getColor(fluidStack);
      float r = ((color >> 16) & 0xFF) / 255f;
      float g = ((color >> 8) & 0xFF) / 255f;
      float b = ((color >> 0) & 0xFF) / 255f;

      BlockPos blockpos = new BlockPos(tile.getPos());
      World world = tile.getWorld();
      int i = world.isBlockLoaded(blockpos) ? world.getCombinedLight(blockpos, 0) : 0;
      int j = i >> 0x10 & 0xFFFF;
      int k = i & 0xFFFF;

      float percent = fluidTank.getFluidAmount() / (float) fluidTank.getCapacity();
      float level = (PX * 6) * percent + PX + PX * 8;

      IBlockState blockState = world.getBlockState(tile.getPos());

      if (blockState.getBlock() != ModuleTanks.Blocks.FLUID_STUMP) {
        return;
      }

      switch (blockState.getValue(Properties.FACING_HORIZONTAL)) {
        // These are rotated because the block model is rotated in the block state
        case NORTH:
          buffer.setTranslation(x + PX * 4, y, z + PX * 2);
          break;
        case EAST:
          buffer.setTranslation(x + PX * 6, y, z + PX * 4);
          break;
        case SOUTH:
          buffer.setTranslation(x + PX * 4, y, z + PX * 6);
          break;
        case WEST:
          buffer.setTranslation(x + PX * 2, y, z + PX * 4);
          break;
      }

      float size = PX * 8;

      // TOP
      buffer
          .pos(INSET, level, INSET)
          .color(r, g, b, 1f)
          .tex(still.getMinU(), still.getMinV())
          .lightmap(j, k)
          .endVertex();
      buffer
          .pos(size - INSET, level, INSET)
          .color(r, g, b, 1f)
          .tex(still.getMaxU(), still.getMinV())
          .lightmap(j, k)
          .endVertex();
      buffer
          .pos(size - INSET, level, size - INSET)
          .color(r, g, b, 1f)
          .tex(still.getMaxU(), still.getMaxV())
          .lightmap(j, k)
          .endVertex();
      buffer
          .pos(INSET, level, size - INSET)
          .color(r, g, b, 1f)
          .tex(still.getMinU(), still.getMaxV())
          .lightmap(j, k)
          .endVertex();
    }
  }
}
