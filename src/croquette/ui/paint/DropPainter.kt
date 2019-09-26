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

import croquette.ui.*
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import javax.swing.JLabel
import javax.swing.JPanel
import kotlin.math.PI
import kotlin.math.sin

/**
 * Painter for the Drop Panel in the main menu.
 */
class DropPainter(canvas: JPanel) : Painter(canvas) {

    //limit execution to 60 frames per second
    private val FPS = ((1.0 / 60.0) * 1_000_000_000).toLong()

    private val font = JLabel().font
    private val txt = "Drop in a folder and hit \"Run\"!"

    //used for updates at a certain FPS
    private var lastTime: Long = 0
    private var t: Long = 0

    var errorMessage = ""
    var errorTimer = 0

    override fun load() {
        lastTime = System.nanoTime()
    }

    override fun paint(g: Graphics2D) {
        //update at FPS frames per second
        val timeNow = System.nanoTime()
        val dtn = timeNow - lastTime
        t += dtn

        //Updates the panel's contents
        while (t > FPS) {
            errorTimer--
            errorTimer = if (errorTimer < 0) 0 else errorTimer

            t -= FPS
        }

        lastTime = timeNow

        // Print the User Message
        g.color = BLACK
        g.fillRect(0, 0, canvas.width, canvas.height)
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.font = font
        var fw = g.fontMetrics.stringWidth(txt)
        var fx = canvas.width / 2 - fw / 2
        val fh = g.fontMetrics.height
        val fy = canvas.height / 2 - fh / 2
        g.color = GREY
        g.drawString(txt, fx+2, fy+2)
        g.color = WHITE
        g.drawString(txt, fx, fy)

        // Print errors
        fw = g.fontMetrics.stringWidth(errorMessage)
        fx = canvas.width / 2 - fw / 2
        g.color = Color((RED.rgb and 0xffffff) or (errorTimer shl 24) , true)
        g.drawString(errorMessage, fx, fy + fh)
    }
}