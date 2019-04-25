package cn.gq.executerdemo.execute;

/**
 * bytes数组处理工具
 */
public class ByteUtils {
    public static int bytes2Int(byte[] bytes, int start, int len) {
        int sum = 0;
        int end = start + len;
        for (int i = start; i < end; i++) {
            int n = ((int) bytes[i]) & 0xff;
            n <<= (--len) * 8;
            sum = n + sum;
        }
        return sum;
    }

    public static byte[] int2Bytes(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[len - i - 1] = (byte) ((value >> 8 * i) & 0xff);
        }
        return b;
    }

    public static String bytes2String(byte[] b, int start, int len) {
        return new String(b, start, len);
    }

    public static byte[] string2Bytes(String str) {
        return str.getBytes();
    }

    /**
     * 用字节数组replaceBytes替换指定字节数组oldBytes中指定位置的值。
     * @param oldBytes
     * @param offset
     * @param len
     * @param replaceBytes
     * @return
     */
    public static byte[] byteReplace(byte[] oldBytes, int offset, int len, byte[] replaceBytes) {
        byte[] newBytes = new byte[oldBytes.length + replaceBytes.length - len];
        System.arraycopy(oldBytes, 0, newBytes, 0, offset);
        System.arraycopy(replaceBytes, 0, newBytes, offset, replaceBytes.length);
        System.arraycopy(oldBytes, offset + len, newBytes,
                offset + replaceBytes.length,oldBytes.length - offset - len);
        return newBytes;
    }
}
