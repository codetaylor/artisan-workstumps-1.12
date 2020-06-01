package com.codetaylor.mc.artisanworkstumps.modules.workstumps.event;

import com.codetaylor.mc.artisanworkstumps.modules.workstumps.block.BlockWorkstump;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.TileWorkstump;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class RecipeRepeat {

  public static class RightClickBlockEventHandler {

    @SubscribeEvent
    public void on(PlayerInteractEvent.RightClickBlock event) {

      // This handler is only registered if the ALLOW_RECIPE_REPEAT config value
      // is set to true.

      World world = event.getWorld();
      BlockPos pos = event.getPos();
      IBlockState blockState = world.getBlockState(pos);
      Block block = blockState.getBlock();

      if (block instanceof BlockWorkstump) {

        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof TileWorkstump) {
          event.setUseBlock(Event.Result.ALLOW);
          event.setUseItem(Event.Result.DENY);
        }
      }
    }
  }

  private RecipeRepeat() {
    //
  }
}
