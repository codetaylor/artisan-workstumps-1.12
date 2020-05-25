package com.codetaylor.mc.artisanworkstumps.modules.workstumps;

import com.codetaylor.mc.artisanworkstumps.ModArtisanWorkstumps;
import com.codetaylor.mc.artisanworktables.ModArtisanWorktables;
import com.codetaylor.mc.athenaeum.module.ModuleBase;
import com.codetaylor.mc.athenaeum.network.IPacketService;
import com.codetaylor.mc.athenaeum.network.tile.ITileDataService;
import com.codetaylor.mc.athenaeum.registry.Registry;
import net.minecraft.creativetab.CreativeTabs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModuleWorkstumps
    extends ModuleBase {

  public static final String MOD_NAME = ModArtisanWorkstumps.NAME;
  public static final String MOD_ID = ModArtisanWorkstumps.MOD_ID;
  public static final CreativeTabs CREATIVE_TAB = ModArtisanWorktables.CREATIVE_TAB;
  public static final Logger LOG = LogManager.getLogger(MOD_ID);

  public static IPacketService PACKET_SERVICE;
  public static ITileDataService TILE_DATA_SERVICE;

  public ModuleWorkstumps() {

    super(0, MOD_ID);

    this.setRegistry(new Registry(MOD_ID, CREATIVE_TAB));
    this.enableAutoRegistry();

    PACKET_SERVICE = this.enableNetwork();
    TILE_DATA_SERVICE = this.enableNetworkTileDataService(PACKET_SERVICE);
  }

}
