package croquette.util

import java.io.File

class Randomizer {

    private var files = mutableListOf<File>()
    private var idx = 0

    val isReady get() = files.isNotEmpty()

    fun add(f: File) {
        files.add(f)
    }

    fun clear() {
        files = mutableListOf()
    }

    fun list() {
        files.forEach {
            println("\t$it")
        }
    }

    fun shuffle() {
        files.shuffle()
    }

    fun retreive(): File {
        val f = files[idx]
        idx++
        if (idx >= files.size) {
            shuffle()
            idx = 0
        }
        return f
    }
}