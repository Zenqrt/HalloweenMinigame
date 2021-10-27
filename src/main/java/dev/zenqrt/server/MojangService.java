package dev.zenqrt.server;

import net.minestom.server.utils.mojang.MojangUtils;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.UUID;

public class MojangService {

    @Nullable
    public static UUID retrieveUuid(String username) {
        var json = MojangUtils.fromUsername(username);
        if(json == null) return null;

        var bigInt = new BigInteger(json.get("id").getAsString(), 16);
        return new UUID(bigInt.shiftRight(64).longValue(), bigInt.longValue());
    }

}
