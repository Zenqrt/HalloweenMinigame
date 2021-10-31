package dev.zenqrt.entity.candy.special;

import dev.zenqrt.entity.candy.SpecialCandy;
import dev.zenqrt.utils.chat.ChatUtils;
import dev.zenqrt.utils.chat.ParsedColor;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;

public class AbsorptionCandy extends SpecialCandy {

    @Override
    public void consume(Player player) {
        super.consume(player);

        var color = ParsedColor.of(ChatUtils.TEXT_COLOR_HEX);

        player.setAdditionalHearts(player.getAdditionalHearts() + 2);
        player.playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1, 2), Sound.Emitter.self());
        player.sendMessage(MiniMessage.get().parse(color + "You have receieved <yellow>1 " + color + "extra heart!"));
    }
}
