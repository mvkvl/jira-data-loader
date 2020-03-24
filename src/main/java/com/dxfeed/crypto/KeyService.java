package com.dxfeed.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KeyService {

    // keys encoded with StringTools.stringToHexString("<ORIGINAL_KEY_STRING>")
    private final String SHARED  = "";

    public byte [] getShared() {
        return hexStringToBytes(SHARED);
    }

    public static String stringToHexString(String value) {
        StringBuilder sb = new StringBuilder();
        for (byte b : value.getBytes()) {
            sb.append(String.format("%x", b));
        }
        return sb.toString();
    }

    public static String hexStringToString(String value) {
        String split = value.replaceAll("..(?!$)", "$0 ");
        List<Byte> chars = Arrays.stream(split.split(" ")).map(s -> (byte)Integer.parseInt(s, 16)).collect(Collectors.toList());
        byte [] result = new byte[chars.size()];
        for (int i = 0; i < chars.size(); i++)
            result[i] = chars.get(i);
        return new String(result);
    }

    public static byte[] hexStringToBytes(String value) {
        return hexStringToString(value).getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] stringToBytes(String value) {
        byte [] shiftA = hexStringToBytes(value);
        byte [] result = new byte[shiftA.length/2];
        for (int i = 0; i < result.length; i++) {
            char v1 = (char)shiftA[i*2];
            char v2 = (char)shiftA[i*2+1];
            char s1 = v1 > '9' ? 'A' - 10 : '0';
            char s2 = v2 > '9' ? 'A' - 10 : '0';
            result[i] = (byte)((v1 - s1) << 4 | (v2 - s2));
        }
        return result;
    }

}
