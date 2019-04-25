package cn.gq.executerdemo.execute;

import org.aspectj.lang.annotation.SuppressAjWarnings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * JavaClass 执行工具
 */
public class JavaClassExecuter {
    /**
     * 执行外部传来的一个代表Java类的byte数组
     * 将输入的数组中代表java.lang.System的CONSTANT_Utf8_info常量修改为替换后的HackSystem类
     * 执行main方法，输出结果为该类向System.out/System.err输出的信息
     */
    public static String execute(byte[] classByte,String systemIn){
        //传入需要修改的字节数组
        ClassModifier classModifier = new ClassModifier(classByte);

        //替换System和Scanner
        byte[] modiBytes = classModifier.modifyUTF8Constant("java/lang/System", "cn/gq/executerdemo/execute/HackSystem");
        modiBytes = classModifier.modifyUTF8Constant("java/util/Scanner", "cn/gq/executerdemo/execute/HackScanner");

        //获取用户输入
        ((HackInputStream)HackSystem.in).set(systemIn);

        HotSwapClassLoader classLoader = new HotSwapClassLoader();
        Class clazz = classLoader.loadByte(modiBytes);

        try{
            Method mainMethod = clazz.getMethod("main",new Class[] {String[].class});
            mainMethod.invoke(null,new String[]{null});
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (InvocationTargetException e){
            e.getCause().printStackTrace(HackSystem.err);
        }

        String result = HackSystem.getBufferString();
//        System.out.print(result + " :-------------------------------------------------");
        HackSystem.closeBuffer();
        return result;
    }
}
