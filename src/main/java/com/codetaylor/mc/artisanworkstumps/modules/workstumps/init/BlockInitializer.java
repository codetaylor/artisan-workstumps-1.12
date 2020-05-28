package com.codetaylor.mc.artisanworkstumps.modules.workstumps.init;

import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumps;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.block.BlockWorkstump;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.TileWorkstump;
import com.codetaylor.mc.athenaeum.interaction.spi.TESRInteractable;
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

    registry.registerBlockWithItem(new BlockWorkstump("tailor"), BlockWorkstump.NAME_TAILOR);
    registry.registerBlockWithItem(new BlockWorkstump("carpenter"), BlockWorkstump.NAME_CARPENTER);
    registry.registerBlockWithItem(new BlockWorkstump("mason"), BlockWorkstump.NAME_MASON);
    registry.registerBlockWithItem(new BlockWorkstump("blacksmith"), BlockWorkstump.NAME_BLACKSMITH);
    registry.registerBlockWithItem(new BlockWorkstump("jeweler"), BlockWorkstump.NAME_JEWELER);
    registry.registerBlockWithItem(new BlockWorkstump("basic"), BlockWorkstump.NAME_BASIC);
    registry.registerBlockWithItem(new BlockWorkstump("engineer"), BlockWorkstump.NAME_ENGINEER);
    registry.registerBlockWithItem(new BlockWorkstump("mage"), BlockWorkstump.NAME_MAGE);
    registry.registerBlockWithItem(new BlockWorkstump("scribe"), BlockWorkstump.NAME_SCRIBE);
    registry.registerBlockWithItem(new BlockWorkstump("chemist"), BlockWorkstump.NAME_CHEMIST);
    registry.registerBlockWithItem(new BlockWorkstump("farmer"), BlockWorkstump.NAME_FARMER);
    registry.registerBlockWithItem(new BlockWorkstump("chef"), BlockWorkstump.NAME_CHEF);
    registry.registerBlockWithItem(new BlockWorkstump("designer"), BlockWorkstump.NAME_DESIGNER);
    registry.registerBlockWithItem(new BlockWorkstump("tanner"), BlockWorkstump.NAME_TANNER);
    registry.registerBlockWithItem(new BlockWorkstump("potter"), BlockWorkstump.NAME_POTTER);

    BlockInitializer.registerTileEntity(registry, TileWorkstump.class);
  }

  @SideOnly(Side.CLIENT)
  public static void onClientRegister(Registry registry) {

    registry.registerClientModelRegistrationStrategy(() -> {

      ModelRegistrationHelper.registerBlockItemModels(
          ModuleWorkstumps.Blocks.WORKSTUMP_TAILOR,
          ModuleWorkstumps.Blocks.WORKSTUMP_CARPENTER,
          ModuleWorkstumps.Blocks.WORKSTUMP_MASON,
          ModuleWorkstumps.Blocks.WORKSTUMP_BLACKSMITH,
          ModuleWorkstumps.Blocks.WORKSTUMP_JEWELER,
          ModuleWorkstumps.Blocks.WORKSTUMP_BASIC,
          ModuleWorkstumps.Blocks.WORKSTUMP_ENGINEER,
          ModuleWorkstumps.Blocks.WORKSTUMP_MAGE,
          ModuleWorkstumps.Blocks.WORKSTUMP_SCRIBE,
          ModuleWorkstumps.Blocks.WORKSTUMP_CHEMIST,
          ModuleWorkstumps.Blocks.WORKSTUMP_FARMER,
          ModuleWorkstumps.Blocks.WORKSTUMP_CHEF,
          ModuleWorkstumps.Blocks.WORKSTUMP_DESIGNER,
          ModuleWorkstumps.Blocks.WORKSTUMP_TANNER,
          ModuleWorkstumps.Blocks.WORKSTUMP_POTTER
      );
    });

    ClientRegistry.bindTileEntitySpecialRenderer(TileWorkstump.class, new TESRInteractable<>());
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
