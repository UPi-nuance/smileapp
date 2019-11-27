package smileapp

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.google.protobuf.ByteString
import io.micronaut.context.annotation.Value
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
class SmileService(
        @Value("\${smileapp.apiKey}") private val faceApiKey: String,
        @Value("\${smileapp.endpoint}") private val faceApiEndpoint: String
) {
    init {
        require(faceApiKey != "WhatIsMyApikey") { "The faceApiKey is the default value, you should set MICRONAUT_APPLICATION_JSON as {\"smileapp\": { \"apiKey\": \"818d5136733349908613fc791dd0ce54\", \"endpoint\": \"https://wad-face-api.cognitiveservices.azure.com/\" } }"}
        require(faceApiEndpoint != "https://whatismyendpoint.cognitiveservices.azure.com/")
    }

    companion object {
        val emojiMap = mapOf(
                Mood.anger to "\uD83D\uDE20",
                Mood.contempt to "\uD83D\uDE24",
                Mood.disgust to "\uD83E\uDD22",
                Mood.fear to "\uD83D\uDE31",
                Mood.happiness to "\uD83D\uDE0A",
                Mood.neutral to "\uD83D\uDE10",
                Mood.sadness to "\uD83D\uDE41",
                Mood.surprise to "\uD83D\uDE2E"
        )
    }

    private fun fetchFaceFromAzure(body: ByteString) : String {
        val uri = "/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&returnFaceAttributes=emotion&recognitionModel=recognition_01&returnRecognitionModel=false&detectionModel=detection_01"
        val url = URL("$faceApiEndpoint$uri")
        val con = url.openConnection()
        val http = con as HttpURLConnection

        http.doOutput = true
        http.requestMethod = "POST"
        http.setRequestProperty("Content-Type", "application/octet-stream");
        http.setRequestProperty("Ocp-Apim-Subscription-Key", faceApiKey)
        http.setFixedLengthStreamingMode(body.size())

        val out = http.outputStream
        body.writeTo(out)
        out.flush()
        out.close()

        println("Fetching $uri")
        val status = con.responseCode
        println("Fetch complete: $status: ${con.responseMessage}")
        if (status >= 300) {
            println("Error fetching $uri: $status")
            throw Exception("Error fetching $uri: $status, ${con.responseMessage}")
        }

        // Read the response body

        val reader = BufferedReader(
                InputStreamReader(con.getInputStream()))
        val content = StringBuffer()
        while (true) {
            val inputLine = reader.readLine() ?: break
            content.append(inputLine)
        }
        val response = content.toString()

        reader.close()
        con.disconnect()

        println("Response body: $response")
        return response
    }

    private fun interpretFaceApiJson(data: String) : FaceData {
        val node = ObjectMapper().readTree(data)
        if (node.size() == 0) {
            throw Exception("Face not found")
        }
        val first = node[0]
        val rect = first["faceRectangle"]

        val mood = findMostLikelyMood(first["faceAttributes"]["emotion"])

        return FaceData(
                mood,
                rect["top"].intValue(),
                rect["left"].intValue(),
                rect["width"].intValue(),
                rect["height"].intValue()
        )
    }

    private fun findMostLikelyMood(emotion: JsonNode) : Mood {
        var topMood = Mood.surprise
        var topMoodValue = Double.MIN_VALUE

        enumValues<Mood>().forEach {
            val value = emotion[it.toString()]
            if (value.nodeType == JsonNodeType.NUMBER) {
                val moodValue = value.asDouble()
                println("$it => $moodValue")
                if (moodValue > topMoodValue) {
                    topMoodValue = moodValue
                    topMood = it
                }
            }
        }

        return topMood
    }

    fun detectMood(contentType: String, body: ByteString) : FaceData {
        val json = fetchFaceFromAzure(body)
        return interpretFaceApiJson(json)
    }

    fun moodToEmoji(mood: Mood) : String {
        return emojiMap[mood] ?: throw Exception("Mood not found")
    }
}
