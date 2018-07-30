package com.os.vee.utils.scheduler;

import io.reactivex.Scheduler;

public interface BaseSchedulerProvider {

    public Scheduler computation();

    public Scheduler io();

    public Scheduler main();
}