package croquette.ui

import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import java.io.File
import java.net.URL
import javax.swing.JPanel

class DropPanel (val callback: (File) -> Unit, val ec: (String) -> Unit): JPanel(), DropTargetListener{

    override fun dropActionChanged(e: DropTargetDragEvent?) {
    }

    override fun drop(e: DropTargetDropEvent?) {
        val trans = e?.transferable
        if (trans?.isDataFlavorSupported(DataFlavor.javaFileListFlavor)!!) {
            e.acceptDrop(DnDConstants.ACTION_MOVE)
            /*val files = trans.getTransferData(DataFlavor.stringFlavor)
            if (files is List<*>) {
                files.forEach {
                    if (it is File) {
                        if (it.isDirectory) {
                            callback(it)
                        }
                    }
                }
            } else if(files is String) {
                val filenames = files.split("\r\n")
                filenames.forEach {
                    if (it.isNotEmpty()) {
                        val f = File(URL(it).toURI())
                        if (f.isDirectory) {
                            callback(f)
                        } else {
                            ec("Dropped file is not a directory!")
                        }
                    }
                }

            } else {
                ec("Cannot handle selected files")
            }*/
            val td = trans.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
            val f = td[0]
            if (f is File)
                if (f.isDirectory) {
                    callback(f)
                } else {
                    ec("Try using a directory. . .")
                }
            else
                ec("The file is strange")
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