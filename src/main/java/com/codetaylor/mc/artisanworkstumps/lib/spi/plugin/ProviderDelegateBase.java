package com.codetaylor.mc.artisanworkstumps.lib.spi.plugin;

import net.minecraft.tileentity.TileEntity;

public abstract class ProviderDelegateBase<D, T extends TileEntity> {

  protected final D display;

  protected ProviderDelegateBase(D display) {

    this.display = display;
  }

  public abstract void display(T tile);
}
