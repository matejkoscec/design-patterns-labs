package design.patterns.gui

import java.awt.event.ActionEvent
import javax.swing.AbstractAction

fun action(block: (e: ActionEvent) -> Unit): AbstractAction =
    object : AbstractAction() {
        override fun actionPerformed(e: ActionEvent) = block(e)
    }
