package com.xiekong.scheduler.config;

import com.xiekong.scheduler.handler.SolrDataImportHandler;
import com.xiekong.scheduler.http.HttpClientHelper;
import com.xiekong.scheduler.solr.SolrConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author XieKong
 * @date 2019/7/9 14:56
 */
@Slf4j
@Configuration
@EnableScheduling
public class SolrSchedulingConfig implements SchedulingConfigurer {
    @Value("${scheduler.pool-size}")
    private int poolSize;
    @Autowired
    SolrConfig solrConfig;
    @Autowired
    private HttpClientHelper httpClientHelper;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(taskScheduler());
        if (solrConfig.getCores() != null) {
            solrConfig.getCores().forEach(solrCore -> {
                scheduledTaskRegistrar.addTriggerTask(
                        //1.添加任务内容(Runnable)
                        new SolrDataImportHandler(httpClientHelper, solrCore),
                        //2.设置执行周期(Trigger)
                        triggerContext -> {
                            PeriodicTrigger trigger = new PeriodicTrigger(solrCore.getImportInterval(), TimeUnit.SECONDS);
//                            trigger.setFixedRate(true);
                            return trigger.nextExecutionTime(triggerContext);
                        }
                );

//                if (solrCore.getReIndexInterval() > 0) {
//                    scheduledTaskRegistrar.addTriggerTask(
//                            new SolrReIndexHandler(httpClientHelper, solrCore),
//                            triggerContext -> {
//                                PeriodicTrigger trigger = new PeriodicTrigger(solrCore.getImportInterval(), TimeUnit.SECONDS);
//                                return trigger.nextExecutionTime(triggerContext);
//                            }
//                    );
//                }
            });
        }
    }

    /**
     * 定时任务使用的线程池
     * @return
     */
    @Bean("taskScheduler")
    public Executor taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolSize);
        // 调度器shutdown被调用时等待当前被调度的任务完成
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时长
        scheduler.setAwaitTerminationSeconds(100);
        scheduler.setThreadNamePrefix("solr-task-");
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return scheduler;
    }
}
