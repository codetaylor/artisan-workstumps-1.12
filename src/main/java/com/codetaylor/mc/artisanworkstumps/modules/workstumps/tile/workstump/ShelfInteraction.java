package com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.workstump;

import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.TileWorkstump;
import com.codetaylor.mc.athenaeum.interaction.api.Quaternion;
import com.codetaylor.mc.athenaeum.interaction.api.Transform;
import com.codetaylor.mc.athenaeum.interaction.spi.InteractionItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.items.ItemStackHandler;

public class ShelfInteraction
    extends InteractionItemStack<TileWorkstump> {

  private static final double ONE_THIRD = 1.0 / 3.0;
  private static final double ONE_SIXTH = 1.0 / 6.0;

  public ShelfInteraction(ItemStackHandler stackHandler, int slot, double x) {

    super(
        new ItemStackHandler[]{stackHandler},
        slot,
        new EnumFacing[]{EnumFacing.UP},
        new AxisAlignedBB(x * ONE_THIRD, 0, 0, x * ONE_THIRD + ONE_THIRD, 5f / 16f, ONE_THIRD),
        new Transform(
            Transform.translate(x * (ONE_THIRD - 0.025) + ONE_SIXTH + 0.025, 5f / 16f, ONE_SIXTH + 0.025),
            Transform.rotate(new Quaternion[]{
                Transform.rotate(0, 1, 0, 180),
                Transform.rotate(1, 0, 0, -90)
            }),
            Transform.scale(0.20, 0.20, 0.20)
        )
    );
  }
}
