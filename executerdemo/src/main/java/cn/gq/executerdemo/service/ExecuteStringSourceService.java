package cn.gq.executerdemo.service;

import cn.gq.executerdemo.compile.StringSourceCompiler;
import cn.gq.executerdemo.execute.JavaClassExecuter;
import org.springframework.stereotype.Service;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.util.List;
import java.util.concurrent.*;

@Service
public class ExecuteStringSourceService {

    private static final int RUN_TIME_LIMITED = 15;

    private static final int N_THREAD = 5;

    private static final ExecutorService threadPool = new ThreadPoolExecutor(N_THREAD, N_THREAD,
            60L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(N_THREAD));

    private static final String WAIT_WARNING = "服务器忙，请稍后提交";
    private static final String NO_OUTPUT = "Nothing";

    public String execute(String source, String systemIn) {
        DiagnosticCollector<JavaFileObject> compileCollector = new DiagnosticCollector<>();

        //获取编译结果
        byte[] classBytes;
        try {
            classBytes = StringSourceCompiler.compile(source, compileCollector);
        } catch (IllegalArgumentException e) {
            return "No valid class";
        }
        //如果编译不通过则打印错误信息
        if (classBytes == null) {
            List<Diagnostic<? extends JavaFileObject>> compileError = compileCollector.getDiagnostics();
            StringBuilder compileErrorRes = new StringBuilder();
            for (Diagnostic diagnostic : compileError) {
                compileErrorRes.append("Compilation error at ");
                compileErrorRes.append(diagnostic.getLineNumber());
                compileErrorRes.append(".");
                compileErrorRes.append(System.lineSeparator());
            }
            return compileErrorRes.toString();
        }

        //编译通过，运行字节码的main()方法
        Callable<String> runTask = new Callable<String>() {
            @Override
            public String call() throws Exception {
//                System.out.print("编译完成开始加载");
                return JavaClassExecuter.execute(classBytes, systemIn);
            }
        };

        Future<String> res = null;
        try {
            res = threadPool.submit(runTask);
        } catch (RejectedExecutionException e) {
            return WAIT_WARNING;
        }

        //获取运行结果，处理非客户端代码错误
        String runResult;
        try {
            runResult = res.get(RUN_TIME_LIMITED, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            runResult = "Program interrupted";
        } catch (ExecutionException e) {
            runResult = e.getCause().getMessage();
        } catch (TimeoutException e) {
            runResult = "Time Limit Exceeded";
        } finally {
            res.cancel(true);
        }
        return runResult != null ? runResult : NO_OUTPUT;
    }

}
























