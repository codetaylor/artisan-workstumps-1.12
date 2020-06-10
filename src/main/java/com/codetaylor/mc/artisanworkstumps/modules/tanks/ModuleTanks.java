package com.codetaylor.mc.artisanworkstumps.modules.tanks;

import com.codetaylor.mc.artisanworkstumps.ModArtisanWorkstumps;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.block.BlockLogBasin;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.init.BlockInitializer;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.init.PacketInitializer;
import com.codetaylor.mc.artisanworktables.ModArtisanWorktables;
import com.codetaylor.mc.athenaeum.module.ModuleBase;
import com.codetaylor.mc.athenaeum.network.IPacketRegistry;
import com.codetaylor.mc.athenaeum.network.IPacketService;
import com.codetaylor.mc.athenaeum.network.tile.ITileDataService;
import com.codetaylor.mc.athenaeum.registry.Registry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleTanks
    extends ModuleBase {

  public static final String MOD_ID = ModArtisanWorkstumps.MOD_ID;
  public static final CreativeTabs CREATIVE_TAB = ModArtisanWorktables.CREATIVE_TAB;

  public static IPacketService PACKET_SERVICE;
  public static ITileDataService TILE_DATA_SERVICE;

  public ModuleTanks() {

    super(0, MOD_ID);

    this.setRegistry(new Registry(MOD_ID, CREATIVE_TAB));
    this.enableAutoRegistry();

    PACKET_SERVICE = this.enableNetwork();
    TILE_DATA_SERVICE = this.enableNetworkTileDataService(PACKET_SERVICE);
  }

  @Override
  public void onPreInitializationEvent(FMLPreInitializationEvent event) {

    super.onPreInitializationEvent(event);

    FMLInterModComms.sendFunctionMessage(
        "theoneprobe",
        "getTheOneProbe",
        "com.codetaylor.mc.artisanworkstumps.modules.tanks.plugin.top.PluginTOP$Callback"
    );
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void onClientPreInitializationEvent(FMLPreInitializationEvent event) {

    super.onClientPreInitializationEvent(event);

    FMLInterModComms.sendMessage(
        "waila",
        "register",
        "com.codetaylor.mc.artisanworkstumps.modules.tanks.plugin.waila.PluginWaila.wailaCallback"
    );
  }

  // ---------------------------------------------------------------------------
  // - Registration
  // ---------------------------------------------------------------------------

  @Override
  public void onNetworkRegister(IPacketRegistry registry) {

    PacketInitializer.register(registry);
  }

  @Override
  public void onRegister(Registry registry) {

    BlockInitializer.onRegister(registry);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void onClientRegister(Registry registry) {

    BlockInitializer.onClientRegister(registry);
  }

  // ---------------------------------------------------------------------------
  // - Object Holders
  // ---------------------------------------------------------------------------

  @GameRegistry.ObjectHolder(MOD_ID)
  public static class Blocks {

    @GameRegistry.ObjectHolder(BlockLogBasin.NAME)
    public static final BlockLogBasin LOG_BASIN;

    static {
      LOG_BASIN = null;
    }
  }
}
