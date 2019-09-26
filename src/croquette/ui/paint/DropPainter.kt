/*
 *
 * MIT License
 *
 * Copyright (c) 2019 David Großkurth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package croquette.ui.paint

import croquette.ui.GREY
import croquette.ui.RED
import croquette.ui.WHITE
import croquette.ui.lerpColours
import croquette.util.Perlin
import java.awt.Color
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
    private val waiting = 30 * 24

    private var img = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
    private val font = JLabel().font
    private val txt = "Drop in a folder and hit \"Run\"!"

    private var bkg = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
    private var pixels = IntArray(1)
    private var ns = IntArray(1)
    private var pal = DoubleArray(256)
    private var palColour = IntArray(256)
    var intensity = 0.0

    var state = 0
    private var lastTime: Long = 0
    private var t: Long = 0

    var timer = 0
    var lrp = 0.0

    var errorMessage = ""
    var errorTimer = 0

    init {
        for ((idx, _) in pal.withIndex()) {
            val v = (sin(idx.toDouble() / 255.0 * (2 *PI)) * 0.5) + 0.5
            pal[idx] = v
        }

        for ((idx, _) in palColour.withIndex()) {
            val r = (sin(idx.toDouble() / 255.0 * (6 *PI)) * 0.5) + 0.5
            val g = (sin(idx.toDouble() / 255.0 * (2 *PI)) * 0.5) + 0.5
            val b = (sin(idx.toDouble() / 255.0 * (4 *PI)) * 0.5) + 0.5

            val ir = (r * 255.0).toInt()
            val ig = (g * 255.0).toInt()
            val ib = (b * 255.0).toInt()

            val c = (ir shl 16) or (ig shl 8) or ib
            palColour[idx] = c
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

        var tmpi = 0
        for ((idx, v) in palColour.withIndex()) {
            if (idx == 0) {
                tmpi = v
                continue
            }
            palColour[idx - 1] = v
        }
        palColour[palColour.size - 1] = tmpi
    }

    override fun load() {
        img = BufferedImage(canvas.width, canvas.height, BufferedImage.TYPE_INT_ARGB)
        noise()

        lastTime = System.nanoTime()
    }

    override fun paint(g: Graphics2D) {
        if (isSizeChange) {
            img = BufferedImage(canvas.width, canvas.height, BufferedImage.TYPE_INT_ARGB)
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
            errorTimer--
            errorTimer = if (errorTimer < 0) 0 else errorTimer

            if (state == 0) {
                timer += 1
                if (timer >= waiting) {
                    state = 1
                }
            }

            if (state == 1) {
                intensity += 0.005
                if (intensity >= 1.0) {
                    intensity = 1.0
                    state = 2
                }
            }

            if (state == 2) {
                lrp += 0.001
                if (lrp >= 1.0) {
                    lrp = 1.0
                    state = 3
                }
            }

            t -= FPS
        }

        lastTime = timeNow

        for ((idx, v) in ns.withIndex()) {
            if (state < 2) {
                val bw = (pal[v] * 255.0 * intensity).toInt()
                val c = (bw shl 16) or (bw shl 8) or bw
                pixels[idx] = c
            }
            if (state == 2) {
                val bw = (pal[v] * 255.0).toInt()
                val c1 = (bw shl 16) or (bw shl 8) or bw
                val c2 = palColour[v]
                val c3 = lerpColours(c1, c2, lrp)
                pixels[idx] = c3
            }

            if (state > 2) {
                pixels[idx] = palColour[v]
            }
        }
        ig.drawImage(bkg, 0, 0, null)

        // Print the User Message
        ig.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        ig.font = font
        var fw = ig.fontMetrics.stringWidth(txt)
        var fx = canvas.width / 2 - fw / 2
        val fh = ig.fontMetrics.height
        val fy = canvas.height / 2 - fh / 2
        ig.color = GREY
        ig.drawString(txt, fx+2, fy+2)
        ig.color = WHITE
        ig.drawString(txt, fx, fy)

        fw = ig.fontMetrics.stringWidth(errorMessage)
        fx = canvas.width / 2 - fw / 2
        ig.color = Color((RED.rgb and 0xffffff) or (errorTimer shl 24) , true)
        ig.drawString(errorMessage, fx, fy + fh)

        g.drawImage(img, 0, 0, null)
    }
}