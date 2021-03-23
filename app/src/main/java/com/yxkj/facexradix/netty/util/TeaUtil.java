package com.yxkj.facexradix.netty.util;

import io.netty.buffer.ByteBufUtil;

/**
 * Tea算法
 * 每次操作可以处理8个字节数据
 * KEY为16字节,应为包含4个int型数的int[]，一个int为4个字节
 * 加密解密轮数应为8的倍数，推荐加密轮数为64轮
 * */
public class TeaUtil {
	
	/**
	 * 秘钥
	 */
    private final static byte[] KEY = { 
    		//byte 0~15
    		(byte) 0x8B, 0x2B, 0x01, 0x6D, 0x6A, (byte) 0xEE, 0x35, 0x25, (byte) 0xCA, (byte) 0x91, (byte) 0xE5, (byte) 0xC3, 0x3C, 0x79, 0x5A, (byte) 0x83,
    		//byte 16~31
    		0x57, (byte) 0xC0, 0x19, 0x39, (byte) 0xF1, 0x1F, 0x51, (byte) 0xE9, (byte) 0xD0, 0x39, 0x1E, 0x37, 0x07, (byte) 0x80, 0x72, 0x4A,
    		//byte 32~47
    		0x4E, 0x7A, 0x56, 0x36, (byte) 0xBE, (byte) 0xEB, (byte) 0x88, (byte) 0xCC, 0x4B, (byte) 0x97, 0x1C, (byte) 0xFC, 0x4D, (byte) 0x93, (byte) 0xFB, (byte) 0xE0,
    		//byte 48~63
    		(byte) 0xCC, (byte) 0xE0, 0x41, (byte) 0xBF, 0x0A, 0x20, (byte) 0x97, 0x6C, (byte) 0xDA, 0x18, (byte) 0x9C, 0x31, (byte) 0xD0, (byte) 0xFD, 0x1D, 0x49,
    		//byte 64~79
    		0x1E, (byte) 0x9A, 0x74, (byte) 0xC1, 0x22, 0x23, (byte) 0xC4, 0x6C, 0x37, 0x4D, (byte) 0xED, 0x5A, 0x03, 0x55, (byte) 0x9B, 0x3D,
    		//byte 80~95
    		0x3D, 0x3E, 0x6A, 0x27, 0x3F, (byte) 0xB2, 0x3F, 0x2E, (byte) 0xE2, (byte) 0xEA, 0x5C, (byte) 0xA6, 0x4E, 0x56, (byte) 0xFB, 0x4D,
    		//byte 96~111
    		0x79, (byte) 0xD2, (byte) 0xDD, (byte) 0xB2, 0x1F, 0x39, (byte) 0xBD, (byte) 0xA9, 0x1C, (byte) 0xD5, (byte) 0x87, (byte) 0xBC, 0x33, 0x3C, 0x68, (byte) 0x85,
    		//byte 112~127
    		0x7C, 0x5F, (byte) 0xF3, (byte) 0x92, (byte) 0x92, (byte) 0xF6, 0x0A, 0x6C, 0x5B, 0x46, (byte) 0xDE, 0x37, (byte) 0xC6, 0x1E, 0x1D, (byte) 0xE1,
    		//byte 128~143
            (byte) 0x8B, 0x2B, 0x01, 0x6D, 0x6A, (byte) 0xEE, 0x35, 0x25, (byte) 0xCA, (byte) 0x91, (byte) 0xE5, (byte) 0xC3, 0x3C, 0x79, 0x5A, (byte) 0x83 };
    
    /**
     * 加密次数
     */
    private final static int times = 32;
    
    
    /**
     * 加密，每次8byte
     */
    private static byte[] encrypt(byte[] data, int offset, int[] key){
        int[] tempInt = byteToInt(data, offset);
        long y = toUInt32(tempInt[0]), z = toUInt32(tempInt[1]), sum = 0, i, temp;
        long delta = toUInt32(0x9e3779b9); //这是算法标准给的值
        long a = toUInt32(key[0]), b = toUInt32(key[1]), c = toUInt32(key[2]), d = toUInt32(key[3]); 
        for (i = 0; i < times; i++) { //加密times次
            sum = toUInt32(sum + delta);
            temp = 0L;
            temp = toUInt32(z<<4);
            temp = toUInt32(temp + a);
            temp = toUInt32(temp ^ toUInt32(z + sum));
            temp = toUInt32(temp ^ toUInt32(toUInt32(z>>5) + b));
            y = toUInt32(temp + y);
            temp = 0L;
            temp = toUInt32(y<<4);
            temp = toUInt32(temp + c);
            temp = toUInt32(temp ^ toUInt32(y + sum));
            temp = toUInt32(temp ^ toUInt32(toUInt32(y>>5) + d));
            z = toUInt32(temp + z);
        }
        tempInt[0]= (int) y;
        tempInt[1]= (int) z;
        return intToByte(tempInt, 0);
    }
    
    /**
     * 解密
     * @param data
     * @param offset
     * @param key
     * @return
     */
    private static byte[] decrypt(byte[] data, int offset, int[] key){
    	int[] tempInt = byteToInt(data, offset);
        long y = toUInt32(tempInt[0]), z = toUInt32(tempInt[1]), i, temp;
        long delta = toUInt32(0x9e3779b9); //这是算法标准给的值
        long a = toUInt32(key[0]), b = toUInt32(key[1]), c = toUInt32(key[2]), d = toUInt32(key[3]);
        long sum = toUInt32(0xC6EF3720);
        for(i = 0; i < times; i++) {
        	temp = 0L;
        	temp = toUInt32(y<<4);
        	temp = toUInt32(temp + c);
        	temp = toUInt32(temp ^ toUInt32(y + sum));
        	temp = toUInt32(temp ^ toUInt32(toUInt32(y>>5) + d));
            z = toUInt32(z - temp);
            temp = 0L;
            temp = toUInt32(z<<4);
            temp = toUInt32(temp + a);
            temp = toUInt32(temp ^ toUInt32(z + sum));
            temp = toUInt32(temp ^ toUInt32(toUInt32(z>>5) + b));
            y = toUInt32(y - temp);
            sum = toUInt32(sum - delta); 
        }
        tempInt[0] = (int)y;
        tempInt[1] = (int)z;
        return intToByte(tempInt, 0);
    }
    
    //byte[]型数据转成int[]型数据
    private static int[] byteToInt(byte[] content, int offset){
        int[] result = new int[content.length >> 2];
        for(int i = 0, j = offset; j < content.length; i++, j += 4){
            result[i] = transform(content[j]) 
            		| transform(content[j +1]) << 8 
            		| transform(content[j + 2]) << 16 
            		| (int)content[j + 3] << 24;
        }
        return result;
    }
    
    private static long toUInt32(long n) {
        return n & 0xFFFFFFFFL;
    }
    
    //int[]型数据转成byte[]型数据
    private static byte[] intToByte(int[] content, int offset){
        byte[] result = new byte[content.length << 2];
        for(int i = 0, j = offset; j < result.length; i++, j += 4){
            result[j] = (byte)(content[i] & 0xff);
            result[j + 1] = (byte)((content[i] >> 8) & 0xff);
            result[j + 2] = (byte)((content[i] >> 16) & 0xff);
            result[j + 3] = (byte)((content[i] >> 24) & 0xff);
        }
        return result;
    }
    
    //若某字节为负数则需将其转成无符号正数
    private static int transform(byte temp){
        int tempInt = (int)temp;
        if(tempInt < 0){
            tempInt += 256;
        }
        return tempInt;
    }
    
    /**
     * 	通过TEA算法加密信息
     * @param data
     * @param random
     * @return
     */
    public static byte[] encryptByTea(byte[] data, byte random){
        byte[] result = new byte[data.length];
        int[] keyData = buildKey(random);
        for(int offset = 0; offset < result.length; offset += 8){
            byte[] temp = TeaUtil.encrypt(data, offset, keyData);
            System.arraycopy(temp, 0, result, offset, 8);
        }
        return result;
    }
    
    /**
     * 	通过TEA算法解密信息
     * @param data
     * @param random
     * @return
     */
    public static byte[] decryptByTea(byte[] data, byte random){
        byte[] result = new byte[data.length];
        int[] keyData = buildKey(random);
        for(int offset = 0; offset < data.length; offset += 8){
            byte[] temp = TeaUtil.decrypt(data, offset, keyData);
            System.arraycopy(temp, 0, result, offset, 8);
        }
        return result;
    }
    
    private static int[] buildKey(byte offset) {
    	byte[] data = new byte[16];
    	System.arraycopy(KEY, Math.abs(offset), data, 0, 16);
    	int[] result = byteToInt(data, 0);
    	return result;
    }
    
    public static void main(String[] args){
    	String hex = "3D30314058807442";
    	byte[] data = ByteBufUtil.decodeHexDump(hex);
    	byte[] result = new byte[data.length];

    	String keyHex = "8B2B016D6AEE3525CA91E5C33C795A83";
    	byte[] keyByte = ByteBufUtil.decodeHexDump(keyHex);
    	int[] keyInt = byteToInt(keyByte, 0);
    	for(int offset = 0; offset < data.length; offset += 8){
            byte[] temp = TeaUtil.decrypt(data, offset, keyInt);
            System.arraycopy(temp, 0, result, offset, 8);
        }
    	System.out.println(ByteBufUtil.hexDump(result));
    }
    
}