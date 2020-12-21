package com.lexor.service;

import com.lexor.config.ConfigProperties;
import com.lexor.service.queue.QueueProcessor;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;


/**
 *
 * @author auphan
 */
public class KafkaServiceImp implements IKafkaService{


    private static final Logger LOG = Logger.getLogger (QueueProcessor.class.getName());

    private final Producer<String, String> producer;
    public static KafkaServiceImp instance = null;
    public static KafkaServiceImp getInstance() throws IOException {
        return new KafkaServiceImp();
    }
    static {
        try {
            LOG.info("new instance KafkaServiceImp");
            instance = new KafkaServiceImp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private KafkaServiceImp() throws IOException {
        Properties properties = ConfigProperties.loadConfig();
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getProperty("bootstrap.servers"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.ACKS_CONFIG, properties.getProperty("acks"));
        props.put(ProducerConfig.RETRIES_CONFIG, properties.getProperty("retries"));
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, properties.getProperty("batch.size"));
        props.put(ProducerConfig.LINGER_MS_CONFIG, properties.getProperty("linger.ms"));
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, properties.getProperty("buffer.memory"));
        props.put("transactional.id", "id");
        producer = new KafkaProducer<>(props);

    }

    @Override
    public void createTopics(String bootstrapServers) {
        Properties properties=new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG,10000);
        properties.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG,5000);
        try(Admin adminClient = AdminClient.create(properties)){

            CreateTopicsResult createTopicsResult=adminClient.createTopics(
                    Collections.singletonList(
                            new NewTopic("middle-message",1,(short)1)
                    )
            );
        }
    }

    @Override
    public boolean sendKafkaMessage(String topic, String key, String payload) {
        System.out.println(payload);
        producer.initTransactions();
        try {
            producer.beginTransaction();
                producer.send(new ProducerRecord<>(ConfigProperties.loadConfig().getProperty("topic") , key,payload));
            producer.commitTransaction();
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
            return false;
        } catch (KafkaException e) {
            // For all other exceptions, just abort the transaction and try again.
            producer.abortTransaction();
            return false;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
        return true;

    }
}
