package com.lexor.service.queue;

import com.lexor.controller.QuickBookController;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class QueueService {

    private static final java.util.logging.Logger LOG = Logger.getLogger (QueueService.class.getName());

    private static final BlockingQueue<String> QUEUE =  new LinkedBlockingQueue<>();

    ExecutorService executorService;
    public QueueService() throws IOException {
         executorService = Executors.newSingleThreadExecutor();

    }


    public void add(String payload) throws IOException {

        // add payload to the queue
        QUEUE.add(payload);
        LOG.info("added to queue:::: queue size " + QUEUE.size());

        //Call executor service
        executorService.submit(new QueueProcessor());
        executorService.shutdown();
    }

    public static BlockingQueue<String> getQueue() {
        return QUEUE;
    }

}
