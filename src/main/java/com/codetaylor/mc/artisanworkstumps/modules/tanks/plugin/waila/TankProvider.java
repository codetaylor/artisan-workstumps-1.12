package com.codetaylor.mc.artisanworkstumps.modules.tanks.plugin.waila;

import com.codetaylor.mc.artisanworkstumps.lib.spi.plugin.hwyla.BodyProviderAdapter;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.tile.TileTankBase;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.util.I18nHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.List;

public class TankProvider
    extends BodyProviderAdapter
    implements TankProviderDelegate.ITankDisplay {

  private final TankProviderDelegate delegate;

  private List<String> tooltip;

  public TankProvider() {

    this.delegate = new TankProviderDelegate(this);
  }

  @Nonnull
  @Override
  public List<String> getWailaBody(
      ItemStack itemStack,
      List<String> tooltip,
      IWailaDataAccessor accessor,
      IWailaConfigHandler config
  ) {

    TileEntity tileEntity = accessor.getTileEntity();

    if (tileEntity instanceof TileTankBase) {
      this.tooltip = tooltip;
      this.delegate.display((TileTankBase) tileEntity);
      this.tooltip = null;
    }

    return tooltip;
  }

  @Override
  public void setFluid(String langKey, FluidStack fluid, int capacity) {

    String fluidLocalizedName = fluid.getLocalizedName();
    int amount = fluid.amount;
    this.tooltip.add(I18nHelper.translateFormatted(langKey, fluidLocalizedName, amount, capacity));
  }

  @Override
  public void setFluidEmpty(String langKey, int capacity) {

    this.tooltip.add(I18nHelper.translateFormatted(langKey, 0, capacity));
  }
}
