package croquette.ui

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.swing.JPanel
import kotlin.math.floor

class ImagePanel : JPanel(null, true) {

    var img = BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY)

    init {
        background = BLACK
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (g is Graphics2D) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            var zoom: Double
            var wx = img.width.toDouble()
            var wy = img.height.toDouble()

            if (wx > wy) {
                zoom = width.toDouble() / wx
                wx = width.toDouble()
                wy *= zoom

                if (wy > height) {
                    zoom = height.toDouble() / img.height.toDouble()
                    wy = height.toDouble()
                    wx = img.width * zoom
                }
            } else {
                zoom = height.toDouble() / wy
                wy = height.toDouble()
                wx *= zoom

                if (wx > width) {
                    zoom = width.toDouble() / img.width.toDouble()
                    wx = width.toDouble()
                    wy = img.height * zoom
                }
            }

            val x = floor((width / 2.0) - (wx / 2.0)).toInt()
            val y = floor((height / 2.0) - (wy / 2.0)).toInt()
            val w = floor(wx).toInt()
            val h = floor(wy).toInt()

            g.drawImage(img, x, y, w + x, h + y, 0, 0, img.width, img.height, null)
        }
    }
}