package com.codetaylor.mc.artisanworkstumps.modules.workstumps.block;

import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.TileWorkstump;
import com.codetaylor.mc.artisanworktables.api.internal.reference.EnumType;
import com.codetaylor.mc.artisanworktables.modules.worktables.particle.ParticleWorktableMage;
import com.codetaylor.mc.athenaeum.util.Properties;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockWorkstumpMage
    extends BlockWorkstump {

  public static final PropertyBool ACTIVE = PropertyBool.create("active");

  public BlockWorkstumpMage() {

    super(EnumType.MAGE.getName());
    this.setDefaultState(this.blockState.getBaseState()
        .withProperty(ACTIVE, false));
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {

    return new BlockStateContainer(this, Properties.FACING_HORIZONTAL, ACTIVE);
  }

  @Override
  public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {

    if (this.getActualState(state, world, pos).getValue(ACTIVE)) {
      return 8;
    }

    return super.getLightValue(state, world, pos);
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {

    TileEntity tileEntity = world.getTileEntity(pos);

    if (tileEntity instanceof TileWorkstump
        && ((TileWorkstump) tileEntity).getTableName().equals("mage")) {
      return state.withProperty(ACTIVE, !((TileWorkstump) tileEntity).getInputStackHandler().isEmpty());
    }

    return super.getActualState(state, world, pos);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {

    TileEntity tileEntity = world.getTileEntity(pos);

    if (tileEntity instanceof TileWorkstump) {
      TileWorkstump workstump = (TileWorkstump) tileEntity;

      if (!workstump.getInputStackHandler().isEmpty()) {
        tileEntity.getWorld().spawnParticle(
            EnumParticleTypes.PORTAL,
            tileEntity.getPos().getX() + 0.5 + rand.nextFloat() * 0.5 - 0.25,
            tileEntity.getPos().getY() + 0.5,
            tileEntity.getPos().getZ() + 0.5 + rand.nextFloat() * 0.5 - 0.25,
            0, rand.nextFloat(), 0
        );

        Minecraft.getMinecraft().effectRenderer.addEffect(
            new ParticleWorktableMage(
                tileEntity.getWorld(),
                tileEntity.getPos().getX() + 0.5 + rand.nextFloat() * 0.5 - 0.25,
                tileEntity.getPos().getY() + 1.5,
                tileEntity.getPos().getZ() + 0.5 + rand.nextFloat() * 0.5 - 0.25,
                0, rand.nextFloat(), 0
            )
        );
      }

    }
  }
}
