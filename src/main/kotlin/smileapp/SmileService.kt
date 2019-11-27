package smileapp

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.google.protobuf.ByteString
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Singleton
import java.io.BufferedReader
import java.io.InputStreamReader


enum class Mood {
    anger,
    contempt,
    disgust,
    fear,
    happiness,
    neutral,
    sadness,
    surprise
}

data class FaceData(
        val mood: Mood,
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int
)


@Singleton
class SmileService {

    fun detectMood(image: ByteString) : FaceData {
        throw NotImplementedError()
    }

    fun moodToEmoji(mood: Mood) : String {
        throw Exception("Mood not found")
    }
}
