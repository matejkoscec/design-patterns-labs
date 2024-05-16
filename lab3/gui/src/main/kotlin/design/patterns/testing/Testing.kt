package design.patterns.testing

import java.awt.BorderLayout
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.*


fun main() {
    SwingUtilities.invokeLater {
        MyFrame().isVisible = true
    }
}

private fun action(block: (e: ActionEvent) -> Unit): AbstractAction =
    object : AbstractAction() {
        override fun actionPerformed(e: ActionEvent) = block(e)
    }

class MyComponent : JComponent() {

    init {
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "close")
        actionMap.put("close", action {
            this.firePropertyChange("value", 1, 2)
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.drawLine(10, 10, 100, 10)
        g.drawLine(10, 10, 10, 50)
        g.drawString("Hello, World!", 20, 30)
    }
}

class MyFrame : JFrame() {
    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(300, 300)
        layout = BorderLayout()

        val myComponent = MyComponent()
        myComponent.addPropertyChangeListener("value") {
            dispose()
        }
        add(myComponent, BorderLayout.CENTER)
    }
}
