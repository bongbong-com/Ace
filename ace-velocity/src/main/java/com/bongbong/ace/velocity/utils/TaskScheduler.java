package com.bongbong.ace.velocity.utils;

import com.velocitypowered.api.scheduler.Scheduler;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class TaskScheduler {
    final Object plugin;
    final Scheduler scheduler;

    public void runTask(Runnable runnable) {
        scheduler.buildTask(plugin, runnable).schedule();
    }

    public void runTaskDelay(int seconds, Runnable runnable) {
        scheduler.buildTask(plugin, runnable).delay(seconds, TimeUnit.SECONDS).schedule();
    }

}
