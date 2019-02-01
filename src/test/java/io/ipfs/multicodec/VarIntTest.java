package io.ipfs.multicodec;

import org.junit.Test;

import java.nio.ByteBuffer;

import static io.ipfs.multicodec.VarInt.decodeVarInt;
import static io.ipfs.multicodec.VarInt.encodeVarInt;
import static org.junit.Assert.*;


/**
 * For testing {@link VarInt}
 *
 * @author Aliabbas Merchant
 * @version 1.0
 */
public class VarIntTest {

    /**
     * For testing the correctness of {@link VarInt#encodeVarInt(int)}
     */
    @Test
    public void encodeVarIntTest() {
        int[] test_cases = {1, 127, 128, 255, 300, 16384};
        String[] test_answers = {"00000001", "01111111", "10000000 00000001", "11111111 00000001", "10101100 00000010", "10000000 10000000 00000001"};
        for (int i = 0; i < test_cases.length; i++) {
            try {
                assertEquals(getBitsOfByteBuffer(encodeVarInt(test_cases[i])).trim(), test_answers[i]);
            } catch (Exception e) {
                fail();
            }
        }
        try {
            encodeVarInt(-200);
            fail();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * For testing the correctness of {@link VarInt#decodeVarInt(ByteBuffer)}
     */
    @Test
    public void decodeVarIntTest() {
        int[] test_cases = {1, 127, 128, 255, 300, 16384};
        for (int test_case : test_cases) {
            try {
                assertEquals(decodeVarInt(encodeVarInt(test_case)), test_case);
            } catch (Exception e) {
                fail();
            }
        }
    }

    /**
     * Helper method for returning the bits of a ByteBuffer
     *
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
     *
     * @param b The input byte
     * @return The bits of the byte, represented as a string of 1s and 0s
     */
    private static String getBitsOfByte(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }


}
