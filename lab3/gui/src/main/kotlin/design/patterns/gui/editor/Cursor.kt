package design.patterns.gui.editor

import java.awt.*
import javax.swing.JComponent
import javax.swing.Timer

class Cursor(
    private val textEditorModel: TextEditorModel,
    private val font: Font,
    private val lineHeight: Double,
    private val padding: Int
) :
    JComponent() {

    init {
        val timer = Timer(600) {
            showCursor = !showCursor
            repaint()
        }
        timer.initialDelay = 1000
        timer.start()

        textEditorModel.addCursorObserver {
            timer.restart()
            showCursor = true
            repaint()
        }
    }

    private var showCursor = true

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        if (!showCursor) {
            return
        }

        g as Graphics2D
        val fontMetrics = g.getFontMetrics(font)
        g.color = Color.RED

        val col = textEditorModel.cursorLocation.col
        val sw = fontMetrics.stringWidth(textEditorModel.currentLine.substring(0..<col))
        val cw = fontMetrics.stringWidth(" ")

        val alpha = 0.5f
        val composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)
        g.composite = composite

        g.fillRect(
            padding + sw,
            textEditorModel.cursorLocation.row * (fontMetrics.font.size * lineHeight).toInt(),
            cw,
            (fontMetrics.font.size * lineHeight).toInt()
        )
    }
}
