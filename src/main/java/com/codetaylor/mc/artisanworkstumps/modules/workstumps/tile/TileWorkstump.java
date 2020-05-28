package com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile;

import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumps;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumpsConfig;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.block.BlockWorkstump;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.workstump.*;
import com.codetaylor.mc.artisanworktables.api.ArtisanAPI;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.ISecondaryIngredientMatcher;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.RecipeRegistry;
import com.codetaylor.mc.artisanworktables.api.internal.reference.EnumTier;
import com.codetaylor.mc.artisanworktables.api.recipe.IArtisanRecipe;
import com.codetaylor.mc.athenaeum.integration.gamestages.Stages;
import com.codetaylor.mc.athenaeum.interaction.spi.IInteraction;
import com.codetaylor.mc.athenaeum.interaction.spi.ITileInteractable;
import com.codetaylor.mc.athenaeum.network.tile.data.TileDataFloat;
import com.codetaylor.mc.athenaeum.network.tile.data.TileDataInteger;
import com.codetaylor.mc.athenaeum.network.tile.data.TileDataItemStackHandler;
import com.codetaylor.mc.athenaeum.network.tile.spi.ITileData;
import com.codetaylor.mc.athenaeum.network.tile.spi.TileEntityDataBase;
import com.codetaylor.mc.athenaeum.util.EnchantmentHelper;
import com.codetaylor.mc.athenaeum.util.Properties;
import com.codetaylor.mc.athenaeum.util.StackHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileWorkstump
    extends TileEntityDataBase
    implements ITileInteractable {

  private TileDataItemStackHandler<InputStackHandler> inputTileDataItemStackHandler;
  private InputStackHandler inputStackHandler;
  private ShelfStackHandler shelfStackHandler;
  private TileDataInteger remainingDurability;

  private TileDataFloat recipeProgress;

  private IInteraction[] interactions;

  private String tableName;
  private String retainedRecipeName;

  public TileWorkstump() {

    super(ModuleWorkstumps.TILE_DATA_SERVICE);

    // --- Initialize ---

    this.inputStackHandler = new InputStackHandler(3, 3, this.getGridMaxStackSize());
    this.inputStackHandler.addObserver((handler, slot) -> {
      this.recipeProgress.set(0);
      this.markDirty();
    });

    this.shelfStackHandler = new ShelfStackHandler(this.getShelfMaxStackSize());
    this.shelfStackHandler.addObserver((handler, slot) -> this.markDirty());

    this.recipeProgress = new TileDataFloat(0);

    this.remainingDurability = new TileDataInteger(this.getDurability());

    // --- Network ---

    this.inputTileDataItemStackHandler = new TileDataItemStackHandler<>(this.inputStackHandler);

    this.registerTileDataForNetwork(new ITileData[]{
        this.inputTileDataItemStackHandler,
        new TileDataItemStackHandler<>(this.shelfStackHandler),
        this.recipeProgress,
        this.remainingDurability
    });

    // --- Interactions ---

    this.interactions = new IInteraction[12];

    List<IInteraction> interactionList = new ArrayList<>();

    interactionList.add(new InteractionTool());

    for (int i = 0; i < 9; i++) {
      int x = 2 - (i % 3);
      int z = 2 - (i / 3);
      interactionList.add(new InputInteraction(this.inputStackHandler, i, x, z));
    }

    for (int i = 0; i < 3; i++) {
      int x = i % 3;
      interactionList.add(new ShelfInteraction(this.shelfStackHandler, i, x));
    }

    this.interactions = interactionList.toArray(new IInteraction[0]);
  }

  public TileWorkstump(String tableName) {

    this();
    this.tableName = tableName;
  }

  // ---------------------------------------------------------------------------
  // - Accessors
  // ---------------------------------------------------------------------------

  @Nullable
  public IFluidHandler getTank() {

    // Stub until we implement tanks.
    return null;
  }

  public String getTableName() {

    return this.tableName;
  }

  public IArtisanRecipe getRetainedRecipe() {

    if (this.retainedRecipeName == null) {
      return null;
    }

    IArtisanRecipe recipe = ArtisanAPI.getRecipe(this.tableName + ":" + this.retainedRecipeName);

    if (recipe == null) {
      this.retainedRecipeName = null;
    }

    return recipe;
  }

  public void setRetainedRecipeName(String recipeName) {

    this.retainedRecipeName = recipeName;
  }

  public float getRecipeProgress() {

    return this.recipeProgress.get();
  }

  public void setRecipeProgress(float recipeProgress) {

    this.recipeProgress.set(recipeProgress);
  }

  public void addRecipeProgress(float recipeProgress) {

    this.recipeProgress.add(recipeProgress);
  }

  public int getRemainingDurability() {

    return this.remainingDurability.get();
  }

  public int addRemainingDurability(int remainingDurability) {

    return this.remainingDurability.add(remainingDurability);
  }

  public InputStackHandler getInputStackHandler() {

    return this.inputStackHandler;
  }

  public IArtisanRecipe getWorkstumpRecipe(EntityPlayer player) {

    int playerExperience = EnchantmentHelper.getPlayerExperienceTotal(player);
    int playerLevels = player.experienceLevel;
    boolean isPlayerCreative = player.isCreative();

    RecipeRegistry registry = ArtisanAPI.getWorktableRecipeRegistry(this.tableName);

    return registry.findRecipe(
        playerExperience,
        playerLevels,
        isPlayerCreative,
        new ItemStack[]{player.getHeldItemMainhand()},
        this.inputStackHandler,
        null,
        ISecondaryIngredientMatcher.FALSE,
        EnumTier.WORKTABLE,
        Collections.emptyMap()
    );
  }

  private int getGridMaxStackSize() {

    return ModuleWorkstumpsConfig.WORKSTUMP.GRID_MAX_STACK_SIZE;
  }

  private int getShelfMaxStackSize() {

    return ModuleWorkstumpsConfig.WORKSTUMP.SHELF_MAX_STACK_SIZE;
  }

  public boolean usesDurability() {

    return ModuleWorkstumpsConfig.WORKSTUMP.USES_DURABILITY;
  }

  private int getDurability() {

    return ModuleWorkstumpsConfig.WORKSTUMP.DURABILITY;
  }

  public int getHitsPerCraft() {

    return ModuleWorkstumpsConfig.WORKSTUMP.HITS_PER_CRAFT;
  }

  public int getMinimumHungerToUse() {

    return ModuleWorkstumpsConfig.WORKSTUMP.MINIMUM_HUNGER_TO_USE;
  }

  public double getExhaustionCostPerHit() {

    return ModuleWorkstumpsConfig.WORKSTUMP.EXHAUSTION_COST_PER_HIT;
  }

  public double getExhaustionCostPerCraftComplete() {

    return ModuleWorkstumpsConfig.WORKSTUMP.EXHAUSTION_COST_PER_CRAFT_COMPLETE;
  }

  // ---------------------------------------------------------------------------
  // - Container
  // ---------------------------------------------------------------------------

  public void dropContents() {

    StackHelper.spawnStackHandlerContentsOnTop(this.world, this.inputStackHandler, this.pos);
    StackHelper.spawnStackHandlerContentsOnTop(this.world, this.shelfStackHandler, this.pos);
  }

  // ---------------------------------------------------------------------------
  // - Serialization
  // ---------------------------------------------------------------------------

  @Override
  protected void setWorldCreate(World world) {

    this.world = world;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {

    super.readFromNBT(compound);

    this.tableName = compound.getString("tableName");
    this.inputStackHandler.deserializeNBT(compound.getCompoundTag("inputStackHandler"));
    this.shelfStackHandler.deserializeNBT(compound.getCompoundTag("shelfStackHandler"));
    this.remainingDurability.set(compound.getInteger("remainingDurability"));

    if (compound.hasKey("retainedRecipe")) {
      this.retainedRecipeName = compound.getString("retainedRecipe");
    }
  }

  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {

    super.writeToNBT(compound);

    compound.setString("tableName", this.tableName);
    compound.setTag("inputStackHandler", this.inputStackHandler.serializeNBT());
    compound.setTag("shelfStackHandler", this.shelfStackHandler.serializeNBT());
    compound.setInteger("remainingDurability", this.remainingDurability.get());

    if (this.retainedRecipeName != null) {
      compound.setString("retainedRecipe", this.retainedRecipeName);
    }

    return compound;
  }

  // ---------------------------------------------------------------------------
  // - Interactions
  // ---------------------------------------------------------------------------

  @Nullable
  @Override
  public Stages getStages() {

    return ModuleWorkstumpsConfig.STAGES_WORKSTUMP;
  }

  @Override
  public boolean shouldRenderInPass(int pass) {

    return (pass == 0) || (pass == 1);
  }

  @Override
  public IInteraction[] getInteractions() {

    return this.interactions;
  }

  @Override
  public EnumFacing getTileFacing(World world, BlockPos pos, IBlockState blockState) {

    if (blockState.getBlock() instanceof BlockWorkstump) {
      return blockState.getValue(Properties.FACING_HORIZONTAL);
    }

    return ITileInteractable.super.getTileFacing(world, pos, blockState);
  }

  public int getBlockStateIdForParticles() {

    IBlockState state = Blocks.PLANKS.getDefaultState()
        .withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK);
    return Block.getStateId(state);
  }

}