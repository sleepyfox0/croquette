package croquette.ui.paint

import croquette.ui.BLACK
import croquette.ui.RED
import java.awt.Graphics2D
import javax.swing.JPanel

class TimePainter(canvas: JPanel) : Painter(canvas) {

    var total = 0
    var current = 0

    override fun paint(g: Graphics2D) {
        g.color = BLACK
        g.fillRect(0, 0, canvas.width, canvas.height)

        val percent = 1.0 - (current.toDouble() / total.toDouble())
        val size = (canvas.width * percent).toInt()
        g.color = RED
        g.fillRect(0, 0, size, canvas.height)
    }
}