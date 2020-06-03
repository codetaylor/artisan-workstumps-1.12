package com.codetaylor.mc.artisanworkstumps.modules.tanks.block;

import com.codetaylor.mc.artisanworkstumps.modules.tanks.ModuleTanksConfig;
import com.codetaylor.mc.artisanworkstumps.modules.tanks.tile.TileFluidStump;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.block.BlockWorkstump;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.TileWorkstump;
import com.codetaylor.mc.athenaeum.interaction.spi.IBlockInteractable;
import com.codetaylor.mc.athenaeum.interaction.spi.IInteraction;
import com.codetaylor.mc.athenaeum.spi.BlockPartialBase;
import com.codetaylor.mc.athenaeum.util.AABBHelper;
import com.codetaylor.mc.athenaeum.util.Properties;
import com.codetaylor.mc.athenaeum.util.StackHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class BlockFluidStump
    extends BlockPartialBase
    implements IBlockInteractable {

  public static final String NAME = "fluidstump";

  private static final AxisAlignedBB AABB_NORTH = AABBHelper.create(4, 8, 2, 16, 16, 14);
  private static final AxisAlignedBB AABB_EAST = AABBHelper.create(2, 8, 4, 14, 16, 16);
  private static final AxisAlignedBB AABB_SOUTH = AABBHelper.create(0, 8, 2, 12, 16, 14);
  private static final AxisAlignedBB AABB_WEST = AABBHelper.create(2, 8, 0, 14, 16, 12);

  public BlockFluidStump() {

    super(Material.WOOD);
    this.setHarvestLevel("axe", 0);
    this.setHardness(2);
  }

  // ---------------------------------------------------------------------------
  // - Light
  // ---------------------------------------------------------------------------

  @Override
  public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {

    TileEntity tileEntity = world.getTileEntity(pos);

    if (tileEntity instanceof TileFluidStump) {
      TileFluidStump tile = (TileFluidStump) tileEntity;
      FluidTank fluidTank = tile.getFluidTank();
      FluidStack fluid = fluidTank.getFluid();
      int fluidAmount = fluidTank.getFluidAmount();

      if (fluid != null && fluidAmount > 0) {
        int luminosity = fluid.getFluid().getLuminosity(fluid);
        return MathHelper.clamp(luminosity, 0, 15);
      }
    }

    return super.getLightValue(state, world, pos);
  }

  // ---------------------------------------------------------------------------
  // - Interaction
  // ---------------------------------------------------------------------------

  @Nullable
  @Override
  public RayTraceResult collisionRayTrace(IBlockState blockState, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {

    RayTraceResult result = super.collisionRayTrace(blockState, world, pos, start, end);
    return this.interactionRayTrace(result, blockState, world, pos, start, end);
  }

  @Override
  public boolean onBlockActivated(
      World world,
      BlockPos pos,
      IBlockState state,
      EntityPlayer player,
      EnumHand hand,
      EnumFacing facing,
      float hitX,
      float hitY,
      float hitZ
  ) {

    return this.interact(IInteraction.EnumType.MouseClick, world, pos, state, player, hand, facing, hitX, hitY, hitZ);
  }

  @Override
  public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {

    // Delay the destruction of the TE until after #getDrops is called. We need
    // access to the TE while creating the dropped item in order to serialize it.
    return willHarvest || super.removedByPlayer(state, world, pos, player, false);
  }

  @Override
  public void harvestBlock(@Nonnull World world, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, ItemStack stack) {

    super.harvestBlock(world, player, pos, state, te, stack);

    if (!world.isRemote) {
      world.setBlockToAir(pos);
    }
  }

  @Override
  public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {

    IBlockState blockState = world.getBlockState(pos);

    if (blockState.getBlock() instanceof BlockFluidStump) {
      EnumFacing facing = blockState.getValue(Properties.FACING_HORIZONTAL);

      if (world.isAirBlock(pos.offset(facing))) {

        if (world instanceof WorldServer) {
          ((WorldServer) world).destroyBlock(pos, true);
        }
      }
    }
  }

  // ---------------------------------------------------------------------------
  // - Drops
  // ---------------------------------------------------------------------------

  @Override
  public void getDrops(@Nonnull NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {

    // Serialize the TE into the item dropped.
    // Called before #breakBlock

    if (this.canHoldContentsWhenBroken()) {
      drops.add(StackHelper.createItemStackFromTileEntity(this, 1, 0, world.getTileEntity(pos)));

    } else {
      super.getDrops(drops, world, pos, state, fortune);
    }
  }

  private boolean canHoldContentsWhenBroken() {

    return ModuleTanksConfig.FLUID_STUMP.HOLDS_CONTENTS_WHEN_BROKEN;
  }

  // ---------------------------------------------------------------------------
  // - Tile Entity
  // ---------------------------------------------------------------------------

  @Override
  public boolean hasTileEntity(IBlockState state) {

    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {

    return new TileFluidStump();
  }

  // ---------------------------------------------------------------------------
  // - Placement
  // ---------------------------------------------------------------------------

  @Override
  public boolean canPlaceBlockOnSide(@Nonnull World world, @Nonnull BlockPos pos, EnumFacing side) {

    if (side == EnumFacing.UP || side == EnumFacing.DOWN) {
      return false;
    }

    BlockPos offset = pos.offset(side.getOpposite());
    TileEntity tileEntity = world.getTileEntity(offset);

    if (!(tileEntity instanceof TileWorkstump)) {
      return false;
    }

    if (((TileWorkstump) tileEntity).hasFluidStump()) {
      return false;
    }

    IBlockState blockState = world.getBlockState(offset);

    if (!(blockState.getBlock() instanceof BlockWorkstump)) {
      return false;
    }

    EnumFacing facing = blockState.getValue(Properties.FACING_HORIZONTAL);

    if (facing == side) {
      return false;
    }

    return super.canPlaceBlockOnSide(world, pos, side);
  }

  @ParametersAreNonnullByDefault
  @Nonnull
  @Override
  public IBlockState getStateForPlacement(
      World world,
      BlockPos pos,
      EnumFacing facing,
      float hitX,
      float hitY,
      float hitZ,
      int meta,
      EntityLivingBase placer,
      EnumHand hand
  ) {

    EnumFacing opposite = facing.getOpposite();
    return this.getDefaultState().withProperty(Properties.FACING_HORIZONTAL, opposite);
  }

  // ---------------------------------------------------------------------------
  // - Rendering
  // ---------------------------------------------------------------------------

  @Nonnull
  @Override
  public BlockRenderLayer getBlockLayer() {

    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public boolean isSideSolid(IBlockState base_state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {

    return false;
  }

  // ---------------------------------------------------------------------------
  // - Collision
  // ---------------------------------------------------------------------------

  @Nonnull
  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

    EnumFacing facing = state.getValue(Properties.FACING_HORIZONTAL);

    switch (facing) {
      // These are rotated because the block model is rotated in the blockstate file.
      case NORTH:
        return AABB_WEST;
      case EAST:
        return AABB_NORTH;
      case SOUTH:
        return AABB_EAST;
      case WEST:
        return AABB_SOUTH;
    }

    return super.getBoundingBox(state, source, pos);
  }

  // ---------------------------------------------------------------------------
  // - Tooltip
  // ---------------------------------------------------------------------------

  @Override
  public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {

    NBTTagCompound stackTag = stack.getTagCompound();

    if (stackTag == null) {
      this.addInformationCapacity(tooltip);

    } else {

      if (stackTag.hasKey(StackHelper.BLOCK_ENTITY_TAG)) {
        NBTTagCompound tileTag = stackTag.getCompoundTag(StackHelper.BLOCK_ENTITY_TAG);

        if (tileTag.hasKey("tank")) {
          NBTTagCompound tankTag = tileTag.getCompoundTag("tank");

          if (tankTag.hasKey("Empty")
              && (!tankTag.hasKey("Amount")
              || tankTag.getInteger("Amount") <= 0)) {

            this.addInformationCapacity(tooltip);

          } else {
            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(tankTag);

            if (fluidStack != null) {
              String localizedName = fluidStack.getLocalizedName();
              int amount = fluidStack.amount;
              int capacity = ModuleTanksConfig.FLUID_STUMP.CAPACITY;
              tooltip.add(I18n.translateToLocalFormatted("gui.pyrotech.tooltip.fluid", localizedName, amount, capacity));
            }
          }
        }
      }
    }

    boolean hotFluids = ModuleTanksConfig.FLUID_STUMP.HOLDS_HOT_FLUIDS;
    tooltip.add((hotFluids ? TextFormatting.GREEN : TextFormatting.RED) + I18n.translateToLocalFormatted("gui.pyrotech.tooltip.hot.fluids." + hotFluids));

    boolean holdsContents = ModuleTanksConfig.FLUID_STUMP.HOLDS_CONTENTS_WHEN_BROKEN;
    tooltip.add((holdsContents ? TextFormatting.GREEN : TextFormatting.RED) + I18n.translateToLocalFormatted("gui.pyrotech.tooltip.contents.retain." + holdsContents));
  }

  private void addInformationCapacity(@Nonnull List<String> tooltip) {

    int capacity = ModuleTanksConfig.FLUID_STUMP.CAPACITY;
    tooltip.add(I18n.translateToLocalFormatted("gui.pyrotech.tooltip.fluid.capacity", capacity));
  }

  // ---------------------------------------------------------------------------
  // - Variants
  // ---------------------------------------------------------------------------

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {

    return new BlockStateContainer(this, Properties.FACING_HORIZONTAL);
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {

    return this.getDefaultState()
        .withProperty(Properties.FACING_HORIZONTAL, EnumFacing.HORIZONTALS[meta]);
  }

  @Override
  public int getMetaFromState(IBlockState state) {

    return state.getValue(Properties.FACING_HORIZONTAL).getIndex() - 2;
  }
}
