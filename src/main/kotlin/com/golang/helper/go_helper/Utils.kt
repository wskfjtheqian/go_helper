package com.golang.helper.go_helper

import java.awt.Toolkit
import java.util.*
import java.util.regex.Pattern
import javax.swing.JDialog
import javax.swing.JFrame

object Utils {
    fun uToLine(text: String): String {
        val matcher = Pattern.compile("[A-Z_]").matcher(text)
        val buffer = StringBuffer()
        var index = 0
        while (matcher.find()) {
            buffer.append("_")
            buffer.append(text.substring(index, matcher.start()))
            index = matcher.start()
        }
        if (index < text.length) {
            buffer.append("_")
            buffer.append(text.substring(index))
        }
        var temp = buffer.toString().replace("[_]+".toRegex(), "_").substring(1).lowercase(Locale.getDefault())
        index = temp.indexOf(".")
        if (-1 != index) {
            temp = temp.substring(index + 1)
        }
        return temp
    }

    fun setJDialogToCenter(dialog: JFrame) {
        val screenSize = Toolkit.getDefaultToolkit().screenSize //获取屏幕的尺寸
        val screenWidth = screenSize.width //获取屏幕的宽
        val screenHeight = screenSize.height //获取屏幕的高
        dialog.setLocation(screenWidth / 2 - dialog.width / 2, screenHeight / 2 - dialog.height / 2)//设置窗口居中显示
    }
}
