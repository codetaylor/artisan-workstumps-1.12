package com.codetaylor.mc.artisanworkstumps.modules.tanks.plugin.waila;

import com.codetaylor.mc.artisanworkstumps.lib.spi.plugin.ProviderDelegateBase;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.ModuleTanks;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.tile.TileTankBase;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class TankProviderDelegate
    extends ProviderDelegateBase<TankProviderDelegate.ITankDisplay, TileTankBase> {

  public TankProviderDelegate(ITankDisplay display) {

    super(display);
  }

  @Override
  public void display(TileTankBase tile) {

    FluidTank fluidTank = tile.getFluidTank();
    FluidStack fluid = fluidTank.getFluid();

    if (fluid != null) {
      String langKey = "gui." + ModuleTanks.MOD_ID + ".waila.tank.fluid";
      this.display.setFluid(langKey, fluid, fluidTank.getCapacity());

    } else {
      String langKey = "gui." + ModuleTanks.MOD_ID + ".waila.tank.empty";
      this.display.setFluidEmpty(langKey, fluidTank.getCapacity());
    }
  }

  public interface ITankDisplay {

    void setFluid(String langKey, FluidStack fluid, int capacity);

    void setFluidEmpty(String langKey, int capacity);
  }
}
