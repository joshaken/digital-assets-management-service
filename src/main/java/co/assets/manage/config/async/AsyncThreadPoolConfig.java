package co.assets.manage.config.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncExecutionAspectSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
@EnableAsync
@Configuration
public class AsyncThreadPoolConfig {


    /**
     * custom thread pool
     *
     * @return executor
     */
    @Bean(name = AsyncExecutionAspectSupport.DEFAULT_TASK_EXECUTOR_BEAN_NAME)
    public Executor getAsyncExecutor() {
        int cpuCore = Runtime.getRuntime().availableProcessors();
        log.info("cpu core : {}", cpuCore);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cpuCore * 2);
        executor.setMaxPoolSize(cpuCore * 4);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("custom-async-");
        executor.setWaitForTasksToCompleteOnShutdown(false);

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        return executor;
    }

}
