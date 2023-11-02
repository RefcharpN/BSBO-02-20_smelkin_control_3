package ru.mirea.smelkin.mireaproject;

import android.util.Log;

import java.nio.charset.StandardCharsets;

public class sha2 {

    public String finalhash;

    sha2(String message)
    {
        String input = "2517Pass";
        String sha256Hash = sha256(input);
        Log.d("sha_test" ,"SHA-256 hash: " + sha256Hash);
        this.finalhash = sha256Hash;
    }

    public static String sha256(String input) {
        try {
            byte[] inputData = input.getBytes(StandardCharsets.UTF_8);
            byte[] hashBytes = calculateHash(inputData);

            // Преобразование массива байт в строку шестнадцатеричного представления
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] calculateHash(byte[] input) {
        int[] k = {
                0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
                0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
                0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
                0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
                0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
                0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
                0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
                0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
        };

        // Преобразование входных данных в массив 64-байтных блоков
        int paddedLength = ((input.length + 8) / 64 + 1) * 64;
        byte[] paddedInput = new byte[paddedLength];
        System.arraycopy(input, 0, paddedInput, 0, input.length);
        paddedInput[input.length] = (byte) 0x80;
        long inputLengthBits = (long) input.length * 8;
        for (int i = 0; i < 8; i++) {
            paddedInput[paddedLength - 8 + i] = (byte) (inputLengthBits >>> (56 - 8 * i));
        }

        // Инициализация хеш-значений
        int[] hash = {
                0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
        };

        // Обработка блоков
        for (int offset = 0; offset < paddedInput.length; offset += 64) {
            int[] w = new int[64];
            for (int i = 0; i < 16; i++) {
                w[i] = ((paddedInput[offset + i * 4] & 0xFF) << 24)
                        | ((paddedInput[offset + i * 4 + 1] & 0xFF) << 16)
                        | ((paddedInput[offset + i * 4 + 2] & 0xFF) << 8)
                        | (paddedInput[offset + i * 4 + 3] & 0xFF);
            }
            for (int i = 16; i < 64; i++) {
                int s0 = Integer.rotateRight(w[i - 15], 7) ^ Integer.rotateRight(w[i - 15], 18) ^ (w[i - 15] >>> 3);
                int s1 = Integer.rotateRight(w[i - 2], 17) ^ Integer.rotateRight(w[i - 2], 19) ^ (w[i - 2] >>> 10);
                w[i] = w[i - 16] + s0 + w[i - 7] + s1;
            }

            int a = hash[0];
            int b = hash[1];
            int c = hash[2];
            int d = hash[3];
            int e = hash[4];
            int f = hash[5];
            int g = hash[6];
            int h = hash[7];

            for (int i = 0; i < 64; i++) {
                int s1 = Integer.rotateRight(e, 6) ^ Integer.rotateRight(e, 11) ^ Integer.rotateRight(e, 25);
                int ch = (e & f) ^ (~e & g);
                int temp1 = h + s1 + ch + k[i] + w[i];
                int s0 = Integer.rotateRight(a, 2) ^ Integer.rotateRight(a, 13) ^ Integer.rotateRight(a, 22);
                int maj = (a & b) ^ (a & c) ^ (b & c);
                int temp2 = s0 + maj;

                h = g;
                g = f;
                f = e;
                e = d + temp1;
                d = c;
                c = b;
                b = a;
                a = temp1 + temp2;
            }

            hash[0] += a;
            hash[1] += b;
            hash[2] += c;
            hash[3] += d;
            hash[4] += e;
            hash[5] += f;
            hash[6] += g;
            hash[7] += h;
        }

        // Преобразование массива хеш-значений в массив байт
        byte[] hashBytes = new byte[hash.length * 4];
        for (int i = 0; i < hash.length; i++) {
            intToBytes(hash[i], hashBytes, i * 4);
        }

        return hashBytes;
    }

    private static void intToBytes(int value, byte[] bytes, int offset) {
        bytes[offset] = (byte) (value >>> 24);
        bytes[offset + 1] = (byte) (value >>> 16);
        bytes[offset + 2] = (byte) (value >>> 8);
        bytes[offset + 3] = (byte) value;
    }

}
