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

import croquette.openMainWin
import croquette.ui.paint.PainterManager
import croquette.ui.paint.TimePainter
import croquette.util.Randomizer
import croquette.util.Timer
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.dnd.DropTarget
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JCheckBox
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * As the name says
 */
class SlideShow(private val rnd: Randomizer, private val t: Int) : JFrame("Croquette") {

    //private val t = 10000

    private val contents = JPanel()
    private val cbPin = JCheckBox("pin")
    private val cbBorder = JCheckBox("borderless")
    private val lTime = JLabel("Time!!!")
    private val pImage = ImagePanel(this)
    private val pTS = JPanel()
    private val timer = Timer()

    private val pm = PainterManager()
    private val paintTS = TimePainter(pTS)

    private var isPaused = false
    private val fd = FolderDrop(rnd::clear, this::newFolders, this::onError)

    private val target = DropTarget(this, fd)

    init {
        buildGUI()
        buildBehavior()

        pImage.img = ImageIO.read(rnd.retrieve())
        background = BLACK
        //defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        addWindowListener(object: WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                openMainWin(x, y, width, height)
                isVisible = false
                dispose()
            }
        })
        pm.add(paintTS)
    }

    private fun buildGUI() {
        contents.background = GREY
        contents.layout = GridBagLayout()

        cbPin.background = GREY
        cbPin.foreground = WHITE
        cbPin.isFocusable = false
        var c = GridBagConstraints()
        c.fill = GridBagConstraints.NONE
        c.gridx = 0
        c.gridy = 0
        c.anchor = GridBagConstraints.LINE_START
        contents.add(cbPin, c)

        cbBorder.background = GREY
        cbBorder.foreground = WHITE
        cbBorder.isFocusable = false
        c = GridBagConstraints()
        c.fill = GridBagConstraints.NONE
        c.gridx = 1
        c.gridy = 0
        c.anchor = GridBagConstraints.LINE_START
        contents.add(cbBorder, c)

        //cbPin.foreground = Color.WHITE

        lTime.background = GREY
        lTime.foreground = WHITE
        c = GridBagConstraints()
        c.fill = GridBagConstraints.NONE
        c.gridx = 3
        c.gridy = 0
        c.anchor = GridBagConstraints.LINE_END
        c.insets = Insets(0, 6, 0, 6)
        contents.add(lTime, c)

        c = GridBagConstraints()
        c.fill = GridBagConstraints.BOTH
        c.gridx = 0
        c.gridy = 1
        c.gridwidth = 4
        c.weightx = 1.0
        c.weighty = 0.9
        c.anchor = GridBagConstraints.CENTER
        contents.add(pImage, c)

        pTS.background = BLACK
        c = GridBagConstraints()
        c.fill = GridBagConstraints.HORIZONTAL
        c.gridx = 0
        c.gridy = 2
        c.gridwidth = 4
        c.weightx = 1.0
        c.anchor = GridBagConstraints.LINE_START
        contents.add(pTS, c)

        contents.preferredSize = Dimension(320, 240)
        add(contents)
    }

    private fun buildBehavior() {
        cbPin.addItemListener {
            pin(cbPin.isSelected)
        }

        cbBorder.addItemListener {
            border(cbBorder.isSelected)
        }
    }

    private fun pin(state: Boolean) {
        isAlwaysOnTop = state
    }

    private fun border(state: Boolean) {
        dispose()
        isUndecorated = state
        isVisible = true
    }

    /**
     * Callback for timer updates
     */
    private fun updateTimer(nt : Int) {
        paintTS.current = nt
        var seconds = (t - nt) / 1000
        val minutes = seconds / 60
        seconds -= (minutes * 60)
        lTime.text = "$minutes : ${seconds.toString().padStart(2, '0')}"
    }

    private fun startTimer() {
        timer.time = t
        paintTS.total = timer.time
        timer.startTimer(this::tick, this::updateTimer)
    }

    /**
     * Callback for when the timer is finished
     */
    private fun tick() {
        pImage.img = ImageIO.read(rnd.retrieve())
        repaint()
        startTimer()
    }

    fun skip() {
        timer.isRunning = false
    }

    fun pause(): String {
        isPaused = !isPaused
        timer.isPaused = isPaused
        return if (isPaused) "Unpause" else "Pause"
    }

    private fun onError(msg: String) {
        println(msg)
    }

    private fun newFolders(f: File) {
        rnd.handleFiles(f)
        rnd.shuffle()
    }

    fun exec(x: Int, y: Int, w: Int, h: Int) {
        pack()
        setLocation(x, y)
        setSize(w, h)
        isVisible = true
        startTimer()
        pm.go()
    }
}