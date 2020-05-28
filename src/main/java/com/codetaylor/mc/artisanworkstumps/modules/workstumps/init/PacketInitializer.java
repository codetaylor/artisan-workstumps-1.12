package com.codetaylor.mc.artisanworkstumps.modules.workstumps.init;

import com.codetaylor.mc.artisanworkstumps.modules.workstumps.network.SCPacketParticleProgress;
import com.codetaylor.mc.athenaeum.interaction.network.CSPacketInteractionMouseWheel;
import com.codetaylor.mc.athenaeum.network.IPacketRegistry;
import com.codetaylor.mc.athenaeum.network.tile.SCPacketTileData;
import net.minecraftforge.fml.relauncher.Side;

public final class PacketInitializer {

  public static void register(IPacketRegistry registry) {

    registry.register(
        SCPacketParticleProgress.class,
        SCPacketParticleProgress.class,
        Side.CLIENT
    );

    registry.register(
        CSPacketInteractionMouseWheel.class,
        CSPacketInteractionMouseWheel.class,
        Side.SERVER
    );

    // Tile Data
    registry.register(
        SCPacketTileData.class,
        SCPacketTileData.class,
        Side.CLIENT
    );
  }

  private PacketInitializer() {
    //
  }
}
