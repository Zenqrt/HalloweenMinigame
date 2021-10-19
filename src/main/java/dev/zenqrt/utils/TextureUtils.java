package dev.zenqrt.utils;

import java.util.Base64;

public class TextureUtils {

    public static String getEncodedTexture(String url) {
        return Base64Utils.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
    }

}
