package com.codetaylor.mc.artisanworkstumps.modules.workstumps.plugin.jei;

import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumps;
import mezz.jei.api.IGuiHelper;
import net.minecraft.util.ResourceLocation;

/* package */ class JEICategoryFactory {

  private IGuiHelper guiHelper;
  public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(
      ModuleWorkstumps.MOD_ID,
      "textures/gui/jei.png"
  );

  /* package */ JEICategoryFactory(IGuiHelper guiHelper) {

    this.guiHelper = guiHelper;
  }

  /* package */ JEICategoryWorkstump createCategory(String name) {

    return new JEICategoryWorkstump(
        this.createTitleTranslateKey(name),
        this.guiHelper.createDrawable(TEXTURE_LOCATION, 0, 0, 151, 64),
        PluginJEI.createUID(name),
        this.guiHelper
    );
  }

  private String createTitleTranslateKey(String name) {

    return String.format("tile.artisanworkstumps.workstump_%s.name", name);
  }
}
