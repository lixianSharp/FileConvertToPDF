package com.fjminbao.task.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName TaskAsyncConfig
 * @Description: TODO
 * @Author lxy
 * @Date 2020/7/2
 * @Version V1.0
 **/

@Configuration
@EnableAsync
public class TaskAsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //定制线程名称，还可以定制线程group
        executor.setThreadFactory(new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                //重新定义一个名称
                Thread t = new Thread(Thread.currentThread().getThreadGroup(), r,
                        "async-task-all" + threadNumber.getAndIncrement(),
                        0);
                return t;
            }
        });
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setKeepAliveSeconds(5);
        executor.setQueueCapacity(100);
//        executor.setRejectedExecutionHandler(null);
        executor.initialize();
        return executor;
    }

//    @Override
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//        return new AsyncUncaughtExceptionHandler() {
//            @Override
//            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
//                System.out.println("do exception by myself");
//            }
//        };
//    }

}
