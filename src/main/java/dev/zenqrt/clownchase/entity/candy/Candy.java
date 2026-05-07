package dev.zenqrt.clownchase.entity.candy;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.zenqrt.clownchase.game.ClownChaseGame;
import dev.zenqrt.clownchase.game.GamePlayerData;
import io.papermc.paper.profile.MutablePropertyMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Candy extends Display.ItemDisplay {

    public Candy(Level level, String textures) {
        super(EntityType.ITEM_DISPLAY, level);

        MutablePropertyMap properties = new MutablePropertyMap();
        properties.put("textures", new Property("textures", textures));

        ResolvableProfile profile = ResolvableProfile.createResolved(new GameProfile(UUID.randomUUID(), "", properties));

        ItemStack displayItem = new ItemStack(Items.PLAYER_HEAD);
        displayItem.set(DataComponents.PROFILE, profile);

        this.setItemStack(displayItem);
    }

    public void onConsume(ClownChaseGame game, GamePlayerData playerData, Player player) {}

    protected void particleTick() {

    }

    @Override
    public void tick() {
        particleTick();
        this.setRot(this.getYRot() + 4F, 0);

        super.tick();
    }
}
