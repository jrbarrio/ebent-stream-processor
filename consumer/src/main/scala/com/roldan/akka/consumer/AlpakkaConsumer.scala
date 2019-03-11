package com.roldan.akka.consumer

import java.net.URI

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.sqs.scaladsl.{SqsAckSink, SqsSource}
import akka.stream.alpakka.sqs.{MessageAction, SqsSourceSettings}
import com.typesafe.config.ConfigFactory
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient

import scala.concurrent.duration._

object AlpakkaConsumer extends App {

  val conf = ConfigFactory.load
  val queueUrl = conf.getString("sqs.queue.url")

  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()

  implicit val awsSqsClient = SqsAsyncClient
    .builder()
    .credentialsProvider(ProfileCredentialsProvider.create())
    .endpointOverride(URI.create(queueUrl))
    .region(Region.EU_WEST_1)
    .build()

  system.registerOnTermination(awsSqsClient.close())

  val settings = SqsSourceSettings()
    .withCloseOnEmptyReceive(false)
    .withWaitTime(0.second)

  SqsSource(queueUrl, settings)
    .map(message => {
      println(message.body())
      message
    })
    .map(MessageAction.Delete(_))
    .runWith(SqsAckSink(queueUrl))
}