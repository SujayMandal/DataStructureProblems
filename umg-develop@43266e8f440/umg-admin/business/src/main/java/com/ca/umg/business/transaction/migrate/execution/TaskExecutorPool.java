package com.ca.umg.business.transaction.migrate.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskExecutorPool {

    private ExecutorService executorService;

    public TaskExecutorPool() {
        super();
    }

    public TaskExecutorPool(Integer poolSize) {
        executorService = Executors.newFixedThreadPool(poolSize);
    }

    public void shutDown() {
        executorService.shutdown();
    }

    public <T> List<Future<T>> runTask(List<? extends Callable<T>> tasks) {
        List<Future<T>> finalList = new ArrayList<>();
        for (Callable<T> task : tasks) {
            Future<T> obj = executorService.submit(task);
            finalList.add(obj);
        }
        return finalList;
    }
}