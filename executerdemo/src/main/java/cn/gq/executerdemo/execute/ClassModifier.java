package cn.gq.executerdemo.execute;

/**
 * 将java.lang.System替换为我们自己的HackSystem类
 */
public class ClassModifier {
    //Class文件中常量池的起始偏移
    private static final int CONSTANT_POOL_COUNT_INDEX = 8;
    //CONSTANT_Utf8_info 常量的tag标志
    private static final int CONSTANT_Utf8_info = 1;
    /**
     * 常量池中11中常量的长度，CONSTANT_Utf8_info除外，因为它不是定长的。
     * 此外，CONSTANT_MethodType_info,CONSTANT_MethodHandle_info,CONSTANT_InvokeDynamic_info
     * 这三项只在极特别的情况会用到，因此长度标识为-1
     */
    private static final int[] CONSTANT_ITEM_LENGTH = {-1, -1, -1, 5, 5, 9, 9, 3, 3, 5, 5, 5, 5};
    /**
     * u1代表tag，u2代表length
     */
    private static final int u1 = 1;
    private static final int u2 = 2;

    private byte[] classByte;
    public ClassModifier(byte[] classByte){
        this.classByte = classByte;
    }
    /**
     * 获取常量池中常量的数量
     * 返回值为常量池中项数+1
     */
    public int getConstantPoolCount(){
        return ByteUtils.bytes2Int(classByte,CONSTANT_POOL_COUNT_INDEX,u2);
    }
    /**
     * 修改常量池中CONSTANT_Utf8_info常量的内容
     */
    public byte[] modifyUTF8Constant(String oldStr,String newStr){
        int cpc = getConstantPoolCount();
        //常量池入口位置
        int offset = CONSTANT_POOL_COUNT_INDEX + u2;
        for(int i = 1;i < cpc;i++){
            int tag = ByteUtils.bytes2Int(classByte,offset,u1);
            if(tag == CONSTANT_Utf8_info){
                //计算CONSTANT_Utf8_info中内容的长度
                int len = ByteUtils.bytes2Int(classByte,offset + u1,u2);
                //下标移动到CONSTANT_Utf8_info的内容部分
                offset += u1 + u2;
                //读取内容
                String str = ByteUtils.bytes2String(classByte,offset,len);
                if(str.equals(oldStr)){
                    //获取用字节数组表示的指定的新字符串的内容以及长度，并替换到classByte中的相应位置
                    byte[] strBytes = ByteUtils.string2Bytes(newStr);
                    byte[] strLen = ByteUtils.int2Bytes(strBytes.length,u2);
                    classByte = ByteUtils.byteReplace(classByte,offset - u2,u2,strLen);
                    classByte = ByteUtils.byteReplace(classByte,offset,len,strBytes);
                    return classByte;
                }else {
                    offset += len;
                }
            }else{
                offset += CONSTANT_ITEM_LENGTH[tag];
            }
        }
        return classByte;
    }
}

















