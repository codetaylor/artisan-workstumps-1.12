package com.codetaylor.mc.artisanworkstumps.modules.workstumps.util;

import com.codetaylor.mc.artisanworkstumps.ModArtisanWorkstumps;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentTranslation;

public class DebugHelper {

  public static void debug(String message, PlayerList playerList) {

    if (ModArtisanWorkstumps.DEBUG) {
      ModArtisanWorkstumps.LOGGER.debug(message);

      for (EntityPlayerMP player : playerList.getPlayers()) {
        player.sendMessage(new TextComponentTranslation(message));
      }
    }
  }
}
