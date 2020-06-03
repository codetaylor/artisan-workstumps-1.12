package com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.workstump;

import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.TileWorkstump;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.IArtisanItemStack;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.ICraftingContext;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.ICraftingMatrixStackHandler;
import com.codetaylor.mc.artisanworktables.api.internal.reference.EnumTier;
import com.codetaylor.mc.artisanworktables.api.internal.reference.EnumType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public final class FactoryCraftingContext {

  private static final SecondaryOutputHandler SECONDARY_OUTPUT_HANDLER;
  private static final FluidHandler FLUID_HANDLER;

  static {
    SECONDARY_OUTPUT_HANDLER = new SecondaryOutputHandler();
    FLUID_HANDLER = new FluidHandler();
  }

  public static ICraftingContext create(
      final TileWorkstump tile,
      final EntityPlayer player,
      @Nullable IFluidHandler fluidHandler
  ) {

    ItemStack heldItem = player.getHeldItemMainhand();
    final IItemHandlerModifiable toolHandler = new ItemStackHandler(1);
    toolHandler.setStackInSlot(0, heldItem);

    return new ICraftingContext() {

      @Override
      public World getWorld() {

        return tile.getWorld();
      }

      @Override
      public Optional<EntityPlayer> getPlayer() {

        return Optional.of(player);
      }

      @Override
      public ICraftingMatrixStackHandler getCraftingMatrixHandler() {

        return tile.getStackHandlerInput();
      }

      @Override
      public IItemHandlerModifiable getToolHandler() {

        return toolHandler;
      }

      @Override
      public IItemHandler getSecondaryOutputHandler() {

        return SECONDARY_OUTPUT_HANDLER;
      }

      @Nullable
      @Override
      public IItemHandlerModifiable getSecondaryIngredientHandler() {

        return null;
      }

      @Override
      public IFluidHandler getFluidHandler() {

        return (fluidHandler == null) ? FLUID_HANDLER : fluidHandler;
      }

      @Nullable
      @Override
      public IItemHandler getToolReplacementHandler() {

        // Return null to prevent the tool replacement logic from running.
        return null;
      }

      @Override
      public EnumType getType() {

        return EnumType.fromName(tile.getTableName());
      }

      @Override
      public EnumTier getTier() {

        return EnumTier.WORKTABLE;
      }

      @Override
      public BlockPos getPosition() {

        return tile.getPos();
      }
    };

  }

  private FactoryCraftingContext() {
    //
  }

  /**
   * Used as a dummy secondary output handler that returns the given item stack
   * when inserted in order to force the recipe logic to output the items into
   * the world.
   *
   * @see com.codetaylor.mc.artisanworktables.api.recipe.ArtisanRecipe#generateExtraOutput(ICraftingContext, IArtisanItemStack)
   */
  public static class SecondaryOutputHandler
      implements IItemHandler {

    @Override
    public int getSlots() {

      return 3;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {

      return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

      return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {

      return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {

      return 0;
    }
  }

  /**
   * Used when no fluid handler is available.
   */
  public static class FluidHandler
      implements IFluidHandler {

    private IFluidTankProperties[] properties;

    public FluidHandler() {

      this.properties = new IFluidTankProperties[0];
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {

      return this.properties;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {

      return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {

      return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {

      return null;
    }
  }

}
