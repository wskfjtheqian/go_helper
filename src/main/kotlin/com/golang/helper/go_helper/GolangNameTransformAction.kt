package com.golang.helper.go_helper

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import java.util.regex.Pattern

class GolangNameTransformAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val model = editor.selectionModel
        val text = model.selectedText
        if (null == text) {
            return
        }
        val temp = if (text.contains(Regex("[A-Z]"))) {
            Utils.nameUnderline(text)
        } else {
            Utils.nameCapitalized(text)
        }
        val action = Runnable {
            editor.document.replaceString(
                    model.selectionStart,
                    model.selectionEnd,
                    temp
            )
        }
        WriteCommandAction.runWriteCommandAction(editor.project, action)
    }
}
