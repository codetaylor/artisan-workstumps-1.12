package com.codetaylor.mc.artisanworkstumps.modules.core.plugin.top;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.apiimpl.client.ElementTextRender;
import mcjty.theoneprobe.network.NetworkTools;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

public class ElementItemLabel
    implements IElement {

  private final String textFormatting;
  private final ItemStack itemStack;

  public ElementItemLabel(@Nullable TextFormatting textFormatting, ItemStack itemStack) {

    this(textFormatting == null ? null : textFormatting.toString(), itemStack);
  }

  public ElementItemLabel(@Nullable String textFormatting, ItemStack itemStack) {

    this.textFormatting = textFormatting;
    this.itemStack = itemStack;
  }

  public ElementItemLabel(ByteBuf buf) {

    int length = buf.readInt();

    if (length > 0) {
      this.textFormatting = new PacketBuffer(buf).readString(length);

    } else {
      this.textFormatting = null;
    }

    if (buf.readBoolean()) {
      this.itemStack = NetworkTools.readItemStack(buf);

    } else {
      this.itemStack = ItemStack.EMPTY;
    }
  }

  @Override
  public void render(int x, int y) {

    if (!this.itemStack.isEmpty()) {
      String text = this.itemStack.getDisplayName();

      if (this.textFormatting == null) {
        ElementTextRender.render(text, x, y);

      } else {
        ElementTextRender.render(this.textFormatting + text, x, y);
      }
    }
  }

  @Override
  public int getWidth() {

    if (!this.itemStack.isEmpty()) {
      String text = this.itemStack.getDisplayName();
      return ElementTextRender.getWidth(text);

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

    if (!this.itemStack.isEmpty()) {
      buf.writeBoolean(true);
      NetworkTools.writeItemStack(buf, this.itemStack);

    } else {
      buf.writeBoolean(false);
    }
  }

  @Override
  public int getID() {

    return PluginTOP.ELEMENT_ITEM_LABEL;
  }
}
