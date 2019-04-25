package cn.gq.executerdemo.execute;

/**
 * 为了多次载入执行类而加入的加载器
 * 设计一个loadByte()方法将defineClass()方法开放出来，只有我们调用loadByte()方法时才使用自己的类加载器
 * 虚拟机调用HotSwapClassLoader时还是按照双亲委派模型使用loadClass方法进行类加载
 */
public class HotSwapClassLoader extends ClassLoader{
    //使用指定的父类加载器创建一个新的类加载器进行委派
    public HotSwapClassLoader(){
        super(HotSwapClassLoader.class.getClassLoader());
    }
    public Class loadByte(byte[] classByte){
        return defineClass(null,classByte,0,classByte.length);
    }
}
