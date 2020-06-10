package com.codetaylor.mc.artisanworkstumps.modules.workstumps.block;

import com.codetaylor.mc.artisanworkstumps.modules.tanks.ModuleTanks;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.TileWorkstump;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.workstump.EnumDamagedSide;
import com.codetaylor.mc.athenaeum.interaction.spi.IBlockInteractable;
import com.codetaylor.mc.athenaeum.interaction.spi.IInteraction;
import com.codetaylor.mc.athenaeum.spi.BlockPartialBase;
import com.codetaylor.mc.athenaeum.util.Properties;
import com.codetaylor.mc.athenaeum.util.StackHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockWorkstump
    extends BlockPartialBase
    implements IBlockInteractable {

  public static final String NAME_TAILOR = "workstump_tailor";
  public static final String NAME_CARPENTER = "workstump_carpenter";
  public static final String NAME_MASON = "workstump_mason";
  public static final String NAME_BLACKSMITH = "workstump_blacksmith";
  public static final String NAME_JEWELER = "workstump_jeweler";
  public static final String NAME_BASIC = "workstump_basic";
  public static final String NAME_ENGINEER = "workstump_engineer";
  public static final String NAME_MAGE = "workstump_mage";
  public static final String NAME_SCRIBE = "workstump_scribe";
  public static final String NAME_CHEMIST = "workstump_chemist";
  public static final String NAME_FARMER = "workstump_farmer";
  public static final String NAME_CHEF = "workstump_chef";
  public static final String NAME_DESIGNER = "workstump_designer";
  public static final String NAME_TANNER = "workstump_tanner";
  public static final String NAME_POTTER = "workstump_potter";

  public static final PropertyInteger CONDITION = PropertyInteger.create("condition", 0, 4);
  public static final PropertyBool DAMAGED_EAST = PropertyBool.create("damaged_east");
  public static final PropertyBool DAMAGED_WEST = PropertyBool.create("damaged_west");
  public static final PropertyBool DAMAGED_SOUTH = PropertyBool.create("damaged_south");

  private final String tableName;

  public BlockWorkstump(String tableName) {

    super(Material.WOOD);
    this.setHardness(2);
    this.setResistance(5);
    this.tableName = tableName;
    this.setDefaultState(this.blockState.getBaseState()
        .withProperty(DAMAGED_EAST, false)
        .withProperty(DAMAGED_WEST, false)
        .withProperty(DAMAGED_SOUTH, false));
  }

  // ---------------------------------------------------------------------------
  // - Interaction
  // ---------------------------------------------------------------------------

  @Nullable
  @Override
  public String getHarvestTool(@Nonnull IBlockState state) {

    return "axe";
  }

  @SuppressWarnings("deprecation")
  @Nullable
  @Override
  public RayTraceResult collisionRayTrace(IBlockState blockState, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {

    return this.interactionRayTrace(super.collisionRayTrace(blockState, world, pos, start, end), blockState, world, pos, start, end);
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

    if (facing != EnumFacing.UP && player.getHeldItem(hand).getItem() == Item.getItemFromBlock(ModuleTanks.Blocks.LOG_BASIN)) {
      return false;
    }

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

    if (!world.isRemote) {
      TileEntity tileEntity = world.getTileEntity(pos);

      if (tileEntity instanceof TileWorkstump) {
        ((TileWorkstump) tileEntity).dropContents();
      }
    }

    super.harvestBlock(world, player, pos, state, te, stack);

    if (!world.isRemote) {
      world.setBlockToAir(pos);
    }
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

    EnumFacing opposite = placer.getHorizontalFacing().getOpposite();
    return this.getDefaultState().withProperty(Properties.FACING_HORIZONTAL, opposite);
  }

  // ---------------------------------------------------------------------------
  // - Drops
  // ---------------------------------------------------------------------------

  @Override
  public void getDrops(@Nonnull NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {

    // Serialize the TE into the item dropped.
    // Called before #breakBlock

    drops.add(StackHelper.createItemStackFromTileEntity(
        this,
        1,
        0,
        world.getTileEntity(pos)
    ));
  }

  // ---------------------------------------------------------------------------
  // - Tile
  // ---------------------------------------------------------------------------

  @Override
  public boolean hasTileEntity(IBlockState state) {

    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {

    return new TileWorkstump(this.tableName);
  }

  // ---------------------------------------------------------------------------
  // - Variants
  // ---------------------------------------------------------------------------

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {

    return new BlockStateContainer(this, Properties.FACING_HORIZONTAL, CONDITION, DAMAGED_EAST, DAMAGED_WEST, DAMAGED_SOUTH);
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

  // ---------------------------------------------------------------------------
  // - Rendering
  // ---------------------------------------------------------------------------

  @Override
  public boolean isSideSolid(IBlockState base_state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {

    return (side == EnumFacing.DOWN);
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {

    TileEntity tileEntity = world.getTileEntity(pos);

    if (tileEntity instanceof TileWorkstump) {
      TileWorkstump tileWorkstump = (TileWorkstump) tileEntity;
      int durability = tileWorkstump.getDurability();
      int remainingDurability = tileWorkstump.getRemainingDurability();
      float durabilityPercentage = remainingDurability / (float) durability;

      if (durabilityPercentage < 0.05) {
        state = state.withProperty(CONDITION, 4);

      } else if (durabilityPercentage < 0.25) {
        state = state.withProperty(CONDITION, 3);

      } else if (durabilityPercentage < 0.5) {
        state = state.withProperty(CONDITION, 2);

      } else if (durabilityPercentage < 0.75) {
        state = state.withProperty(CONDITION, 1);

      } else {
        state = state.withProperty(CONDITION, 0);
      }

      state = state.withProperty(DAMAGED_EAST, tileWorkstump.isSideDamaged(EnumDamagedSide.East));
      state = state.withProperty(DAMAGED_WEST, tileWorkstump.isSideDamaged(EnumDamagedSide.West));
      state = state.withProperty(DAMAGED_SOUTH, tileWorkstump.isSideDamaged(EnumDamagedSide.South));
    }

    return state;
  }

}
