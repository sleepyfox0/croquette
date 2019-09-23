package croquette.ui

import croquette.util.Randomizer
import java.awt.Dimension
import java.nio.file.Files
import javax.swing.*

class CWin : JFrame("Croquette") {
    private val MIME_TYPES = setOf("image/png", "image/jpg", "image/jpeg", "image/gif")

    private val content = JPanel()
    private val open = JButton("Open")
    private val play = JButton("Run")
    private val lTime = JLabel("Time: ")
    private val cbTime = JComboBox(arrayOf("1", "2", "5", "10"))
    private val rand = Randomizer()

    init {
        content.preferredSize = Dimension(320, 240)
        buildGUI()
        buildBehaviour()
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    }

    private fun buildGUI() {
        open.background = BLUE
        open.foreground = WHITE
        open.isFocusPainted = false

        play.background = BLUE
        play.foreground = WHITE
        play.isFocusPainted = false
        play.isEnabled = false

        lTime.background = BLACK
        lTime.foreground = WHITE

        content.background = BLACK
        content.add(open)
        content.add(play)
        content.add(lTime)
        content.add(cbTime)

        add(content)
    }

    private fun buildBehaviour() {
        open.addActionListener {
            val fc = JFileChooser()
            fc.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            val selection = fc.showOpenDialog(this)
            if (selection == JFileChooser.APPROVE_OPTION) {
                rand.clear()
                val f = fc.selectedFile
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
        }

        play.addActionListener {
            val t = cbTime.selectedItem
            var time = 1
            if (t is String) {
                time = t.toInt()
            }
            time *= 60000
            isVisible = false
            SlideShow(rand, time).exec()
            dispose()
        }
    }

    fun exec() {
        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }
}