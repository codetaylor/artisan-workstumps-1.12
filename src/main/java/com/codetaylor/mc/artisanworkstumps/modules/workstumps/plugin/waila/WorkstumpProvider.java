package com.codetaylor.mc.artisanworkstumps.modules.workstumps.plugin.waila;

import com.codetaylor.mc.artisanworkstumps.lib.spi.plugin.hwyla.BodyProviderAdapter;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.TileWorkstump;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.util.I18nHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import java.util.List;

public class WorkstumpProvider
    extends BodyProviderAdapter
    implements WorkstumpProviderDelegate.IWorkstumpDisplay {

  private final WorkstumpProviderDelegate delegate;

  private List<String> tooltip;

  public WorkstumpProvider() {

    this.delegate = new WorkstumpProviderDelegate(this);
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

    if (tileEntity instanceof TileWorkstump) {
      this.tooltip = tooltip;
      this.delegate.display((TileWorkstump) tileEntity, Minecraft.getMinecraft().player);
      this.tooltip = null;
    }

    return tooltip;
  }

  @Override
  public void setRecipeProgress(ItemStack input, ItemStack output, int progress, int maxProgress) {

    String renderString = WailaHelper.getStackRenderString(input)
        + WailaHelper.getProgressRenderString(progress, maxProgress)
        + WailaHelper.getStackRenderString(output);
    this.tooltip.add(renderString);
  }

  @Override
  public void setRecipeOutputName(ItemStack itemStack) {

    this.tooltip.add(itemStack.getDisplayName());
  }

  @Override
  public void setCondition(String langKey, String textColorString, String conditionLangKey) {

    String condition = I18nHelper.translateFormatted(conditionLangKey);
    this.tooltip.add(I18nHelper.translateFormatted(langKey, textColorString, condition));
  }

  @Override
  public void setHoveredItem(ItemStack stackInSlot) {

    String displayName = stackInSlot.getDisplayName();
    int count = stackInSlot.getCount();

    if (count == 1) {
      this.tooltip.add(displayName);

    } else {
      String localized = I18nHelper.translateFormatted(WorkstumpProviderDelegate.LANG_KEY_HOVERED_ITEM_QUANTITY, displayName, count);
      this.tooltip.add(localized);
    }
  }
}
