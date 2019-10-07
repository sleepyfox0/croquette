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

import croquette.openSlideShow
import croquette.ui.paint.DropPainter
import croquette.ui.paint.PainterManager
import croquette.util.Randomizer
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.File
import java.nio.file.Files
import javax.swing.*

/**
 * Main window
 * Users can open/or drop folders and run them if they are valid.
 * It's also possible to select the session time.
 */
class CWin : JFrame("Croquette") {

    //Files Java's ImageIO supports
    private val MIME_TYPES = setOf("image/png", "image/jpg", "image/jpeg", "image/gif")

    private val content = JPanel()
    private val open = JButton("Open...")
    private val play = JButton("Run")
    private val lMinutes = JLabel("Minutes:")
    private val sMinutes = JSpinner()
    private val lSeconds = JLabel("Seconds:")
    private val sSeconds = JSpinner()
    private val drop = DropPanel(this::clearFiles, this::handleFiles, this::handleError)

    private val rand = Randomizer()
    private val pm = PainterManager()
    private val dP = DropPainter(drop)

    init {
        content.preferredSize = Dimension(320, 240)
        buildGUI()
        buildBehaviour()
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        pm.add(dP)
    }

    private fun buildGUI() {
        content.background = BLACK
        content.layout = GridBagLayout()

        open.background = GREY
        open.foreground = WHITE
        open.isBorderPainted = false
        open.isFocusPainted = false
        var c = GridBagConstraints()
        c.gridx = 1
        c.gridy = 0
        c.anchor = GridBagConstraints.LINE_START
        c.insets = Insets(6, 6, 6, 6)
        content.add(open, c)

        play.background = GREY
        play.foreground = WHITE
        play.isBorderPainted = false
        play.isFocusPainted = false
        play.isEnabled = false
        c = GridBagConstraints()
        c.gridx = 0
        c.gridy = 0
        c.anchor = GridBagConstraints.LINE_START
        c.insets = Insets(6, 6, 6, 6)
        content.add(play, c)

        lMinutes.background = BLACK
        lMinutes.foreground = WHITE
        c = GridBagConstraints()
        c.gridx = 0
        c.gridy = 1
        c.anchor = GridBagConstraints.LINE_START
        c.insets = Insets(6, 6, 6, 6)
        content.add(lMinutes, c)

        c = GridBagConstraints()
        c.gridx = 1
        c.gridy = 1
        c.anchor = GridBagConstraints.LINE_START
        c.insets = Insets(6, 6, 6, 6)
        content.add(sMinutes, c)

        lSeconds.background = BLACK
        lSeconds.foreground = WHITE
        c = GridBagConstraints()
        c.gridx = 2
        c.gridy = 1
        c.anchor = GridBagConstraints.LINE_START
        c.insets = Insets(6, 6, 6, 6)
        content.add(lSeconds, c)

        c = GridBagConstraints()
        c.gridx = 3
        c.gridy = 1
        c.anchor = GridBagConstraints.LINE_START
        c.insets = Insets(6, 6, 6, 6)
        content.add(sSeconds, c)

        c = GridBagConstraints()
        c.gridx = 0
        c.gridy = 2
        c.gridwidth = 4
        c.weightx = 0.8
        c.weighty = 0.8
        c.anchor = GridBagConstraints.CENTER
        c.fill = GridBagConstraints.BOTH
        c.insets = Insets(6, 6, 6, 6)
        content.add(drop, c)

        add(content)
    }

    private fun buildBehaviour() {
        open.addActionListener {
            val fc = JFileChooser()
            fc.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            val selection = fc.showOpenDialog(this)
            if (selection == JFileChooser.APPROVE_OPTION) {
                clearFiles()
                val f = fc.selectedFiles
                f.forEach {
                    handleFiles(it)
                }
            }
        }

        play.addActionListener {
            val m = sMinutes.value
            val s = sSeconds.value
            var time = 1
            if (m is Int) {
                time = m * 60
            }

            if (s is Int) {
                time += s
            }
            time *= 1000
            time = if (time < 1000) 1000 else time

            isVisible = false
            println("Playing with the following files")
            rand.shuffle()
            rand.list()
            openSlideShow(rand, time, x, y, width, height)
        }


        val modelMinutes = SpinnerNumberModel(5, 0, 60, 1)
        sMinutes.model = modelMinutes

        val modelSeconds = SpinnerNumberModel(0, 0, 60, 1)
        sSeconds.model = modelSeconds
    }

    /**
     * Goes through a folder and adds supported files to the Randomizer
     */
    private fun handleFiles(f: File) {
        println("Selected: ${f.absolutePath}")
        val files = f.listFiles()
        files?.forEach {
            if (it.isFile) {
                val mime = Files.probeContentType(it.toPath())
                if (mime != null)
                    if (MIME_TYPES.contains(mime.trim()))
                        rand.add(it)
            }
        }
        rand.shuffle()
        rand.list()

        play.isEnabled = rand.isReady
    }

    private fun clearFiles() {
        rand.clear()
    }

    private fun handleError(s: String) {
        dP.errorMessage = s
        dP.errorTimer = 255
    }

    fun exec() {
        pack()
        setLocationRelativeTo(null)
        isVisible = true
        pm.go()
    }
}