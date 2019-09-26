package croquette

import croquette.ui.CWin
import croquette.ui.SlideShow
import croquette.util.Randomizer

val launcher = CWin()

fun openMainWin(x: Int, y: Int, w: Int, h: Int) {
    launcher.setLocation(x, y)
    launcher.setSize(w, h)
    launcher.isVisible = true
    launcher.resetTheThing()
}

fun openSlideShow(rng: Randomizer, time: Int, x: Int, y: Int, w: Int, h: Int) {
    SlideShow(rng, time).exec(x, y, w, h)
}

fun main() {
    launcher.exec()
}