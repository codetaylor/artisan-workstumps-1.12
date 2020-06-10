package com.codetaylor.mc.artisanworkstumps.modules.workstumps.plugin.top;

import com.codetaylor.mc.artisanworkstumps.modules.core.plugin.top.ElementTextLocalized;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumps;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.plugin.waila.WorkstumpProviderDelegate;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.TileWorkstump;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorkstumpProvider
    implements IProbeInfoProvider,
    WorkstumpProviderDelegate.IWorkstumpDisplay {

  private final WorkstumpProviderDelegate delegate;

  private IProbeInfo probeInfo;

  public WorkstumpProvider() {

    this.delegate = new WorkstumpProviderDelegate(this);
  }

  @Override
  public String getID() {

    return ModuleWorkstumps.MOD_ID + ":" + this.getClass().getName();
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {

    BlockPos pos = data.getPos();
    TileEntity tileEntity = world.getTileEntity(pos);

    if (tileEntity instanceof TileWorkstump) {
      this.probeInfo = probeInfo;
      this.delegate.display((TileWorkstump) tileEntity, player);
      this.probeInfo = null;
    }
  }

  @Override
  public void setRecipeProgress(ItemStack input, ItemStack output, int progress, int maxProgress) {

    this.probeInfo.horizontal()
        .item(input)
        .progress(progress, maxProgress, new ProgressStyle().height(18).width(64).showText(false))
        .item(output);
  }

  @Override
  public void setRecipeOutputName(ItemStack itemStack) {

    this.probeInfo.itemLabel(itemStack);
  }

  @Override
  public void setCondition(String langKey, String textColorString, String conditionLangKey) {

    this.probeInfo.element(new ElementTextLocalized(langKey, textColorString, conditionLangKey));
  }

  @Override
  public void setHoveredItem(ItemStack stackInSlot) {

    if (stackInSlot.getCount() == 1) {
      this.probeInfo.itemLabel(stackInSlot);

    } else {
      this.probeInfo.element(new ElementTextLocalized(WorkstumpProviderDelegate.LANG_KEY_HOVERED_ITEM_QUANTITY, stackInSlot, stackInSlot.getCount()));
    }
  }
}
