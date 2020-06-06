package com.codetaylor.mc.artisanworkstumps.modules.workstumps.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public final class ToolHarvestLevelHelper {

  private static final MethodHandle itemTool$toolMaterial_Getter;

  static {

    try {
      itemTool$toolMaterial_Getter = MethodHandles.lookup().unreflectGetter(
          /*
          MC 1.12: net/minecraft/item/ItemTool.toolMaterial
          Name: d => field_77862_b => toolMaterial
          Comment: The material this tool is made from.
          Side: BOTH
          AT: public net.minecraft.item.ItemTool field_77862_b # toolMaterial
           */
          ObfuscationReflectionHelper.findField(ItemTool.class, "field_77862_b")
      );

    } catch (IllegalAccessException e) {
      throw new RuntimeException(String.format("Error unreflecting field %s", "field_77862_b"), e);
    }
  }

  public static int getHarvestLevel(ItemStack itemStack) {

    Item item = itemStack.getItem();

    if (item instanceof ItemTool) {

      try {
        Item.ToolMaterial toolMaterial = (Item.ToolMaterial) itemTool$toolMaterial_Getter.invokeExact((ItemTool) item);
        return toolMaterial.getHarvestLevel();

      } catch (Throwable t) {
        throw new RuntimeException(String.format("Error accessing unreflected field %s", "field_77862_b"), t);
      }
    }

    return -1;
  }

  private ToolHarvestLevelHelper() {
    //
  }
}
