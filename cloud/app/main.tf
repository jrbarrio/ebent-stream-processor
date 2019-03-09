terraform {
  backend "s3" {
    bucket = "jorgeroldanbarrio.terraform.state"
    region = "eu-west-1"
    key = "event-stream-processor/terraform.tfstate"
  }
}

provider "aws" {
  region = "${var.region}"
}

resource "aws_sqs_queue" "sqs_queue" {
  name = "${var.app}-sqs-queue"

  tags {
    App = "${var.app}"
  }
}

resource "aws_sqs_queue_policy" "sqs_queue" {
  queue_url = "${aws_sqs_queue.sqs_queue.id}"

  policy = <<POLICY
  {
    "Version": "2012-10-17",
    "Id": "sqspolicy",
    "Statement": [
      {
        "Sid": "First",
        "Effect": "Allow",
        "Principal": "*",
        "Action": "sqs:SendMessage",
        "Resource": "${aws_sqs_queue.sqs_queue.arn}",
        "Condition": {
          "ArnEquals": {
            "aws:SourceArn": "${aws_sqs_queue.sqs_queue.arn}"
          }
        }
      }
    ]
  }
  POLICY
}


