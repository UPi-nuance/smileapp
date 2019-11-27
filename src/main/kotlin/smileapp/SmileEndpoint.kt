package smileapp

import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
class SmileEndpoint(
        private val smileService : SmileService
) : SmileServiceGrpc.SmileServiceImplBase() {

    override fun detectMood(request: MoodRequest, responseObserver: StreamObserver<MoodReply>) {
        val faceData = smileService.detectMood(request.body)
        val emoji = smileService.moodToEmoji(faceData.mood)
        val reply = MoodReply.newBuilder()
                .setMood(faceData.mood.toString())
                .setEmoji(emoji)
                .setRect(Rect.newBuilder()
                        .setX(faceData.x)
                        .setY(faceData.y)
                        .setWidth(faceData.width)
                        .setHeight(faceData.height)
                        .build()
                )
                .build()

        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }
}
