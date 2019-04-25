package cn.gq.executerdemo.execute;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HackInputStream extends InputStream {
    public static final ThreadLocal<InputStream> privateInputStream = new ThreadLocal<>();
    @Override
    public int read() throws IOException {
        return 0;
    }
    public void set(String systemIn){
        privateInputStream.set(new ByteArrayInputStream(systemIn.getBytes()));
    }
    public InputStream get(){
        return privateInputStream.get();
    }
    @Override
    public void close(){
        privateInputStream.remove();
    }
}
