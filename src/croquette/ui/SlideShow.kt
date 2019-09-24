package croquette.ui

import croquette.util.Randomizer
import croquette.ui.paint.PainterManager
import croquette.ui.paint.TimePainter
import croquette.util.Timer
import java.awt.*
import javax.imageio.ImageIO
import javax.swing.*

class SlideShow(private val rnd: Randomizer, private val t: Int) : JFrame("Croquette") {

    //private val t = 10000

    private val contents = JPanel()
    private val cbPin = JCheckBox("pin")
    private val lTime = JLabel("Time!!!")
    private val pImage = ImagePanel()
    private val pTS = JPanel()
    private val timer = Timer()

    private val pm = PainterManager()
    private val paintTS = TimePainter(pTS)

    init {
        buildGUI()
        buildBehavior()

        pImage.img = ImageIO.read(rnd.retreive())
        background = BLACK
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
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

        //cbPin.foreground = Color.WHITE

        lTime.background = GREY
        lTime.foreground = WHITE
        c = GridBagConstraints()
        c.fill = GridBagConstraints.NONE
        c.gridx = 1
        c.gridy = 0
        c.anchor = GridBagConstraints.LINE_END
        c.insets = Insets(0, 6, 0, 6)
        contents.add(lTime, c)

        c = GridBagConstraints()
        c.fill = GridBagConstraints.BOTH
        c.gridx = 0
        c.gridy = 1
        c.gridwidth = 2
        c.weightx = 0.9
        c.weighty = 0.9
        c.anchor = GridBagConstraints.CENTER
        contents.add(pImage, c)

        pTS.background = BLACK
        c = GridBagConstraints()
        c.fill = GridBagConstraints.HORIZONTAL
        c.gridx = 0
        c.gridy = 2
        c.gridwidth = 2
        c.anchor = GridBagConstraints.LINE_START
        contents.add(pTS, c)

        contents.preferredSize = Dimension(320, 240)
        add(contents)
    }

    private fun buildBehavior() {
        cbPin.addItemListener {
            pin(cbPin.isSelected)
        }
    }

    private fun pin(state: Boolean) {
        isAlwaysOnTop = state
    }

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

    private fun tick() {
        pImage.img = ImageIO.read(rnd.retreive())
        repaint()
        startTimer()
    }

    fun exec() {
        pack()
        setLocationRelativeTo(null)
        isVisible = true
        startTimer()
        pm.go()
    }
}