package dev.zenqrt.clownchase.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.papermc.paper.profile.MutablePropertyMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;

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

    protected void particleTick() {

    }

    @Override
    public void tick() {
        particleTick();
        this.setRot(this.getYRot() + 2F, 0);

        super.tick();
    }
}
