package com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.workstump;

import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumpsConfig;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.TileWorkstump;
import com.codetaylor.mc.artisanworktables.api.ArtisanAPI;
import com.codetaylor.mc.athenaeum.interaction.api.Quaternion;
import com.codetaylor.mc.athenaeum.interaction.api.Transform;
import com.codetaylor.mc.athenaeum.interaction.spi.InteractionItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Supplier;

public class InteractionInput
    extends InteractionItemStack<TileWorkstump> {

  private static final double ONE_THIRD = 1.0 / 3.0;
  private static final double ONE_SIXTH = 1.0 / 6.0;
  private final Vec3d textOffset = new Vec3d(0, 0.25, 0);
  private final Supplier<String> tableNameSupplier;

  public InteractionInput(Supplier<String> tableNameSupplier, ItemStackHandler stackHandler, int slot, double x, double z) {

    super(
        new ItemStackHandler[]{stackHandler},
        slot,
        new EnumFacing[]{EnumFacing.UP},
        new AxisAlignedBB(x * ONE_THIRD, 14f / 16f, z * ONE_THIRD, x * ONE_THIRD + ONE_THIRD, 15f / 16f, z * ONE_THIRD + ONE_THIRD),
        new Transform(
            Transform.translate(x * (ONE_THIRD - 0.025) + ONE_SIXTH + 0.025, 15f / 16f + (1f / 32f), z * (ONE_THIRD - 0.025) + ONE_SIXTH + 0.025),
            Transform.rotate(new Quaternion[]{
                Transform.rotate(0, 1, 0, 180),
                Transform.rotate(1, 0, 0, -90)
            }),
            Transform.scale(0.20, 0.20, 0.20)
        )
    );
    this.tableNameSupplier = tableNameSupplier;
  }

  @Override
  public Vec3d getTextOffset(EnumFacing tileFacing, EnumFacing playerHorizontalFacing, EnumFacing sideHit) {

    return this.textOffset;
  }

  @Override
  protected boolean doItemStackValidation(ItemStack itemStack) {

    // Allow the item if it isn't a tool used to bang on the table.
    return !ArtisanAPI.containsRecipeWithTool(itemStack)
        && !ModuleWorkstumpsConfig.WORKSTUMP.isDefaultTool(this.tableNameSupplier.get(), itemStack);
  }

  @Override
  protected int getInsertItemCount(EnumType type, ItemStack itemStack) {

    return 1;
  }
}
