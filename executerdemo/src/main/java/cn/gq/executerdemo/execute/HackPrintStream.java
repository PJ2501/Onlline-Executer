package cn.gq.executerdemo.execute;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;

/**
 * 为了能够同时运行多个客户端，原本的PrintStream只装饰了一个输出流，并且将所有的输出写入一个流中，不符合项目要求
 * 因此必须重写PrintStream,使用ThreadLocal为每一个线程维护一个私有的输出流，并且重写PrintStream中有关操作流的方法
 * 以及所有的public方法
 */
public class HackPrintStream extends PrintStream {

    private ThreadLocal<ByteArrayOutputStream> out;
    private ThreadLocal<Boolean> trouble;//每个标准输出流执行过程中是否拋异常

    public HackPrintStream() {
        super(new ByteArrayOutputStream());
        out = new ThreadLocal<>();
        trouble = new ThreadLocal<>();
    }

    @Override
    public String toString() {
        return out.get().toString();
    }

    /**
     * Check to make sure that the stream has not been closed
     */
    private void ensureOpen() throws IOException {
        if (out.get() == null)
            out.set(new ByteArrayOutputStream());
    }

    public void flush() {
        try {
            ensureOpen();
            out.get().flush();
        } catch (IOException e) {
            trouble.set(true);
        }
    }

    @Override
    public void close() {
        try {
            out.get().close();
        } catch (IOException x) {
            trouble.set(true);
        }
        out.remove();
    }

    public boolean checkError() {
        if (out.get() != null)
            flush();
        return trouble.get() != null ? trouble.get() : false;
    }

    public void setError() {
        trouble.set(true);
    }

    public void clearError() {
        trouble.remove();
    }

    public void write(int b) {
        try {
            ensureOpen();
            out.get().write(b);
            if ((b == '\n')) {
                out.get().flush();
            }
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble.set(true);
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        try {
            ensureOpen();
            out.get().write(buf, off, len);
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble.set(true);
        }
    }

    private void write(char buf[]) {
        try {
            ensureOpen();
            out.get().write(new String(buf).getBytes());
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble.set(true);
        }
    }

    private void write(String s) {
        try {
            ensureOpen();
            out.get().write(s.getBytes());
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble.set(true);
        }
    }
    private void newLine(){
        try{
            ensureOpen();
            out.get().write(System.lineSeparator().getBytes());
        }catch (InterruptedIOException x){
            Thread.currentThread().interrupt();
        }catch (IOException x){
            trouble.set(true);
        }
    }
    public void print(boolean b){
        write(b ? "true" : "false");
    }
    public void print(char c){
        write(String.valueOf(c));
    }
    public void print(int i){
        write(String.valueOf(i));
    }
    public void print(long l){
        write(String.valueOf(l));
    }
    public void print(float f) {
        write(String.valueOf(f));
    }
    public void print(double d) {
        write(String.valueOf(d));
    }
    public void print(char s[]) {
        write(s);
    }
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        write(s);
    }
    public void print(Object obj) {
        write(String.valueOf(obj));
    }
    public void println() {
        newLine();
    }
    public void println(boolean x) {
        print(x);
        newLine();
    }
    public void println(char x) {
        print(x);
        newLine();
    }
    public void println(int x) {
        print(x);
        newLine();
    }
    public void println(long x) {
        print(x);
        newLine();
    }
    public void println(float x) {
        print(x);
        newLine();
    }
    public void println(double x) {
        print(x);
        newLine();
    }
    public void println(char x[]) {
        print(x);
        newLine();
    }
    public void println(String x) {
        print(x);
        newLine();
    }
    public void println(Object x) {
        String s = String.valueOf(x);
        print(s);
        newLine();
    }
}


























