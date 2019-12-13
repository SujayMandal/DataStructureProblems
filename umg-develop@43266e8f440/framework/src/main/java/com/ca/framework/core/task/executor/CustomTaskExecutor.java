package com.ca.framework.core.task.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class CustomTaskExecutor extends ThreadPoolTaskExecutor {

    private static final long serialVersionUID = 91227304701055102L;

    public <T> List<Future<T>> runTask(List<? extends Callable<T>> tasks) {
        List<Future<T>> finalList = new ArrayList<>();
        for (Callable<T> task : tasks) {
            Future<T> obj = submit(task);
            finalList.add(obj);
        }
        return finalList;
    }

}
