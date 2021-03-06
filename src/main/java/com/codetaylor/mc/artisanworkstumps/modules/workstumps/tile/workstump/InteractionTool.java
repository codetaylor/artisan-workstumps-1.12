package com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.workstump;

import com.codetaylor.mc.artisanworkstumps.modules.core.network.SCPacketParticleProgress;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumps;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumpsConfig;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.TileWorkstump;
import com.codetaylor.mc.artisanworktables.api.ArtisanAPI;
import com.codetaylor.mc.artisanworktables.api.ArtisanToolHandlers;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.IArtisanIngredient;
import com.codetaylor.mc.artisanworktables.api.recipe.IArtisanRecipe;
import com.codetaylor.mc.artisanworktables.api.recipe.IToolHandler;
import com.codetaylor.mc.athenaeum.interaction.api.InteractionBounds;
import com.codetaylor.mc.athenaeum.interaction.spi.InteractionUseItemBase;
import com.codetaylor.mc.athenaeum.util.RandomHelper;
import com.codetaylor.mc.athenaeum.util.StackHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class InteractionTool
    extends InteractionUseItemBase<TileWorkstump> {

  public InteractionTool() {

    super(new EnumFacing[]{EnumFacing.UP}, InteractionBounds.BLOCK);
  }

  @Override
  protected boolean allowInteraction(TileWorkstump tile, World world, BlockPos hitPos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing hitSide, float hitX, float hitY, float hitZ) {

    // Ensure the player has enough food haunches.
    if (player.getFoodStats().getFoodLevel() < tile.getMinimumHungerToUse()) {
      return false;
    }

    ItemStack heldItemStack = player.getHeldItem(hand);
    boolean sneaking = player.isSneaking();

    // Allow an empty hand if clearing.
    if (sneaking
        && heldItemStack.isEmpty()
        && ModuleWorkstumpsConfig.WORKSTUMP.ALLOW_RECIPE_CLEAR) {
      return true;
    }

    if (heldItemStack.isEmpty()) {
      return false;
    }

    if (sneaking) {
      // Player is sneaking, allow only tools for recipe repeat.
      return ArtisanAPI.containsRecipeWithTool(heldItemStack)
          || ModuleWorkstumpsConfig.WORKSTUMP.isDefaultTool(tile.getTableName(), heldItemStack);

    } else {
      IArtisanRecipe recipe = tile.getWorkstumpRecipe(player);

      if (recipe != null) {

        if (recipe.getBaseOutput(tile.createCraftingContext(player)).isEmpty()) {
          return false;
        }

        if (recipe.getToolCount() == 0) {
          return ModuleWorkstumpsConfig.WORKSTUMP.isDefaultTool(tile.getTableName(), heldItemStack)
              && ArtisanToolHandlers.get(heldItemStack).canAcceptAllDamage(heldItemStack, ModuleWorkstumpsConfig.WORKSTUMP.DEFAULT_RECIPE_TOOL_DAMAGE);

        } else {
          // We've already matched the player's held tool with the recipe and
          // if a recipe was returned earlier, we know it matches.
          return true;
        }
      }

      return false;
    }
  }

  @Override
  protected void applyItemDamage(ItemStack itemStack, EntityPlayer player) {

    // We apply our own item damage on recipe completion.
  }

  /**
   * Determine the intent and perform one of three actions:
   * - clear the grid
   * - repeat the last recipe
   * - increment recipe progress
   *
   * @return true
   */
  @Override
  protected boolean doInteraction(TileWorkstump tile, World world, BlockPos hitPos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing hitSide, float hitX, float hitY, float hitZ) {

    ItemStack heldItem = player.getHeldItemMainhand();

    if (player.isSneaking()) {

      if (heldItem.isEmpty()) {
        this.doRecipeClear(tile, world, player);

      } else if (ModuleWorkstumpsConfig.WORKSTUMP.ALLOW_RECIPE_REPEAT) {
        this.doRecipeRepeat(tile, player, heldItem);
      }

    } else {
      this.doRecipeProgress(tile, world, hitPos, player, hitX, hitY, hitZ);
    }

    return true;
  }

  private void doRecipeClear(TileWorkstump tile, World world, EntityPlayer player) {

    int slots = tile.getStackHandlerInput().getSlots();

    for (int i = 0; i < slots; i++) {
      int slotLimit = tile.getStackHandlerInput().getSlotLimit(i);
      ItemStack itemStack = tile.getStackHandlerInput().extractItem(i, slotLimit, false);
      StackHelper.addToInventoryOrSpawn(world, player, itemStack, tile.getPos(), 1, false, true);
    }
  }

  private void doRecipeRepeat(TileWorkstump tile, EntityPlayer player, ItemStack heldItem) {

    IArtisanRecipe existingRecipe = tile.getWorkstumpRecipe(player);
    IArtisanRecipe recipe;

    if (existingRecipe != null) {
      recipe = existingRecipe;

    } else {

      IArtisanRecipe retainedRecipe = tile.getRetainedRecipe();

      if (retainedRecipe == null) {
        return;
      }

      recipe = retainedRecipe;
    }

    List<IArtisanIngredient> ingredientList = recipe.getIngredientList();
    ItemStackHandler inputStackHandler = tile.getStackHandlerInput();
    List<ItemStack> itemStackList = new ArrayList<>(ingredientList.size());

    // Gather ingredients from the player's inventory and hotbar.

    for (IArtisanIngredient ingredient : ingredientList) {

      if (ingredient.matches(ItemStack.EMPTY)) {
        itemStackList.add(ItemStack.EMPTY);

      } else {

        for (ItemStack itemStack : player.inventory.mainInventory) {

          if (ingredient.matches(itemStack)) {
            ItemStack copy = itemStack.copy();
            copy.setCount(1);
            itemStackList.add(copy);
            itemStack.shrink(1);
            break;
          }
        }
      }
    }

    // If the player doesn't have all the items, return gathered items to the
    // player and abort.

    if (ingredientList.size() != itemStackList.size()) {

      for (ItemStack itemStack : itemStackList) {
        player.addItemStackToInventory(itemStack);
      }

      return;
    }

    // Check if the table can take another recipe's worth of inputs.

    boolean tableHasRoom = true;

    for (int i = 0; i < itemStackList.size(); i++) {
      ItemStack remainingItemStack = inputStackHandler.insertItem(i, itemStackList.get(i), true);

      if (!remainingItemStack.isEmpty()) {
        tableHasRoom = false;
        break;
      }
    }

    // If the table doesn't have room, return gathered items to the player
    // and abort.

    if (!tableHasRoom) {

      for (ItemStack itemStack : itemStackList) {
        player.addItemStackToInventory(itemStack);
      }

      return;
    }

    // Finally, insert the gathered items.

    for (int i = 0; i < itemStackList.size(); i++) {
      inputStackHandler.insertItem(i, itemStackList.get(i), false);
    }

    // Damage the held item.

    int toolDamage = ModuleWorkstumpsConfig.WORKSTUMP.RECIPE_REPEAT_TOOL_DAMAGE;

    if (!tile.getWorld().isRemote && toolDamage > 0) {
      ArtisanToolHandlers.get(heldItem).applyDamage(tile.getWorld(), heldItem, toolDamage, player, false);
    }
  }

  private void doRecipeProgress(TileWorkstump tile, World world, BlockPos hitPos, EntityPlayer player, float hitX, float hitY, float hitZ) {

    IArtisanRecipe recipe = tile.getWorkstumpRecipe(player);

    if (!world.isRemote) {
      this.doRecipeProgressServer(tile, world, hitPos, player, recipe);

    } else {
      this.doRecipeProgressClient(tile, world, hitX, hitY, hitZ);
    }
  }

  private void doRecipeProgressServer(TileWorkstump tile, World world, BlockPos hitPos, EntityPlayer player, IArtisanRecipe recipe) {

    world.playSound(null, hitPos, SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS, 1, 1);

    if (recipe != null) {
      tile.addRecipeProgress(1f / tile.getHitsPerCraft(player.getHeldItemMainhand()));

      if (tile.getExhaustionCostPerHit() > 0) {
        player.addExhaustion((float) tile.getExhaustionCostPerHit());
      }

      ModuleWorkstumps.PACKET_SERVICE.sendToAllAround(
          new SCPacketParticleProgress(hitPos.getX() + 0.5, hitPos.getY() + 1, hitPos.getZ() + 0.5, 2),
          world.provider.getDimension(),
          hitPos
      );

      if (tile.getRecipeProgress() >= 0.9999) {
        tile.setRecipeProgress(0);

        tile.setRetainedRecipeName(recipe.getName());

        List<ItemStack> output = new ArrayList<>();
        recipe.doCraft(tile.createCraftingContext(player), output);

        if (recipe.getToolCount() == 0) {
          // no damage was done to the tool, we need to damage it here
          IToolHandler toolHandler = ArtisanToolHandlers.get(player.getHeldItemMainhand());
          toolHandler.applyDamage(world, player.getHeldItemMainhand(), ModuleWorkstumpsConfig.WORKSTUMP.DEFAULT_RECIPE_TOOL_DAMAGE, player, false);
        }

        for (ItemStack result : output) {
          StackHelper.spawnStackOnTop(world, result, tile.getPos(), 0.75);
        }

        // Check durability and break
        if (tile.usesDurability()
            && tile.addRemainingDurability(-1) == 0) {

          tile.dropContents();
          world.destroyBlock(tile.getPos(), false);
          world.playSound(
              null,
              tile.getPos(),
              SoundEvents.ENTITY_ITEM_BREAK,
              SoundCategory.BLOCKS,
              1.0F,
              RandomHelper.random().nextFloat() * 0.4F + 0.8F
          );
        }

        if (tile.getExhaustionCostPerCraftComplete() > 0) {
          player.addExhaustion((float) tile.getExhaustionCostPerCraftComplete());
        }
      }
    }
  }

  private void doRecipeProgressClient(TileWorkstump tile, World world, float hitX, float hitY, float hitZ) {

    int stateId = tile.getBlockStateIdForParticles();

    for (int i = 0; i < 2; ++i) {
      world.spawnParticle(
          EnumParticleTypes.BLOCK_CRACK,
          tile.getPos().getX() + hitX + (tile.getWorld().rand.nextFloat() * 2 - 1) * 0.1,
          tile.getPos().getY() + hitY + 0.1,
          tile.getPos().getZ() + hitZ + (tile.getWorld().rand.nextFloat() * 2 - 1) * 0.1,
          0.0D,
          0.0D,
          0.0D,
          stateId
      );
    }
  }
}
