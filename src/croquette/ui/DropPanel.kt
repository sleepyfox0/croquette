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

import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import java.io.File
import javax.swing.JPanel

/**
 * Used for dropping directories directly into the program
 *
 * Don't know if works on windows correctly. . .
 */
class DropPanel (val clear: () -> Unit, val callback: (File) -> Unit, val ec: (String) -> Unit): JPanel(), DropTargetListener{

    override fun dropActionChanged(e: DropTargetDragEvent?) {
    }

    override fun drop(e: DropTargetDropEvent?) {
        val trans = e?.transferable
        if (trans?.isDataFlavorSupported(DataFlavor.javaFileListFlavor)!!) {
            e.acceptDrop(DnDConstants.ACTION_MOVE)
            val td = trans.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
            clear()
            td.forEach {
                if (it is File)
                    if (it.isDirectory) {
                        println(it.absolutePath)
                        callback(it)
                    } else {
                        ec("Try using a directory. . .")
                    }
                else
                    ec("The file is strange")
            }

        } else {
            e.rejectDrop()
        }
    }

    override fun dragOver(e: DropTargetDragEvent?) {
    }

    override fun dragExit(e: DropTargetEvent?) {
    }

    override fun dragEnter(e: DropTargetDragEvent?) {
        e?.acceptDrag(DnDConstants.ACTION_MOVE)
    }

    private val target = DropTarget(this, this)

    init {
        background = BLACK
    }
}