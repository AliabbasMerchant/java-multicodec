package io.ipfs.multicodec;

import java.nio.ByteBuffer;

/*
 * Copyright 2016 Kukri Máté.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

// Well, I just got inspired by a particular gist, and they had mentioned to include the copyright notice, so I did.
// The code was not copied from there, but taken from the fmoo/python-varint repo and edited.

public class VarInt {
    public static ByteBuffer encodeVarInt(int input) throws Exception {
        if (input >= 0) {
            ByteBuffer buf = ByteBuffer.allocate(5);
            while (true) {
                int toWrite = input & 0x7f;
                input >>>= 7;
                if (input != 0) {
                    buf.put((byte) (toWrite | 0x80));
                } else {
                    buf.put((byte) (toWrite));
                    break;
                }
            }
            buf.flip();
            return buf;
        } else {
            throw new Exception("The input cannot be a negative integer");
        }
    }

    public static int decodeVarInt(ByteBuffer varint) {
        int shift = 0;
        int result = 0;
        int index = 0;
        while (true) {
            int i = varint.get(index);
            index += 1;
            result |= (i & 0x7f) << shift;
            shift += 7;
            if ((i & 0x80) == 0) {
                break;
            }
        }
        return result;
    }
}