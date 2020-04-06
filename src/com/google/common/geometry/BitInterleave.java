/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.geometry;

import com.google.common.annotations.GwtCompatible;

/**
 * Utility functions for interleaving and deinterleaving sequences of bits.
 *
 */
@GwtCompatible
public final strictfp class BitInterleave {

  private BitInterleave() {}

  /** The lookup table below converts 8 bits into interleaved 16 bits. */
  private static final short[] INTERLEAVE_LOOKUP_TABLE = {
    0x0000, 0x0001, 0x0004, 0x0005, 0x0010, 0x0011, 0x0014, 0x0015,
    0x0040, 0x0041, 0x0044, 0x0045, 0x0050, 0x0051, 0x0054, 0x0055,
    0x0100, 0x0101, 0x0104, 0x0105, 0x0110, 0x0111, 0x0114, 0x0115,
    0x0140, 0x0141, 0x0144, 0x0145, 0x0150, 0x0151, 0x0154, 0x0155,
    0x0400, 0x0401, 0x0404, 0x0405, 0x0410, 0x0411, 0x0414, 0x0415,
    0x0440, 0x0441, 0x0444, 0x0445, 0x0450, 0x0451, 0x0454, 0x0455,
    0x0500, 0x0501, 0x0504, 0x0505, 0x0510, 0x0511, 0x0514, 0x0515,
    0x0540, 0x0541, 0x0544, 0x0545, 0x0550, 0x0551, 0x0554, 0x0555,
    0x1000, 0x1001, 0x1004, 0x1005, 0x1010, 0x1011, 0x1014, 0x1015,
    0x1040, 0x1041, 0x1044, 0x1045, 0x1050, 0x1051, 0x1054, 0x1055,
    0x1100, 0x1101, 0x1104, 0x1105, 0x1110, 0x1111, 0x1114, 0x1115,
    0x1140, 0x1141, 0x1144, 0x1145, 0x1150, 0x1151, 0x1154, 0x1155,
    0x1400, 0x1401, 0x1404, 0x1405, 0x1410, 0x1411, 0x1414, 0x1415,
    0x1440, 0x1441, 0x1444, 0x1445, 0x1450, 0x1451, 0x1454, 0x1455,
    0x1500, 0x1501, 0x1504, 0x1505, 0x1510, 0x1511, 0x1514, 0x1515,
    0x1540, 0x1541, 0x1544, 0x1545, 0x1550, 0x1551, 0x1554, 0x1555,
    0x4000, 0x4001, 0x4004, 0x4005, 0x4010, 0x4011, 0x4014, 0x4015,
    0x4040, 0x4041, 0x4044, 0x4045, 0x4050, 0x4051, 0x4054, 0x4055,
    0x4100, 0x4101, 0x4104, 0x4105, 0x4110, 0x4111, 0x4114, 0x4115,
    0x4140, 0x4141, 0x4144, 0x4145, 0x4150, 0x4151, 0x4154, 0x4155,
    0x4400, 0x4401, 0x4404, 0x4405, 0x4410, 0x4411, 0x4414, 0x4415,
    0x4440, 0x4441, 0x4444, 0x4445, 0x4450, 0x4451, 0x4454, 0x4455,
    0x4500, 0x4501, 0x4504, 0x4505, 0x4510, 0x4511, 0x4514, 0x4515,
    0x4540, 0x4541, 0x4544, 0x4545, 0x4550, 0x4551, 0x4554, 0x4555,
    0x5000, 0x5001, 0x5004, 0x5005, 0x5010, 0x5011, 0x5014, 0x5015,
    0x5040, 0x5041, 0x5044, 0x5045, 0x5050, 0x5051, 0x5054, 0x5055,
    0x5100, 0x5101, 0x5104, 0x5105, 0x5110, 0x5111, 0x5114, 0x5115,
    0x5140, 0x5141, 0x5144, 0x5145, 0x5150, 0x5151, 0x5154, 0x5155,
    0x5400, 0x5401, 0x5404, 0x5405, 0x5410, 0x5411, 0x5414, 0x5415,
    0x5440, 0x5441, 0x5444, 0x5445, 0x5450, 0x5451, 0x5454, 0x5455,
    0x5500, 0x5501, 0x5504, 0x5505, 0x5510, 0x5511, 0x5514, 0x5515,
    0x5540, 0x5541, 0x5544, 0x5545, 0x5550, 0x5551, 0x5554, 0x5555,
  };

  /**
   * The lookup table below can convert a sequence of interleaved 8 bits into non-interleaved 4
   * bits. The table can convert both odd and even bits at the same time, and
   * DEINTERLEAVE_LOOKUP_TABLE[x & 0x55] converts the even bits (bits 0, 2, 4 and 6), while
   * DEINTERLEAVE_LOOKUP_TABLE[x & 0xaa] converts the odd bits (bits 1, 3, 5 and 7).
   */
  private static final byte[] DEINTERLEAVE_LOOKUP_TABLE = {
    0x0, 0x1, 0x1, 0x1, 0x2, 0x3, 0x3, 0x3,
    0x2, 0x3, 0x3, 0x3, 0x2, 0x3, 0x3, 0x3,
    0x4, 0x5, 0x5, 0x5, 0x6, 0x7, 0x7, 0x7,
    0x6, 0x7, 0x7, 0x7, 0x6, 0x7, 0x7, 0x7,
    0x4, 0x5, 0x5, 0x5, 0x6, 0x7, 0x7, 0x7,
    0x6, 0x7, 0x7, 0x7, 0x6, 0x7, 0x7, 0x7,
    0x4, 0x5, 0x5, 0x5, 0x6, 0x7, 0x7, 0x7,
    0x6, 0x7, 0x7, 0x7, 0x6, 0x7, 0x7, 0x7,
    0x8, 0x9, 0x9, 0x9, 0xa, 0xb, 0xb, 0xb,
    0xa, 0xb, 0xb, 0xb, 0xa, 0xb, 0xb, 0xb,
    0xc, 0xd, 0xd, 0xd, 0xe, 0xf, 0xf, 0xf,
    0xe, 0xf, 0xf, 0xf, 0xe, 0xf, 0xf, 0xf,
    0xc, 0xd, 0xd, 0xd, 0xe, 0xf, 0xf, 0xf,
    0xe, 0xf, 0xf, 0xf, 0xe, 0xf, 0xf, 0xf,
    0xc, 0xd, 0xd, 0xd, 0xe, 0xf, 0xf, 0xf,
    0xe, 0xf, 0xf, 0xf, 0xe, 0xf, 0xf, 0xf,
    0x8, 0x9, 0x9, 0x9, 0xa, 0xb, 0xb, 0xb,
    0xa, 0xb, 0xb, 0xb, 0xa, 0xb, 0xb, 0xb,
    0xc, 0xd, 0xd, 0xd, 0xe, 0xf, 0xf, 0xf,
    0xe, 0xf, 0xf, 0xf, 0xe, 0xf, 0xf, 0xf,
    0xc, 0xd, 0xd, 0xd, 0xe, 0xf, 0xf, 0xf,
    0xe, 0xf, 0xf, 0xf, 0xe, 0xf, 0xf, 0xf,
    0xc, 0xd, 0xd, 0xd, 0xe, 0xf, 0xf, 0xf,
    0xe, 0xf, 0xf, 0xf, 0xe, 0xf, 0xf, 0xf,
    0x8, 0x9, 0x9, 0x9, 0xa, 0xb, 0xb, 0xb,
    0xa, 0xb, 0xb, 0xb, 0xa, 0xb, 0xb, 0xb,
    0xc, 0xd, 0xd, 0xd, 0xe, 0xf, 0xf, 0xf,
    0xe, 0xf, 0xf, 0xf, 0xe, 0xf, 0xf, 0xf,
    0xc, 0xd, 0xd, 0xd, 0xe, 0xf, 0xf, 0xf,
    0xe, 0xf, 0xf, 0xf, 0xe, 0xf, 0xf, 0xf,
    0xc, 0xd, 0xd, 0xd, 0xe, 0xf, 0xf, 0xf,
    0xe, 0xf, 0xf, 0xf, 0xe, 0xf, 0xf, 0xf,
  };

  /**
   * These functions interleave the given arguments into the return value.
   *
   * <p>The 0-bit in val0 will be the 0-bit in the return value. The 0-bit in val1 will be the 1-bit
   * in the return value. The 1-bit of val0 will be the 2-bit in the return value, and so on.
   *
   * @param val0 unsigned 32-bit integer to be interleaved.
   * @param val1 unsigned 32-bit integer to be interleaved.
   * @return unsigned 64-bit integer.
   */
  public static long interleaveInteger(int val0, int val1) {
    return INTERLEAVE_LOOKUP_TABLE[val0 & 0xff]
        | (((long) INTERLEAVE_LOOKUP_TABLE[(val0 >>> 8) & 0xff]) << 16)
        | (((long) INTERLEAVE_LOOKUP_TABLE[(val0 >>> 16) & 0xff]) << 32)
        | (((long) INTERLEAVE_LOOKUP_TABLE[val0 >>> 24]) << 48)
        | (((long) INTERLEAVE_LOOKUP_TABLE[val1 & 0xff]) << 1)
        | (((long) INTERLEAVE_LOOKUP_TABLE[(val1 >>> 8) & 0xff]) << 17)
        | (((long) INTERLEAVE_LOOKUP_TABLE[(val1 >>> 16) & 0xff]) << 33)
        | (((long) INTERLEAVE_LOOKUP_TABLE[val1 >>> 24]) << 49);
  }

  /**
   * Decodes the interleaved unsigned 64-bit integer (as long) into two unsigned 32-bit integer.
   *
   * @param code unsigned 64-bit integer to be deinterleaved.
   * @return arrays with two decoded unsigned 32-bit integer.
   */
  public static int[] deinterleaveInteger(long code) {
    int[] vals = new int[2];

    vals[0] =
        ((DEINTERLEAVE_LOOKUP_TABLE[(byte) code & 0x55])
            | (DEINTERLEAVE_LOOKUP_TABLE[(byte) (code >>> 8) & 0x55] << 4)
            | (DEINTERLEAVE_LOOKUP_TABLE[(byte) (code >>> 16) & 0x55] << 8)
            | (DEINTERLEAVE_LOOKUP_TABLE[(byte) (code >>> 24) & 0x55] << 12)
            | (DEINTERLEAVE_LOOKUP_TABLE[(byte) (code >>> 32) & 0x55] << 16)
            | (DEINTERLEAVE_LOOKUP_TABLE[(byte) (code >>> 40) & 0x55] << 20)
            | (DEINTERLEAVE_LOOKUP_TABLE[(byte) (code >>> 48) & 0x55] << 24)
            | (DEINTERLEAVE_LOOKUP_TABLE[(byte) (code >>> 56) & 0x55] << 28));
    vals[1] =
        ((DEINTERLEAVE_LOOKUP_TABLE[(byte) code & 0xaa])
            | (DEINTERLEAVE_LOOKUP_TABLE[(byte) (code >>> 8) & 0xaa] << 4)
            | (DEINTERLEAVE_LOOKUP_TABLE[(byte) (code >>> 16) & 0xaa] << 8)
            | (DEINTERLEAVE_LOOKUP_TABLE[(byte) (code >>> 24) & 0xaa] << 12)
            | (DEINTERLEAVE_LOOKUP_TABLE[(byte) (code >>> 32) & 0xaa] << 16)
            | (DEINTERLEAVE_LOOKUP_TABLE[(byte) (code >>> 40) & 0xaa] << 20)
            | (DEINTERLEAVE_LOOKUP_TABLE[(byte) (code >>> 48) & 0xaa] << 24)
            | (DEINTERLEAVE_LOOKUP_TABLE[(byte) (code >>> 56) & 0xaa] << 28));
    return vals;
  }
}
