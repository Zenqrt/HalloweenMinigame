package dev.zenqrt.utils;

import java.io.*;
import java.util.Base64;

public class Base64Utils {

    /**
     * Encodes object in Base64 format.
     * @param object Object
     * @return A Base64 encoded string
     */
    public static String encode(Object object) {
        try {
            var io = new ByteArrayOutputStream();
            var output = new ObjectOutputStream(io);
            output.writeObject(object);
            output.close();
            var serialized = io.toByteArray();
            return Base64.getEncoder().encodeToString(serialized);
        } catch (IOException var4) {
            var4.printStackTrace();
            return "error";
        }
    }

    /**
     * Decodes object from Base64 encoded string
     * @param encoded Encoded string
     * @return A decoded object
     */
    public static Object decode(String encoded) {
        try {
            var serialized = Base64.getDecoder().decode(encoded);
            var in = new ByteArrayInputStream(serialized);
            var is = new ObjectInputStream(in);
            var object = is.readObject();
            System.out.println("OBJECT IS NULL = " + (object == null));
            return object;
        } catch (ClassNotFoundException | IOException var5) {
            System.out.println("error");
            var5.printStackTrace();
            return new Object();
        }
    }

}
