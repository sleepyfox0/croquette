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

import croquette.ui.BLACK
import croquette.ui.GREY
import croquette.ui.RED
import croquette.ui.pureColourLerp
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
        g.color = pureColourLerp(RED, GREY, percent)
        g.fillRect(0, 0, size, canvas.height)
    }
}