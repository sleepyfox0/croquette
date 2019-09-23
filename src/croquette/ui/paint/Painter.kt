package croquette.ui.paint

import croquette.ui.BLACK
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JPanel

abstract class Painter(val canvas: JPanel) {

    var db = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)

    fun prePaint() {
        if (canvas.width > db.width) {
            db = BufferedImage(canvas.width, db.height, BufferedImage.TYPE_INT_ARGB)
        }

        if (canvas.height > db.height) {
            db = BufferedImage(db.width, canvas.height, BufferedImage.TYPE_INT_ARGB)
        }

        val g = canvas.graphics
        val dg = db.graphics
        if (g != null) {
            if (dg is Graphics2D) {
                paint(dg)
            }
            g.drawImage(db, 0, 0, null)
        }
    }

    abstract fun paint(g: Graphics2D)
}