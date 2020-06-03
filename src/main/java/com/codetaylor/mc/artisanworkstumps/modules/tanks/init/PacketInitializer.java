package com.codetaylor.mc.artisanworkstumps.modules.tanks.init;

import com.codetaylor.mc.artisanworkstumps.modules.tanks.network.SCPacketParticleCombust;
import com.codetaylor.mc.athenaeum.network.IPacketRegistry;
import net.minecraftforge.fml.relauncher.Side;

public final class PacketInitializer {

  public static void register(IPacketRegistry registry) {

    registry.register(
        SCPacketParticleCombust.class,
        SCPacketParticleCombust.class,
        Side.CLIENT
    );
  }

  private PacketInitializer() {
    //
  }
}
