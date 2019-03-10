package com.roldan.akka.consumer

import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.typesafe.config.ConfigFactory

object Consumer extends App {

  val conf = ConfigFactory.load
  val queueUrl = conf.getString("sqs.queue.url")

  val sqs = AmazonSQSClientBuilder.defaultClient
  val messages = sqs.receiveMessage(queueUrl).getMessages

  messages.forEach(message =>{
    println(message.getBody)
    sqs.deleteMessage(queueUrl, message.getReceiptHandle)
  })
}
