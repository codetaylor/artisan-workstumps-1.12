package com.codetaylor.mc.artisanworkstumps.modules.workstumps.plugin.waila;

import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.TileWorkstump;
import mcp.mobius.waila.api.IWailaRegistrar;

public class PluginWaila {

  @SuppressWarnings("unused")
  public static void wailaCallback(IWailaRegistrar registrar) {

    registrar.registerBodyProvider(new WorkstumpProvider(), TileWorkstump.class);
  }
}
