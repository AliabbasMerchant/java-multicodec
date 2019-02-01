package io.ipfs.multicodec;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.nio.ByteBuffer;
import static io.ipfs.multicodec.Multicodec.*;
import static io.ipfs.multicodec.VarInt.*;
import static org.junit.Assert.*;

/**
 * For testing {@link Multicodec}
 * @author Aliabbas Merchant
 * @version 1.0
 */
public class MulticodecTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
    For testing {@link Multicodec#getPrefix(String)}
     */
    @Test
    public void getPrefixTest() {
        String[] multicodecs = {"multihash", "multiaddr", "multibase"};
        int[] answers = {49, 50, 51};
        for (int i = 0; i < multicodecs.length; i++) {
            try {
                assertEquals(getPrefix(multicodecs[i]), VarInt.encodeVarInt(answers[i]));
            } catch (IllegalArgumentException e) {
                fail();
            }
        }
        String[] false_codecs = {"fdsf", "dfdfdf", "sha200"};
        for (String s : false_codecs) {
            try {
                getPrefix(s);
            } catch (IllegalArgumentException e) {
                assertTrue(true);
            }
        }
    }

    /**
     For testing {@link Multicodec#isCodec(String)}
     */
    @Test
    public void isCodecTest() {
        for (String s : NameTable.keySet()) {
            assertTrue(isCodec(s));
        }
        String[] false_codecs = {"fdsf", "dfdfdf", "sha200"};
        for (String s : false_codecs) {
            assertFalse(isCodec(s));
        }
    }

    /**
     For testing:<br>
     {@link Multicodec#addPrefix(String, ByteBuffer)}<br>
     {@link Multicodec#extractPrefix(ByteBuffer)}<br>
     {@link Multicodec#removePrefix(ByteBuffer)}<br>
     {@link Multicodec#getCodec(ByteBuffer)}<br>
     (The test for {@link Multicodec#getCodec(ByteBuffer)} fails,
     if more than 1 multicodec in 'table.csv' have the same code)
     */
    @Test
    public void prefixTests() {
        String something = "0dfe1iu2tc3rn79ztv5485e64";
        ByteBuffer someData = ByteBuffer.allocate(something.length());
        someData.put(something.getBytes());
        for (String s : NameTable.keySet()) {
            try {
                ByteBuffer prefixedData = addPrefix(s, someData);
                System.out.println("Checking for codec :" + s);
                System.out.println("Buffer of something:" + getBitsOfByteBuffer(someData));
                System.out.println("Final Buffer       :" + getBitsOfByteBuffer(prefixedData));
                System.out.println("Prefix Buffer      :" + getBitsOfByteBuffer(extractPrefix(prefixedData)));
                System.out.println("After removing pref:" + getBitsOfByteBuffer(removePrefix(prefixedData)));
                System.out.println("Codec              :" + getCodec(prefixedData));
                System.out.println();
                assertEquals(someData, removePrefix(prefixedData));
                assertEquals(s, getCodec(prefixedData));
                assertEquals(encodeVarInt(NameTable.get(s)), extractPrefix(prefixedData));
            } catch (IllegalArgumentException e) {
                fail();
            }
        }
        try {
            addPrefix("blakess", someData);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    /**
     * Helper method for returning the bits of a ByteBuffer
     * @param byteBuffer The input ByteBuffer
     * @return The bits of the ByteBuffer, represented as a string of 1s and 0s
     */
    private static String getBitsOfByteBuffer(ByteBuffer byteBuffer) {
        String ans = "";
        int i = 0;
        while (true) {
            try {
                if (!ans.equals("")) {
                    ans = ans.concat(getBitsOfByte(byteBuffer.get(i)));
                } else {
                    ans = getBitsOfByte(byteBuffer.get(i));
                }
                ans = ans.concat(" ");
                i += 1;
            } catch (Exception e) {
                break;
            }
        }
        return ans.trim();
    }

    /**
     * Helper method for returning the bits of a byte
     * @param b The input byte
     * @return The bits of the byte, represented as a string of 1s and 0s
     */
    private static String getBitsOfByte(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    /**
     * Tests for the correctness of the generated data
     */
    @Test
    public void pythonInteroperability() {
        String something = "EiC5TSe5k00";
        ByteBuffer someData = ByteBuffer.allocate(something.length());
        someData.put(something.getBytes());
        String[] trials = {"sha2-256", "blake2b-120"};
        String[] correct_bits = {
                "00010010 01000101 01101001 01000011 00110101 01010100 01010011 01100101 00110101 01101011 00110000 00110000",
                "10001111 11100100 00000010 01000101 01101001 01000011 00110101 01010100 01010011 01100101 00110101 01101011 00110000 00110000"
        };
        System.out.println("Buffer of something:" + getBitsOfByteBuffer(someData));
        for (int i=0;i<trials.length;i++) {
            try {
                ByteBuffer prefixedData = addPrefix(trials[i], someData);
                System.out.println("Checking for codec :" + trials[i]);
                System.out.println("Final Buffer       :" + getBitsOfByteBuffer(prefixedData));
                System.out.println("Expected Buffer    :" + correct_bits[i]);
                System.out.println("Codec              :" + getCodec(prefixedData));
                System.out.println();
                assertEquals(getBitsOfByteBuffer(prefixedData), correct_bits[i]);
                assertEquals(someData, removePrefix(prefixedData));
                assertEquals(trials[i], getCodec(prefixedData));
                assertEquals(encodeVarInt(NameTable.get(trials[i])), extractPrefix(prefixedData));
            } catch (IllegalArgumentException e) {
                fail();
            }
        }
    }
}
