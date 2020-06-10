package com.codetaylor.mc.artisanworkstumps.modules.tanks.plugin.top;

import mcjty.theoneprobe.api.ITheOneProbe;

import java.util.function.Function;

@SuppressWarnings("unused")
public class PluginTOP {

  public static class Callback
      implements Function<ITheOneProbe, Void> {

    @Override
    public Void apply(ITheOneProbe top) {

      top.registerProvider(new TankProvider());
      return null;
    }
  }
}
