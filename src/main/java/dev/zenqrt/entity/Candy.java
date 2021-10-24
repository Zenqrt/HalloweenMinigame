package dev.zenqrt.entity;

import dev.zenqrt.utils.EntityUtils;
import dev.zenqrt.utils.TextureUtils;
import dev.zenqrt.utils.particle.ParticleEmitter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.PlayerHeadMeta;
import net.minestom.server.particle.Particle;

import java.util.Objects;

public class Candy extends RotatingArmorStand {

    public ParticleEmitter ambientParticleEmitter,consumeParticleEmitter, colorConsumeParticleEmitter;

    public Candy(Color color) {
        this.ambientParticleEmitter = new ParticleEmitter(Particle.CRIT, new Vec(0.5, 0.5, 0.5), 0.05F, 5, false);
        this.consumeParticleEmitter = createConsumeEmitter(Block.WHITE_CONCRETE.id());
        this.colorConsumeParticleEmitter = createConsumeEmitter(color.blockId);
        this.setHelmet(ItemStack.builder(Material.PLAYER_HEAD)
                .meta(PlayerHeadMeta.class, meta -> meta.playerSkin(color.getPlayerSkin()))
                .build()
        );

        var meta = (ArmorStandMeta) this.getLivingEntityMeta();
        EntityUtils.editMeta(meta, m -> {
            m.setInvisible(true);
            m.setHasNoGravity(true);
            m.setHasNoBasePlate(true);
        });
    }

    @Override
    public void tick(long time) {
        super.tick(time);

        sendPacketToViewers(ambientParticleEmitter.createPacket(position));
    }

    public void consume(Player player) {
        var position = player.getPosition().add(0, player.getEyeHeight(), 0);
        player.sendPacketToViewersAndSelf(consumeParticleEmitter.createPacket(position));
        player.sendPacketToViewersAndSelf(colorConsumeParticleEmitter.createPacket(position));
        Objects.requireNonNull(player.getInstance()).playSound(Sound.sound(Key.key("minecraft:entity.player.burp"), Sound.Source.PLAYER, 1, 1.5f), Sound.Emitter.self());
        this.remove();
    }

    private ParticleEmitter createConsumeEmitter(int blockId) {
        return new ParticleEmitter(Particle.BLOCK, new Vec(0.5), 0.01f, 10, true, writer -> writer.writeInt(blockId));
    }

    public enum Color {
        RED("http://textures.minecraft.net/texture/2b21617d2755bc20f8f7e388f49e48582745fec16bb14c776f7118f98c55e8", Block.RED_CONCRETE),
        BLUE("http://textures.minecraft.net/texture/2542575423246faa73a3763e1b46dcfa3f46edb9b15acb0bc1190146ee19d", Block.BLUE_CONCRETE),
        GREEN("http://textures.minecraft.net/texture/7ecd5e8480214bd633756e6192e1473ce26aaba7a6fb82f591880ab4877563", Block.GREEN_CONCRETE),
        PINK("http://textures.minecraft.net/texture/17752bdccad6a4678b6eaa5923738aa63d0b7517180329216f0aa34feb55eb", Block.PINK_CONCRETE),
        PURPLE("http://textures.minecraft.net/texture/d1081830e644d29ee194bd65e74099fb9747da8c3d8817f94829e65cfce49a", Block.PURPLE_CONCRETE),
        ORANGE("http://textures.minecraft.net/texture/85331cfc6cbc9d4697fd35bb84d58da4099bad927e9fa66e896dc5275cd379", Block.ORANGE_CONCRETE),
        YELLOW("http://textures.minecraft.net/texture/9361a7847b8c1d08de0438d58a5a32475d61728a3d651498fe867bd5d598", Block.YELLOW_CONCRETE);

        private final PlayerSkin playerSkin;
        private final int blockId;

        Color(String url, Block block) {
            this.playerSkin = new PlayerSkin(TextureUtils.getEncodedTexture(url), "");
            this.blockId = block.id();
        }

        public PlayerSkin getPlayerSkin() {
            return playerSkin;
        }

        public int getBlockId() {
            return blockId;
        }
    }

}
