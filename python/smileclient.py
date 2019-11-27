#!/usr/bin/python

from __future__ import print_function
import logging

import grpc

import smileservice_pb2
import smileservice_pb2_grpc


def run():
    # NOTE(gRPC Python Team): .close() is possible on a channel and should be
    # used in circumstances in which the with statement does not fit the needs
    # of the code.

    with open('peter_ungar.jpg', 'rb') as content_file:
        content = content_file.read()

    with grpc.insecure_channel('localhost:50051') as channel:
        stub = smileservice_pb2_grpc.SmileServiceStub(channel)
        response = stub.detectMood(smileservice_pb2.MoodRequest(contentType='image/jpeg', body=content))
    print("Smile service client received: " + response.mood + " ~ " + response.emoji + " ~ rect = " + str(response.rect))


if __name__ == '__main__':
    logging.basicConfig()
    run()
