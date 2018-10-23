package me.scana.okgradle.util

import com.intellij.ui.components.JBList
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.KeyEvent
import javax.swing.JComponent
import javax.swing.JPopupMenu
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.ListSelectionListener



class HintTextField(var hint: String = "") : JTextField() {
    override fun paint(g: Graphics?) {
        super.paint(g)
        if (text.isEmpty()) {
            (g as Graphics2D).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            val ins = insets
            val fm = g.getFontMetrics()
            g.setColor(Color.GRAY)
            g.drawString(hint, ins.left * 2 , height / 2 + fm.ascent / 2 - 2)
        }
    }
}

abstract class DocumentListenerAdapter : DocumentListener {

    abstract fun update(e: DocumentEvent)

    override fun changedUpdate(e: DocumentEvent) {
        update(e)
    }

    override fun insertUpdate(e: DocumentEvent) {
        update(e)
    }

    override fun removeUpdate(e: DocumentEvent) {
        update(e)
    }
}

fun HintTextField.observeText(): Observable<String> = Observable.create {
    val listener = object : DocumentListenerAdapter() {
        override fun update(e: DocumentEvent) {
            it.onNext(text)
        }
    }
    document.addDocumentListener(listener)
    it.setDisposable(Disposables.fromAction {
        document.removeDocumentListener(listener)
    })
}

sealed class Selection<T> {
    class Item<T>(val value: T) : Selection<T>()
    class None<T> : Selection<T>()
}

fun <T> JBList<T>.observeSelection(): Observable<Selection<T>> = Observable.create {
    val listener = ListSelectionListener { _ ->
        if (isSelectionEmpty) {
            it.onNext(Selection.None())
        } else {
            it.onNext(Selection.Item(model.getElementAt(selectedIndex)))
        }
    }
    addListSelectionListener(listener)
    it.setDisposable(Disposables.fromAction { removeListSelectionListener(listener) })
}

fun JComponent.onKeyPress(keyCode: Int, action: () -> Unit)  {
    addKeyListener(object : SimpleKeyListener() {
        override fun keyReleased(e: KeyEvent) {
            if (e.keyCode == keyCode) {
                action()
            }
        }
    })
}

fun JComponent.singleOnKeyPress(keyCode: Int, action: () -> Unit) {
    addKeyListener(object : SimpleKeyListener() {
        override fun keyReleased(e: KeyEvent) {
            if (e.keyCode == keyCode) {
                action()
                removeKeyListener(this)
            }
        }
    })
}

fun JPopupMenu.invokeKeyDownPress() {
    SwingUtilities.invokeLater {
        this.dispatchEvent(
                KeyEvent(this, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN, '\u0000')
        )
    }
}

fun Color.transparent(alpha: Int): Color {
    return Color(this.red, this.green, this.blue, alpha)
}