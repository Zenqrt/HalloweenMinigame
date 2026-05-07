package dev.zenqrt.clownchase.candy;

import dev.zenqrt.clownchase.entity.candy.*;
import dev.zenqrt.clownchase.item.CustomItems;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public enum CandyType {
    REGULAR(90, RegularCandy::new),
    SPEED(30, SpeedCandy::new),
    REGEN(50, RegenCandy::new),
    SHIELD(2, ShieldCandy::new),
    STUN_BALL(20, level -> new ItemCandy(level, CustomItems.STUN_BALL)),
    DAMAGE_TRAP(2, level -> new ItemCandy(level, CustomItems.DAMAGE_TRAP))
    ;

    private final Function<Level, Candy> factory;
    private final int weight;

    CandyType(int weight, Function<Level, Candy> factory) {
        this.weight = weight;
        this.factory = factory;
    }

    public Candy create(Level level) {
        return this.factory.apply(level);
    }

    public int weight() {
        return weight;
    }
}
