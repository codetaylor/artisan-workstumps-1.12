package com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.workstump;

import com.codetaylor.mc.artisanworkstumps.modules.core.network.SCPacketParticleProgress;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumps;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.ModuleWorkstumpsConfig;
import com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.TileWorkstump;
import com.codetaylor.mc.artisanworktables.api.ArtisanToolHandlers;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.IArtisanIngredient;
import com.codetaylor.mc.artisanworktables.api.recipe.IArtisanRecipe;
import com.codetaylor.mc.artisanworktables.api.recipe.IToolHandler;
import com.codetaylor.mc.athenaeum.interaction.api.InteractionBounds;
import com.codetaylor.mc.athenaeum.interaction.spi.InteractionUseItemBase;
import com.codetaylor.mc.athenaeum.util.OreDictHelper;
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

public class InteractionRepair
    extends InteractionUseItemBase<TileWorkstump> {

  public InteractionRepair() {

    super(EnumFacing.values(), InteractionBounds.BLOCK);
  }

  @Override
  protected boolean allowInteraction(TileWorkstump tile, World world, BlockPos hitPos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing hitSide, float hitX, float hitY, float hitZ) {

    if (hand != EnumHand.MAIN_HAND) {
      return false;
    }

    ItemStack heldItemMainhand = player.getHeldItemMainhand();
    ItemStack heldItemOffhand = player.getHeldItemOffhand();

    return OreDictHelper.contains("plankWood", heldItemOffhand)
        && OreDictHelper.contains("artisansFramingHammer", heldItemMainhand);
  }

  @Override
  protected void applyItemDamage(ItemStack itemStack, EntityPlayer player) {

    // We apply our own item damage.
  }

  @Override
  protected boolean doInteraction(TileWorkstump tile, World world, BlockPos hitPos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing hitSide, float hitX, float hitY, float hitZ) {

    ItemStack heldItem = player.getHeldItemMainhand();

    if (player.isSneaking()) {

      if (heldItem.isEmpty()) {

      } else if (ModuleWorkstumpsConfig.WORKSTUMP.ALLOW_RECIPE_REPEAT) {
        this.doRecipeRepeat(tile, player, heldItem);
      }

    } else {
      this.doRepair(tile, world, hitPos, player, hitX, hitY, hitZ);
    }

    return true;
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

  private void doRepair(TileWorkstump tile, World world, BlockPos hitPos, EntityPlayer player, float hitX, float hitY, float hitZ) {

    if (!world.isRemote) {
      this.doRepairServer(tile, world, hitPos, player);

    } else {
      this.doRepairClient(tile, world, hitX, hitY, hitZ);
    }
  }

  private void doRepairServer(TileWorkstump tile, World world, BlockPos hitPos, EntityPlayer player) {

    world.playSound(null, hitPos, SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS, 1, 1);

    ModuleWorkstumps.PACKET_SERVICE.sendToAllAround(
        new SCPacketParticleProgress(hitPos.getX() + 0.5, hitPos.getY() + 1, hitPos.getZ() + 0.5, 2),
        world.provider.getDimension(),
        hitPos
    );

    // damage the tool
    IToolHandler toolHandler = ArtisanToolHandlers.get(player.getHeldItemMainhand());
    toolHandler.applyDamage(world, player.getHeldItemMainhand(), ModuleWorkstumpsConfig.WORKSTUMP.DEFAULT_RECIPE_TOOL_DAMAGE, player, false);
    player.getHeldItemOffhand().shrink(1);

    // repair the workstump
    tile.addRemainingDurability(1);
  }

  private void doRepairClient(TileWorkstump tile, World world, float hitX, float hitY, float hitZ) {

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
