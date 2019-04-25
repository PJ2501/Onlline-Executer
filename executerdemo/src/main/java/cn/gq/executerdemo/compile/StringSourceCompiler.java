package cn.gq.executerdemo.compile;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义编译模块
 */
public class StringSourceCompiler {
    private static Map<String,JavaFileObject> fileObjectMap = new ConcurrentHashMap<>();

    //预编译正则表达式,用来匹配源码字符串中的类名
    private static Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");

    public static byte[] compile(String source, DiagnosticCollector<JavaFileObject> compileCollector){
        //获取Java语言编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        /**
         * 通过以下两步获得自己的JavaFIleManager
         */
        //获取Java语言编译器的标准文件管理器实现的新实例
        JavaFileManager j = compiler.getStandardFileManager(compileCollector,null,null);
        //将文件管理器传入FordingJavaFileManager的构造器，获得一个我们自定义的JavaFileManager
        JavaFileManager javaFileManager = new MyJavaFileManager(j);

        //从源码字符串中匹配类名
        Matcher matcher = CLASS_PATTERN.matcher(source);
        String className;
        if(matcher.find()) {
            className = matcher.group(1);
        }else {
            throw new IllegalArgumentException("No valid class");
        }

        //将源码字符串封装为一个sourceJavaFileObject，供自定义的编译器使用
        JavaFileObject sourceJavaFileObject = new MyJavaFileObject(className,source);
        /**
         * 1、编译器得到源码，进行编译，得到字节码，源码封装在sourceJavaFIleObject中
         * 2、通过调用JavaFileManager的getJavaFileForOutput()方法创建一个MyJavaFileObject对象，用于存放编译生成的字节码
         *       |----->然后将存放了字节码的JavaFileObject放在Map<className,JavaFileObject>中，以便后面取用。
         * 3、通过类名从map中获取到存放字节码的MyJavaFileObject
         * 4、通过MyJavaFileObject对象获取到存放编译结果的输出流
         * 5、调用getCompiledBytes()方法将输出流内容转换为字节数组
         */
        //开始执行编译，通过传入自己的JavaFileManager为编译器创建存放字节码的JavaFIleObject对象
        Boolean result = compiler.getTask(null,javaFileManager,compileCollector,
                null,null, Arrays.asList(sourceJavaFileObject)).call();
        //3、
        JavaFileObject byteJavaFileObject = fileObjectMap.get(className);
        if(result && byteJavaFileObject != null){
            //4、5、
//            System.out.print("获取到字节码数组");
//            String bytes = new String(((MyJavaFileObject)byteJavaFileObject).getCompiledBytes());
//            System.out.print(bytes);
            return ((MyJavaFileObject)byteJavaFileObject).getCompiledBytes();
        }
        return null;
    }

    /**
     * 用于管理JavaFileObject
     */
    public static class MyJavaFileManager extends ForwardingJavaFileManager<JavaFileManager>{
        public MyJavaFileManager(JavaFileManager javaFileManager){
            super(javaFileManager);

        }

        @Override
        public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
            JavaFileObject javaFileObject = fileObjectMap.get(className);
            if(javaFileObject == null){
                return super.getJavaFileForInput(location,className,kind);
            }
            return javaFileObject;
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            JavaFileObject javaFileObject = new MyJavaFileObject(className,kind);
            fileObjectMap.put(className,javaFileObject);
            return javaFileObject;
        }

    }

    /**
     * 用于封装表示源码与字节码的对象
     */
    public static class MyJavaFileObject extends SimpleJavaFileObject{
        private String source;
        private ByteArrayOutputStream byteArrayOutputStream;

        /**
         * 构造用于存放源代码的对象
         * @param name
         * @param source
         */
        public MyJavaFileObject(String name,String source){
            super(URI.create("String:///" + name + Kind.SOURCE.extension),Kind.SOURCE);
            this.source = source;
        }
        /**
         * 构建用于存放字节码的JavaFileObject
         */
        public MyJavaFileObject(String name,Kind kind){
            super(URI.create("String:///" + name + Kind.SOURCE.extension),kind);
        }

        /**
         * 获取源代码字符序列
         * @param ignoreEncodingErrors
         * @return
         * @throws IOException
         */
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            if(source == null)
                throw new IllegalArgumentException("source == null");
            return source;
        }

        /**
         * 得到JavaFileObject中用于存放字节码的输出流
         * @return
         * @throws IOException
         */
        @Override
        public OutputStream openOutputStream() throws IOException {
            byteArrayOutputStream = new ByteArrayOutputStream();
            return byteArrayOutputStream;
        }

        /**
         * 将输出流的内容转化为byte数组
         * @return
         */
        public byte[] getCompiledBytes(){
            return byteArrayOutputStream.toByteArray();
        }
    }
}






















