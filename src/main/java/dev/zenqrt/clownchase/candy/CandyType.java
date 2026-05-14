package dev.zenqrt.clownchase.candy;

import dev.zenqrt.clownchase.entity.candy.*;
import dev.zenqrt.clownchase.item.CustomItems;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public enum CandyType {
    REGULAR(90, RegularCandy::new),
    SPEED(3, SpeedCandy::new),
    REGEN(1, RegenCandy::new),
    SHIELD(2, ShieldCandy::new),
    STUN_BALL(2, level -> new ItemCandy(level, CustomItems.STUN_BALL)),
    DAMAGE_TRAP(1, level -> new ItemCandy(level, CustomItems.DAMAGE_TRAP)),
    WALL_PLACER(1, level -> new ItemCandy(level, CustomItems.WALL_PLACER)),
    CLOWN_SPEED(10000, level -> new ItemCandy(level, CustomItems.CLOWN_SPEED))
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
