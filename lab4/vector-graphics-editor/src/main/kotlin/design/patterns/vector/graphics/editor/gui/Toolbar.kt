package design.patterns.vector.graphics.editor.gui

import design.patterns.vector.graphics.editor.graphics.CompositeShape
import design.patterns.vector.graphics.editor.graphics.GraphicalObject
import design.patterns.vector.graphics.editor.graphics.LineSegment
import design.patterns.vector.graphics.editor.graphics.Oval
import design.patterns.vector.graphics.editor.svg.SVGRendererImpl
import java.io.File
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JToolBar

class Toolbar(private val gui: GUI) : JToolBar() {

    private val idObjectMap = listOf(LineSegment(), Oval(), CompositeShape()).associateBy { it.shapeID }

    init {
        add(JButton("Učitaj").apply {
            addActionListener {
                val default = "graphics.txt"
                val fileName = askForFileName(default)?.ifBlank { default } ?: return@addActionListener
                val file = File(fileName)

                val stack = ArrayDeque<GraphicalObject>()
                for (line in file.readLines()) {
                    val prefix = line.takeWhile { !it.isWhitespace() }
                    idObjectMap[prefix.uppercase()]?.load(stack, line)
                }
                gui.documentModel.clear()
                for (o in stack) {
                    gui.documentModel.addGraphicalObject(o)
                }
                println("Graphics loaded")
            }
        })

        add(JButton("Pohrani").apply {
            addActionListener {
                val default = "graphics.txt"
                val fileName = askForFileName(default)?.ifBlank { default } ?: return@addActionListener
                val file = File(fileName)

                val lines = mutableListOf<String>()
                for (o in gui.documentModel.objects) {
                    o.save(lines)
                }
                file.writeText(lines.joinToString("\n"))
                println("Graphics saved")
            }
        })

        add(JButton("SVG Export").apply {
            addActionListener {
                val default = "output.svg"
                val fileName = askForFileName(default)?.ifBlank { default } ?: return@addActionListener
                SVGRendererImpl(fileName).use {
                    for (o in gui.documentModel.objects) o.render(it)
                }
            }
        })

        add(JButton("Linija").apply {
            addActionListener {
                gui.currentState = AddShapeState(LineSegment(), gui.documentModel)
            }
        })

        add(JButton("Oval").apply {
            addActionListener {
                gui.currentState = AddShapeState(Oval(), gui.documentModel)
            }
        })

        add(JButton("Selektiraj").apply {
            addActionListener {
                gui.currentState = SelectShapeState(gui.documentModel)
            }
        })

        add(JButton("Brisalo").apply {
            addActionListener {
                gui.currentState = EraserState(gui.documentModel)
            }
        })
    }

    private fun askForFileName(default: String): String? =
        JOptionPane.showInputDialog(this, "Enter file name:\n(default: '$default')")
}
