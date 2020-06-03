package com.codetaylor.mc.artisanworkstumps.modules.tanks.init;

import com.codetaylor.mc.artisanworkstumps.modules.tanks.ModuleTanks;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.block.BlockFluidStump;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.client.render.TESRFluidStump;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.tile.TileFluidStump;
import com.codetaylor.mc.athenaeum.registry.Registry;
import com.codetaylor.mc.athenaeum.util.ModelRegistrationHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class BlockInitializer {

  public static void onRegister(Registry registry) {

    registry.registerBlockWithItem(new BlockFluidStump(), BlockFluidStump.NAME);

    BlockInitializer.registerTileEntity(registry, TileFluidStump.class);
  }

  @SideOnly(Side.CLIENT)
  public static void onClientRegister(Registry registry) {

    registry.registerClientModelRegistrationStrategy(() -> {

      ModelRegistrationHelper.registerBlockItemModels(
          ModuleTanks.Blocks.FLUID_STUMP
      );
    });

    ClientRegistry.bindTileEntitySpecialRenderer(TileFluidStump.class, new TESRFluidStump());
  }

  private static void registerTileEntity(Registry registry, Class<? extends TileEntity> tileEntityClass) {

    registry.registerTileEntityRegistrationStrategy(() -> GameRegistry.registerTileEntity(
        tileEntityClass,
        new ResourceLocation(registry.getModId(), "tile." + tileEntityClass.getSimpleName())
    ));
  }

  private BlockInitializer() {
    //
  }
}
