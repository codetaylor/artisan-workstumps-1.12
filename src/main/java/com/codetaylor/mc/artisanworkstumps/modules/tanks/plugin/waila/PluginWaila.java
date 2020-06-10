package com.codetaylor.mc.artisanworkstumps.modules.tanks.plugin.waila;

import com.codetaylor.mc.artisanworkstumps.modules.tanks.tile.TileTankBase;
import mcp.mobius.waila.api.IWailaRegistrar;

public class PluginWaila {

  @SuppressWarnings("unused")
  public static void wailaCallback(IWailaRegistrar registrar) {

    {
      TankProvider provider = new TankProvider();
      registrar.registerBodyProvider(provider, TileTankBase.class);
    }
  }
}
