package com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.workstump;

import com.codetaylor.mc.artisanworktables.api.internal.recipe.ICraftingMatrixStackHandler;
import com.codetaylor.mc.athenaeum.inventory.ObservableStackHandler;
import com.codetaylor.mc.athenaeum.network.tile.spi.ITileDataItemStackHandler;

public class StackHandlerInput
    extends ObservableStackHandler
    implements ITileDataItemStackHandler,
    ICraftingMatrixStackHandler {

  private final int width;
  private final int height;
  private final int maxStackSize;

  public StackHandlerInput(int width, int height, int maxStackSize) {

    super(width * height);
    this.width = width;
    this.height = height;
    this.maxStackSize = maxStackSize;
  }

  @Override
  public int getSlotLimit(int slot) {

    return this.maxStackSize;
  }

  @Override
  public int getWidth() {

    return this.width;
  }

  @Override
  public int getHeight() {

    return this.height;
  }

  public boolean isEmpty() {

    for (int i = 0; i < this.getSlots(); i++) {

      if (!this.getStackInSlot(i).isEmpty()) {
        return false;
      }
    }

    return true;
  }
}
