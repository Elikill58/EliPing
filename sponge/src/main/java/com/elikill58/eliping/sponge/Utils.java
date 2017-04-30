package com.elikill58.eliping.sponge;

import org.spongepowered.api.text.serializer.TextSerializers;

public final class Utils {

    private Utils() {}

    public static String applyColorCodes(String message) {
        return TextSerializers.FORMATTING_CODE.replaceCodes(message, '\u00a7');
    }
}
