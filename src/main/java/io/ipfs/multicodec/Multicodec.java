package io.ipfs.multicodec;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Scanner;

/**
* Java Implementation of multiformats/multicodec (https://github.com/multiformats/multicodec)
* @author Aliabbas Merchant
* @version 1.0
* @since 2019-02-01
*/
public class Multicodec {
    /*
    A map to store the codec as key(string) and the corresponding prefix as value(integer)
     */
    public static HashMap<String, Integer> NameTable = new HashMap<>();

    /*
    A map to store the prefix as key(integer) and the corresponding codec as value(string)
     */
    public static HashMap<Integer, String> CodeTable = new HashMap<>();

    /*
    Static block to read the codecs from the 'table.csv' file and store them in the maps
     */
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the prefix(multicodec identifier) corresponding to the multicodec name
     * @param multicodec The multicodec name
     * @return The prefix, encoded in {@link VarInt} format
     * @exception IllegalArgumentException If an unsupported multicodec name is passed
     */
    public static ByteBuffer getPrefix(String multicodec) throws IllegalArgumentException {
        if (NameTable.containsKey(multicodec)) {
            return VarInt.encodeVarInt(NameTable.get(multicodec));
        } else {
            throw new IllegalArgumentException("The " + multicodec + " is not supported");
        }
    }

    /**
     * Checks if the codec is a valid/supported multicodec or not
     * @param codecName The multicodec name
     * @return True if the codecName is a supported multicodec, else false
     */
    public static boolean isCodec(String codecName) {
        return NameTable.containsKey(codecName);
    }

    /**
     * Returns the prefix(multicodec identifier) of the prefixed data
     * @param prefixedData Data prefixed with the multicodec identifier
     * @return The prefix, encoded in {@link VarInt} format
     */
    public static ByteBuffer extractPrefix(ByteBuffer prefixedData) {
        return VarInt.encodeVarInt(extractPrefixInt(prefixedData));
    }

    /**
     * Returns the prefix(multicodec identifier) of the prefixed data
     * @param prefixedData Data prefixed with the multicodec identifier
     * @return The prefix, encoded in integer format
     */
    private static int extractPrefixInt(ByteBuffer prefixedData) {
        return VarInt.decodeVarInt(prefixedData);
    }

    /**
     * Returns the data, after prefixing it with the multicodec identifier
     * @param multicodec Name of the multicodec with which the data has to be prefixed
     * @param data The data, which needs prefixing
     * @return The prefixed data
     * @exception IllegalArgumentException If an unsupported multicodec name is passed
     */
    public static ByteBuffer addPrefix(String multicodec, ByteBuffer data) throws IllegalArgumentException {
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
            throw new IllegalArgumentException("The " + multicodec + " is not supported");
        }
    }

    /**
     * Returns the data after removing the multicodec identifier
     * @param prefixedData The data, prefixed with a multicodec identifier
     * @return The prefix-removed data
     */
    public static ByteBuffer removePrefix(ByteBuffer prefixedData) {
        ByteBuffer prefix = extractPrefix(prefixedData);
        ByteBuffer ans = ByteBuffer.allocate(prefixedData.limit() - prefix.limit());
        for (int i = prefix.limit(); i < prefixedData.limit(); i++) {
            ans.put(prefixedData.get(i));
        }
        return ans;
    }

    /**
     * Returns the multicodec name, with which the data has been prefixed
     * @param prefixedData The data, prefixed with a multicodec identifier
     * @return The multicodec name
     * @exception IllegalArgumentException If the data was prefixed with an unsupported multicodec
     */
    public static String getCodec(ByteBuffer prefixedData) throws IllegalArgumentException {
        int code = extractPrefixInt(prefixedData);
        if (CodeTable.containsKey(code)) {
            return CodeTable.get(code);
        } else {
            throw new IllegalArgumentException("The code " + code + " is not found in the Codec Table");
        }
    }
}
