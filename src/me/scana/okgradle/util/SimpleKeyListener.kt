package me.scana.okgradle.util

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

open class SimpleKeyListener : KeyListener {
    override fun keyTyped(e: KeyEvent?) {}
    override fun keyPressed(e: KeyEvent?) {}
    override fun keyReleased(e: KeyEvent) {}
}