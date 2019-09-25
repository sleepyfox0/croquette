package croquette.ui

import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import java.io.File
import java.net.URL
import javax.swing.JPanel

class DropPanel (val callback: (File) -> Unit): JPanel(), DropTargetListener{

    override fun dropActionChanged(e: DropTargetDragEvent?) {
    }

    override fun drop(e: DropTargetDropEvent?) {
        val trans = e?.transferable
        if (trans?.isDataFlavorSupported(DataFlavor.stringFlavor)!!) {
            e.acceptDrop(DnDConstants.ACTION_MOVE)
            val filename = trans.getTransferData(DataFlavor.stringFlavor) as String
            val f = File(URL(filename).toURI())
            if (f.isDirectory) {
                callback(f)
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