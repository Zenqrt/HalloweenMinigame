package dev.zenqrt.item.listeners;

import com.github.christian162.annotations.Listen;
import com.github.christian162.annotations.Node;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionPacket;

@Node(name = "packet-listen", event = PlayerEvent.class)
public class PacketEvents {

    @Listen
    public void listen(PlayerPacketEvent event) {
        var packet = event.getPacket();
        if(packet instanceof ClientPlayerPositionPacket) return;
        System.out.println("[" + event.getPlayer().getUsername() + "]: " + event.getPacket().getClass().getName());
        // well i was going to but then who knows what happened to that one
        // which one
        // yeah i can learn kotlin
        // if its less typing then im down
        // yea
    }

}
