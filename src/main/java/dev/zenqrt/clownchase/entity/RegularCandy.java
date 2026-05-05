package dev.zenqrt.clownchase.entity;

import net.minecraft.world.level.Level;

public final class RegularCandy extends Candy {

    private static final String TEXTURES = "ewogICJ0aW1lc3RhbXAiIDogMTYzNTExMTMxNTAwOSwKICAicHJvZmlsZUlkIiA6ICI5NDA5NDM2ZDVmYjE0NjA3ODI3OTU3YTY4MWZiMGU1MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXhCWmlnIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzJiMjE2MTdkMjc1NWJjMjBmOGY3ZTM4OGY0OWU0ODU4Mjc0NWZlYzE2YmIxNGM3NzZmNzExOGY5OGM1NWU4IgogICAgfQogIH0KfQ==";

    public RegularCandy(Level level) {
        super(level, TEXTURES);
    }

}
