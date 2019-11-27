package smileapp

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("smileapp")
                .mainClass(Application.javaClass)
                .start()
    }
}