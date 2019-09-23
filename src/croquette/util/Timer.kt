package croquette.util

class Timer () {

    var time = 0

    fun startTimer(cb: () -> Unit, cbu: (Int) -> Unit) {
        var startTime = System.nanoTime()
        var currentTime: Long = 0
        val thread = Thread {
            while ((currentTime / 1000000) < time) {
                val ct = System.nanoTime()
                currentTime += (ct - startTime)
                startTime = ct
                cbu((currentTime / 1000000).toInt())
                Thread.sleep(5)
            }
            cb()
        }
        thread.start()
    }

}