package com.codetaylor.mc.artisanworkstumps.modules.core.plugin.top;

import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumps;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.util.I18nHelper;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.apiimpl.client.ElementTextRender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.io.IOException;

public class ElementTankLabel
    implements IElement {

  private final String textFormatting;
  private final FluidStack fluidStack;
  private final int capacity;

  private String renderText;
  public static final String LANG_KEY = "gui." + ModuleWorkstumps.MOD_ID + ".waila.tank.fluid";

  public ElementTankLabel(@Nullable TextFormatting textFormatting, FluidStack fluidStack, int capacity) {

    if (textFormatting == null) {
      this.textFormatting = null;

    } else {
      this.textFormatting = textFormatting.toString();
    }

    this.fluidStack = Preconditions.checkNotNull(fluidStack);
    this.capacity = capacity;
  }

  public ElementTankLabel(ByteBuf buf) {

    int length = buf.readInt();

    if (length > 0) {
      this.textFormatting = new PacketBuffer(buf).readString(length);

    } else {
      this.textFormatting = null;
    }

    NBTTagCompound compound;

    try {
      compound = new PacketBuffer(buf).readCompoundTag();

    } catch (IOException e) {
      this.fluidStack = null;
      throw new RuntimeException("", e);
    }

    this.capacity = buf.readInt();

    if (compound != null) {
      this.fluidStack = FluidStack.loadFluidStackFromNBT(compound);

    } else {
      this.fluidStack = null;
    }

    if (this.fluidStack != null) {
      StringBuilder builder = new StringBuilder();

      if (this.textFormatting != null) {
        String localizedName = this.fluidStack.getLocalizedName();
        this.renderText = this.textFormatting
            + I18nHelper.translateFormatted(LANG_KEY, localizedName, this.fluidStack.amount, this.capacity);

      } else {
        String localizedName = this.fluidStack.getLocalizedName();
        this.renderText = I18nHelper.translateFormatted(LANG_KEY, localizedName, this.fluidStack.amount, this.capacity);
      }
    }
  }

  @Override
  public void render(int x, int y) {

    if (this.renderText != null) {
      ElementTextRender.render(this.renderText, x, y);
    }
  }

  @Override
  public int getWidth() {

    if (this.renderText != null) {
      return ElementTextRender.getWidth(this.renderText);

    } else {
      return 10;
    }
  }

  @Override
  public int getHeight() {

    return 10;
  }

  @Override
  public void toBytes(ByteBuf buf) {

    if (this.textFormatting == null) {
      buf.writeInt(0);

    } else {
      buf.writeInt(this.textFormatting.length());
      new PacketBuffer(buf).writeString(this.textFormatting);
    }

    NBTTagCompound compound = this.fluidStack.writeToNBT(new NBTTagCompound());
    new PacketBuffer(buf).writeCompoundTag(compound);

    buf.writeInt(this.capacity);
  }

  @Override
  public int getID() {

    return PluginTOP.ELEMENT_TANK_LABEL;
  }
}
