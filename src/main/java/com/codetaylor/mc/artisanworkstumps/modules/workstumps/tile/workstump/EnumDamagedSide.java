package com.codetaylor.mc.artisanworkstumps.modules.workstumps.tile.workstump;

import java.util.Random;

public enum EnumDamagedSide {
  // Do not reorder, ordinal used in serialization
  // None must be at ordinal 0
  None, East, West, South;

  public static EnumDamagedSide getRandomSide(Random random, EnumDamagedSide[] from) {

    return from[random.nextInt(from.length)];
  }
}
