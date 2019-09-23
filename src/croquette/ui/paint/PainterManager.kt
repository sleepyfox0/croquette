package croquette.ui.paint

class PainterManager {

    var isRunning = true
    private val painters = mutableListOf<Painter>()

    fun add(p: Painter) {
        painters.add(p)
    }

    fun go() {
        val t = Thread {
            while (isRunning) {
                painters.forEach {
                    it.prePaint()
                }
                Thread.sleep(5)
            }
        }

        t.start()
    }

}