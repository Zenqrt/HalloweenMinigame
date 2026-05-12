package dev.zenqrt.clownchase.item;

import dev.zenqrt.clownchase.item.items.DamageTrapItem;
import dev.zenqrt.clownchase.item.items.StunBallItem;
import dev.zenqrt.clownchase.item.items.WallPlacerItem;

public interface CustomItems {

    CustomItem STUN_BALL = new StunBallItem();
    CustomItem DAMAGE_TRAP = new DamageTrapItem();
    CustomItem WALL_PLACER = new WallPlacerItem();

}
