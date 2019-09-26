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

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JPanel

/**
 * A Painter actively paints on top of a JPanel which serves as canvas
 * Painters are managed by a PainterManager. The canvas of a Painter
 * usually cannot display its normal contents anymore and must be done
 * entirely by the programmer.
 */
abstract class Painter(val canvas: JPanel) {

    var db = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    var isSizeChange = false

    open fun load() {}

    /**
     * Makes sure only correct values are used by paint.
     * Also creates a double buffer strategy.
     */
    fun prePaint() {
        isSizeChange = false
        if (canvas.width > db.width) {
            db = BufferedImage(canvas.width, db.height, BufferedImage.TYPE_INT_ARGB)
            isSizeChange = true
        }

        if (canvas.height > db.height) {
            db = BufferedImage(db.width, canvas.height, BufferedImage.TYPE_INT_ARGB)
            isSizeChange = true
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

    /**
     * Implement all rendering here.
     */
    abstract fun paint(g: Graphics2D)
}