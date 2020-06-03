package com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.workstump;

import com.codetaylor.mc.athenaeum.inventory.ObservableStackHandler;
import com.codetaylor.mc.athenaeum.network.tile.spi.ITileDataItemStackHandler;

public class StackHandlerShelf
    extends ObservableStackHandler
    implements ITileDataItemStackHandler {

  private final int maxStackSize;

  public StackHandlerShelf(int maxStackSize) {

    super(3);
    this.maxStackSize = maxStackSize;
  }

  @Override
  public int getSlotLimit(int slot) {

    return this.maxStackSize;
  }
}
