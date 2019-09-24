package croquette.ui.paint

import croquette.ui.GREY
import croquette.ui.WHITE
import croquette.util.Perlin
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import javax.swing.JLabel
import javax.swing.JPanel
import kotlin.math.PI
import kotlin.math.sin

class DropPainter(canvas: JPanel) : Painter(canvas) {

    private val FPS = ((1.0 / 24.0) * 1_000_000_000).toLong()
    private val waiting = 5 * 24

    private var img = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
    private val font = JLabel().font
    private val txt = "Drop in a folder and hit \"Run\"!"

    private var bkg = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
    private var pixels = IntArray(1)
    private var ns = IntArray(1)
    private var pal = DoubleArray(256)
    private var intensity = 0.0

    private var state = 0
    private var lastTime: Long = 0
    private var t: Long = 0

    private var timer = 0

    init {
        for ((idx, _) in pal.withIndex()) {
            val v = (sin(idx.toDouble() / 255.0 * (2 *PI)) * 0.5) + 0.5
            pal[idx] = v
        }
    }

    private fun noise() {
        bkg = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_RGB)
        pixels = (bkg.raster.dataBuffer as DataBufferInt).data
        ns = IntArray(pixels.size)
        for ((idx, _) in ns.withIndex()) {
            val y = (idx / img.width).toDouble() / 0.5
            val x = (idx % img.width).toDouble() / 0.5
            var n = Perlin.noise(x / 512.0, y / 512.0, 10.0 / 512.0) * 4
            n += Perlin.noise(x / 256.0, y / 256.0, 10.0 / 256.0) * 2
            n += Perlin.noise(x / 128.0, y / 128.0, 10.0 / 128.0)
            //n += Perlin.noise(x / 8.0, y / 8.0, 10.0 / 8.0)
            n /= 7.0
            n = (n * 0.5) + 0.5
            val bw = (n * 255.0).toInt()
            ns[idx] = bw
        }
    }

    private fun shiftPal() {
        var tmp = 0.0
        for ((idx, v) in pal.withIndex()) {
            if (idx == 0) {
                tmp = v
                continue
            }
            pal[idx - 1] = v
        }
        pal[pal.size - 1] = tmp
    }

    override fun load() {
        img = BufferedImage(canvas.width, canvas.height, BufferedImage.TYPE_INT_RGB)
        noise()

        lastTime = System.nanoTime()
    }

    override fun paint(g: Graphics2D) {
        if (isSizeChange) {
            img = BufferedImage(canvas.width, canvas.height, BufferedImage.TYPE_INT_RGB)
            noise()
        }

        //Paint the secret plasma
        val ig = img.graphics as Graphics2D

        //update at FPS frames per second
        val timeNow = System.nanoTime()
        val dtn = timeNow - lastTime
        t += dtn

        while (t > FPS) {
            shiftPal()

            if (state == 0) {
                timer += 1
                if (timer >= waiting) {
                    state = 1
                    println("State is now 1")
                }
            }

            if (state == 1) {
                intensity += 0.005
                if (intensity >= 1.0) {
                    intensity = 1.0
                    state = 2
                    println("State is now 2")
                }
            }
            t -= FPS
        }

        lastTime = timeNow

        //ig.color = BLACK
        //ig.fillRect(0, 0, canvas.width, canvas.height)
        for ((idx, v) in ns.withIndex()) {
            val bw = (pal[v] * 255.0 * intensity).toInt()
            val c = (bw shl 16) or (bw shl 8) or bw
            pixels[idx] = c
        }
        ig.drawImage(bkg, 0, 0, null)

        // Print the User Message
        ig.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        ig.font = font
        val fw = ig.fontMetrics.stringWidth(txt)
        val fx = canvas.width / 2 - fw / 2
        val fh = ig.fontMetrics.height
        val fy = canvas.height / 2 - fh / 2
        ig.color = GREY
        ig.drawString(txt, fx+2, fy+2)
        ig.color = WHITE
        ig.drawString(txt, fx, fy)

        g.drawImage(img, 0, 0, null)
    }
}