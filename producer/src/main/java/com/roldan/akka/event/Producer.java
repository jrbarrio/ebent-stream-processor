package com.roldan.akka.event;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Producer {

    public static void main(String[] args) {
        Config conf = ConfigFactory.load();
        String queueUrl = conf.getString("sqs.queue.url");

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl
                )
                .withMessageBody("hello world")
                .withDelaySeconds(5);

        sqs.sendMessage(send_msg_request);
    }
}