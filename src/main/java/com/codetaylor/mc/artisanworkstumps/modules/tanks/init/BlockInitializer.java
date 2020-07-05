package com.codetaylor.mc.artisanworkstumps.modules.tanks.init;

import com.codetaylor.mc.artisanworkstumps.modules.tanks.ModuleTanks;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.block.BlockLogBasin;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.block.BlockStoneBasin;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.client.render.TESRFluidTank;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.tile.TileLogBasin;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.tile.TileStoneBasin;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.tile.TileTankBase;
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

    registry.registerBlockWithItem(new BlockLogBasin(), BlockLogBasin.NAME);
    registry.registerBlockWithItem(new BlockStoneBasin(), BlockStoneBasin.NAME);

    BlockInitializer.registerTileEntity(registry, TileLogBasin.class);
    BlockInitializer.registerTileEntity(registry, TileStoneBasin.class);
  }

  @SideOnly(Side.CLIENT)
  public static void onClientRegister(Registry registry) {

    registry.registerClientModelRegistrationStrategy(() -> {

      ModelRegistrationHelper.registerBlockItemModels(
          ModuleTanks.Blocks.LOG_BASIN,
          ModuleTanks.Blocks.STONE_BASIN
      );
    });

    ClientRegistry.bindTileEntitySpecialRenderer(TileTankBase.class, new TESRFluidTank());
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
