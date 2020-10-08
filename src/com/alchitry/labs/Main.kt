package com.alchitry.labs

import com.alchitry.labs.gui.main.LoaderWindow
import com.alchitry.labs.gui.main.MainWindow
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.logging.Level
import kotlin.system.exitProcess

const val VERSION = "1.2.2"

fun main(args: Array<String>) {
    parseCommand(args)
    Util.isGUI = true
    try {
        MainWindow.open()
    } catch (e: Throwable) {
        Util.logger.log(Level.SEVERE, "", e)
        if (Util.envType != Util.IDE) Reporter.reportException(e, true)
        MainWindow.saveOnCrash()
        Settings.commit()
    }
    runBlocking { Reporter.waitForAll() }
    return
}

private fun parseCommand(args: Array<String>) {
    if (args.isNotEmpty()) {
        when (args[0]) {
            "lin32" -> Util.envType = Util.LIN32
            "lin64" -> Util.envType = Util.LIN64
            "win32" -> Util.envType = Util.WIN32
            "win64" -> Util.envType = Util.WIN64
            "mac32" -> Util.envType = Util.MAC32
            "mac64" -> Util.envType = Util.MAC64
            "ide" -> Util.envType = Util.IDE
        }
    }
    if (Util.envType == Util.UNKNOWN) {
        if (args.size == 2 && args[0] == "-u") {
            try {
                UpdateChecker.copyLibrary(args[1])
            } catch (e: IOException) {
                exitProcess(1)
            }
            exitProcess(0)
        } else {
            System.err.println("Library value missing after -u!")
            exitProcess(2)
        }
    } else if (args.size > 1 && args[1] == "--loader") {
        runLoader()
    }
}

private fun runLoader() {
    val loader: LoaderWindow
    Util.isGUI = true
    try {
        loader = LoaderWindow()
        Util.loader = loader
        loader.open()
    } catch (e: Throwable) {
        Util.logger.log(Level.SEVERE, "", e)
        if (Util.envType != Util.IDE) Reporter.reportException(e, true)
    }
    runBlocking { Reporter.waitForAll() }
    exitProcess(0)
}