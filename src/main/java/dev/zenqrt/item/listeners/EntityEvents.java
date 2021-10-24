package dev.zenqrt.item.listeners;

import com.github.christian162.annotations.Listen;
import com.github.christian162.annotations.Node;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.trait.EntityEvent;

@Node(name = "entity-events", event = EntityEvent.class)
public class EntityEvents {

    // yeah but isnt the yaml stuff their code?
    @Listen
    public void onAttack(EntityAttackEvent event) {
        System.out.println("attack");
        // yeah im just seeing why this projectile is disappearing
        // no msgs
        // and i put msgs everywhere
        // yes
        // not the attack event yet

    }

}
