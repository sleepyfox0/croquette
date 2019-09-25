package croquette

import croquette.ui.CWin
import croquette.ui.SlideShow
import croquette.util.Randomizer

val launcher = CWin()

fun openMainWin() {
    launcher.isVisible = true
}

fun openSlideShow(rng: Randomizer, time: Int) {
    SlideShow(rng, time).exec()
}

fun main() {
    launcher.exec()
}