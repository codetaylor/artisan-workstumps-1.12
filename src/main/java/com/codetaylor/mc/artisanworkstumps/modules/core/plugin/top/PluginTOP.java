package com.codetaylor.mc.artisanworkstumps.modules.core.plugin.top;

import mcjty.theoneprobe.api.ITheOneProbe;

import java.util.function.Function;

@SuppressWarnings("unused")
public class PluginTOP {

  public static int ELEMENT_TEXT_LOCALIZED;
  public static int ELEMENT_ITEM_LABEL;
  public static int ELEMENT_TANK_LABEL;

  public static class Callback
      implements Function<ITheOneProbe, Void> {

    @Override
    public Void apply(ITheOneProbe top) {

      PluginTOP.ELEMENT_TEXT_LOCALIZED = top.registerElementFactory(ElementTextLocalized::new);
      PluginTOP.ELEMENT_ITEM_LABEL = top.registerElementFactory(ElementItemLabel::new);
      PluginTOP.ELEMENT_TANK_LABEL = top.registerElementFactory(ElementTankLabel::new);
      return null;
    }
  }

}
