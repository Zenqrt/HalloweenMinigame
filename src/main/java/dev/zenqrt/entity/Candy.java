package dev.zenqrt.entity;

import dev.zenqrt.utils.EntityUtils;
import dev.zenqrt.utils.TextureUtils;
import dev.zenqrt.utils.particle.ParticleEmitter;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.PlayerHeadMeta;
import net.minestom.server.particle.Particle;

public class Candy extends RotatingArmorStand {

    private static final ParticleEmitter particleEmitter = new ParticleEmitter(Particle.CRIT, new Vec(0.5, 0.5, 0.5), 0.05F, 5, false);

    public Candy(Color color) {
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

        sendPacketToViewers(particleEmitter.createPacket(position));
    }

    public enum Color {
        RED("http://textures.minecraft.net/texture/2b21617d2755bc20f8f7e388f49e48582745fec16bb14c776f7118f98c55e8"),
        BLUE("http://textures.minecraft.net/texture/2542575423246faa73a3763e1b46dcfa3f46edb9b15acb0bc1190146ee19d"),
        GREEN("http://textures.minecraft.net/texture/7ecd5e8480214bd633756e6192e1473ce26aaba7a6fb82f591880ab4877563"),
        PINK("http://textures.minecraft.net/texture/17752bdccad6a4678b6eaa5923738aa63d0b7517180329216f0aa34feb55eb"),
        PURPLE("http://textures.minecraft.net/texture/d1081830e644d29ee194bd65e74099fb9747da8c3d8817f94829e65cfce49a"),
        ORANGE("http://textures.minecraft.net/texture/85331cfc6cbc9d4697fd35bb84d58da4099bad927e9fa66e896dc5275cd379"),
        YELLOW("http://textures.minecraft.net/texture/9361a7847b8c1d08de0438d58a5a32475d61728a3d651498fe867bd5d598");

        private final PlayerSkin playerSkin;

        Color(String url) {
            this.playerSkin = new PlayerSkin(TextureUtils.getEncodedTexture(url), "");
        }

        public PlayerSkin getPlayerSkin() {
            return playerSkin;
        }
    }

}
