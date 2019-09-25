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

class CWin : JFrame("Croquette") {
    private val MIME_TYPES = setOf("image/png", "image/jpg", "image/jpeg", "image/gif")

    private val content = JPanel()
    private val open = JButton("Open")
    private val play = JButton("Run")
    private val lMinutes = JLabel("Minutes:")
    private val sMinutes = JSpinner()
    private val lSeconds = JLabel("Seconds:")
    private val sSeconds = JSpinner()
    private val drop = DropPanel(this::handleFiles)

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
        c.gridx = 0
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
        c.gridx = 1
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
                val f = fc.selectedFile
                handleFiles(f)
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
            openSlideShow(rand, time)
            //SlideShow(this, rand, time).exec()
            //dispose()
        }


        val modelMinutes = SpinnerNumberModel(5, 0, 60, 1)
        sMinutes.model = modelMinutes

        val modelSeconds = SpinnerNumberModel(0, 0, 60, 1)
        sSeconds.model = modelSeconds
    }

    private fun handleFiles(f: File) {
        rand.clear()
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

    fun exec() {
        pack()
        setLocationRelativeTo(null)
        isVisible = true
        pm.go()
    }
}