package com.codetaylor.mc.artisanworkstumps.modules.workstumps.util;

import net.minecraft.util.text.translation.I18n;

public final class I18nHelper {

  public static String translateFormatted(String key, Object... pars) {
    // translates twice to allow rerouting/alias
    return I18n.translateToLocal(I18n.translateToLocalFormatted(key, (Object[]) pars).trim()).trim();
  }

  private I18nHelper() {
    //
  }

}
