package com.codetaylor.mc.artisanworkstumps.modules.workstumps;

import com.codetaylor.mc.artisanworkstumps.ModArtisanWorkstumps;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.block.BlockWorkstump;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.init.BlockInitializer;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.init.PacketInitializer;
import com.codetaylor.mc.artisanworktables.ModArtisanWorktables;
import com.codetaylor.mc.athenaeum.interaction.event.InteractionMouseScrollEventHandler;
import com.codetaylor.mc.athenaeum.module.ModuleBase;
import com.codetaylor.mc.athenaeum.network.IPacketRegistry;
import com.codetaylor.mc.athenaeum.network.IPacketService;
import com.codetaylor.mc.athenaeum.network.tile.ITileDataService;
import com.codetaylor.mc.athenaeum.registry.Registry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleWorkstumps
    extends ModuleBase {

  public static final String MOD_ID = ModArtisanWorkstumps.MOD_ID;
  public static final CreativeTabs CREATIVE_TAB = ModArtisanWorktables.CREATIVE_TAB;

  public static IPacketService PACKET_SERVICE;
  public static ITileDataService TILE_DATA_SERVICE;

  public ModuleWorkstumps() {

    super(0, MOD_ID);

    this.setRegistry(new Registry(MOD_ID, CREATIVE_TAB));
    this.enableAutoRegistry();

    PACKET_SERVICE = this.enableNetwork();
    TILE_DATA_SERVICE = this.enableNetworkTileDataService(PACKET_SERVICE);

    MinecraftForge.EVENT_BUS.register(new InteractionMouseScrollEventHandler(PACKET_SERVICE));
  }

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

  @GameRegistry.ObjectHolder(MOD_ID)
  public static class Blocks {

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_TAILOR)
    public static final BlockWorkstump WORKSTUMP_TAILOR;

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_CARPENTER)
    public static final BlockWorkstump WORKSTUMP_CARPENTER;

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_MASON)
    public static final BlockWorkstump WORKSTUMP_MASON;

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_BLACKSMITH)
    public static final BlockWorkstump WORKSTUMP_BLACKSMITH;

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_JEWELER)
    public static final BlockWorkstump WORKSTUMP_JEWELER;

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_BASIC)
    public static final BlockWorkstump WORKSTUMP_BASIC;

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_ENGINEER)
    public static final BlockWorkstump WORKSTUMP_ENGINEER;

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_MAGE)
    public static final BlockWorkstump WORKSTUMP_MAGE;

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_SCRIBE)
    public static final BlockWorkstump WORKSTUMP_SCRIBE;

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_CHEMIST)
    public static final BlockWorkstump WORKSTUMP_CHEMIST;

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_FARMER)
    public static final BlockWorkstump WORKSTUMP_FARMER;

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_CHEF)
    public static final BlockWorkstump WORKSTUMP_CHEF;

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_DESIGNER)
    public static final BlockWorkstump WORKSTUMP_DESIGNER;

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_TANNER)
    public static final BlockWorkstump WORKSTUMP_TANNER;

    @GameRegistry.ObjectHolder(BlockWorkstump.NAME_POTTER)
    public static final BlockWorkstump WORKSTUMP_POTTER;

    static {
      WORKSTUMP_TAILOR = null;
      WORKSTUMP_CARPENTER = null;
      WORKSTUMP_MASON = null;
      WORKSTUMP_BLACKSMITH = null;
      WORKSTUMP_JEWELER = null;
      WORKSTUMP_BASIC = null;
      WORKSTUMP_ENGINEER = null;
      WORKSTUMP_MAGE = null;
      WORKSTUMP_SCRIBE = null;
      WORKSTUMP_CHEMIST = null;
      WORKSTUMP_FARMER = null;
      WORKSTUMP_CHEF = null;
      WORKSTUMP_DESIGNER = null;
      WORKSTUMP_TANNER = null;
      WORKSTUMP_POTTER = null;
    }
  }
}
