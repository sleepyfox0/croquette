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
package croquette.ui

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.image.BufferedImage
import javax.swing.JButton
import javax.swing.JPanel
import kotlin.math.floor

/**
 * Displays the image slide show
 */
class ImagePanel(parent: SlideShow) : JPanel(null, true) {

    var img = BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY)

    private var isMouse = false

    private val bSkip = JButton("Skip")
    private val bPause = JButton("Pause")

    init {
        bSkip.setSize(100, 25)
        bSkip.background = GREY
        bSkip.foreground = WHITE
        bSkip.isBorderPainted = false

        bPause.setSize(100, 25)
        bPause.background = GREY
        bPause.foreground = WHITE
        bPause.isBorderPainted = false

        background = BLACK
        addMouseListener(object: MouseAdapter() {
            override fun mouseEntered(e: MouseEvent?) {
                isMouse = true
                repaint()
            }

            override fun mouseExited(e: MouseEvent?) {
                isMouse = false
                repaint()
            }

            override fun mousePressed(e: MouseEvent?) {
                var r = bPause.bounds
                r.x = 6
                r.y = height - 31

                if (r.contains(e!!.point))
                    bPause.background = GREY.brighter().brighter()
                else
                    bPause.background = GREY

                r = bSkip.bounds
                r.x = 112
                r.y = height - 31

                if (r.contains(e.point))
                    bSkip.background = GREY.brighter().brighter()
                else
                    bSkip.background = GREY

                repaint()
            }

            override fun mouseReleased(e: MouseEvent?) {
                val r = bSkip.bounds
                r.x = 112
                r.y = height - 31

                if (r.contains(e!!.point)) {
                    parent.skip()
                } else {
                    bPause.text = parent.pause()
                }
                bSkip.background = GREY
                bPause.background = GREY

                repaint()
            }
        })

        addMouseMotionListener(object: MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent?) {
                var r = bPause.bounds
                r.x = 6
                r.y = height - 31

                if (r.contains(e!!.point))
                    bPause.background = GREY.brighter().brighter()
                else
                    bPause.background = GREY

                r = bSkip.bounds
                r.x = 112
                r.y = height - 31

                if (r.contains(e.point))
                    bSkip.background = GREY.brighter().brighter()
                else
                    bSkip.background = GREY

                repaint()
            }
        })
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (g is Graphics2D) {
            //Resize image so that the size is proportional

            g.color = Color.WHITE
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

            //Draw overlay to pause and skip
            if (isMouse) {
                //Components are painted manually!!
                g.translate(6, height - 31)
                bPause.paint(g)
                g.translate(106, 0)
                bSkip.paint(g)
            }
        }
    }
}