package io.ipfs.multicodec;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Scanner;

public class Multicodec {
    public static HashMap<String, Integer> NameTable = new HashMap<>();
    public static HashMap<Integer, String> CodeTable = new HashMap<>();

    static {
        try {
            Scanner scanner = new Scanner(new File("table.csv"));
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();
                if (!s.trim().equals("")) {
                    String[] attributes = s.split(",");
                    try {
                        String codec = attributes[0].trim();
                        if (attributes[2].trim().startsWith("0x")) {
                            int code = Integer.parseInt(attributes[2].trim().substring(2), 16);
                            NameTable.put(codec, code);
                            CodeTable.put(code, codec);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ByteBuffer getPrefix(String multicodec) throws Exception {
        if (NameTable.containsKey(multicodec)) {
            return VarInt.encodeVarInt(NameTable.get(multicodec));
        } else {
            throw new Exception("The " + multicodec + " is not supported");
        }
    }

    public static boolean isCodec(String codecName) {
        return NameTable.containsKey(codecName);
    }

    public static ByteBuffer extractPrefix(ByteBuffer prefixedData) throws Exception {
        return VarInt.encodeVarInt(extractPrefixInt(prefixedData));
    }

    private static int extractPrefixInt(ByteBuffer prefixedData) {
        return VarInt.decodeVarInt(prefixedData);
    }

    public static ByteBuffer addPrefix(String multicodec, ByteBuffer data) throws Exception {
        if (NameTable.containsKey(multicodec)) {
            ByteBuffer varInt = VarInt.encodeVarInt(NameTable.get(multicodec));
            ByteBuffer b = ByteBuffer.allocate(data.limit() + varInt.limit());
//            b.put(varInt);
            for (int i = 0; i < varInt.limit(); i++) {
                b = b.put(varInt.get(i));
            }
//            b.put(data);
            for (int i = 0; i < data.limit(); i++) {
                b = b.put(data.get(i));
            }
            return b;
        } else {
            throw new Exception("The " + multicodec + " is not supported");
        }
    }

    public static ByteBuffer removePrefix(ByteBuffer prefixedData) throws Exception {
        ByteBuffer prefix = extractPrefix(prefixedData);
        ByteBuffer ans = ByteBuffer.allocate(prefixedData.limit() - prefix.limit());
        for (int i = prefix.limit(); i < prefixedData.limit(); i++) {
            ans.put(prefixedData.get(i));
        }
        return ans;
    }

    public static String getCodec(ByteBuffer prefixedData) throws Exception {
        int code = extractPrefixInt(prefixedData);
        if (CodeTable.containsKey(code)) {
            return CodeTable.get(code);
        } else {
            throw new Exception("The code " + code + " is not found in the Codec Table");
        }
    }
}
