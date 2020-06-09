package com.fjminbao.task.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: xianyuanLi
 * @Date: created in 17:22 2019/12/19
 * Descrpition:
 */
// 线程池配置,可根据具体的需要配置线程池相关配置
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    /**
     * 配置线程池（线程池使用默认的配置）
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //设置核心线程数
        taskExecutor.setCorePoolSize(24);
        //设置最大线程数
        taskExecutor.setMaxPoolSize(256);
        //设置队列容量
        taskExecutor.setQueueCapacity(512);
        //设置线程池活跃时间
        taskExecutor.setKeepAliveSeconds(300);
        //设置默认线程名称
        taskExecutor.setThreadNamePrefix("helloTask-");
        //设置拒绝测了
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return taskExecutor;
    }
}
