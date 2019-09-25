package croquette.util

class Timer () {

    var time = 0
    var isRunning = false
    var isPaused = false

    fun startTimer(cb: () -> Unit, cbu: (Int) -> Unit) {
        isRunning = true
        var startTime = System.nanoTime()
        var currentTime: Long = 0
        val thread = Thread {
            while ((currentTime / 1000000 ) < time && isRunning) {
                val ct = System.nanoTime()
                if (!isPaused) {
                    currentTime += (ct - startTime)
                    cbu((currentTime / 1000000).toInt())
                }
                startTime = ct
                Thread.sleep(5)
            }
            cb()
        }
        thread.start()
    }

}