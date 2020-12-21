package com.lexor.service.queue;

import com.lexor.config.ConfigProperties;
import com.lexor.service.KafkaServiceImp;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class QueueProcessor implements Callable<Object> {

    private static final Logger LOG = Logger.getLogger (QueueProcessor.class.getName());



    Properties config = ConfigProperties.loadConfig();

    final String topic = config.getProperty("topic");


    public QueueProcessor() throws IOException {
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Object call() throws Exception {
        while (!QueueService.getQueue().isEmpty()) {
            //remove item from queue
            String payload = QueueService.getQueue().poll();
            LOG.info("processing payload: Queue Size:" + QueueService.getQueue().size());
            try {
                KafkaServiceImp.getInstance().sendKafkaMessage(topic,"event",payload);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
