package dev.zenqrt.clownchase.item;

import dev.zenqrt.clownchase.item.items.DamageTrapItem;
import dev.zenqrt.clownchase.item.items.StunBallItem;

public interface CustomItems {

    CustomItem STUN_BALL = new StunBallItem();
    CustomItem DAMAGE_TRAP = new DamageTrapItem();

}
