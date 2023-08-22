package com.golang.helper.go_helper

import com.goide.intentions.GoBaseIntentionAction
import com.goide.psi.GoStructType
import com.goide.psi.GoTypeSpec
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class GoLangStruct2StructIntention : GoBaseIntentionAction() {
    override fun getFamilyName(): String {
        return "Struct to Struct"
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        var element = PsiTreeUtil.findFirstParent(element) { element -> element is GoTypeSpec }
        var struct = PsiTreeUtil.findChildOfType(element, GoStructType::class.java)
        return null != struct
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        var type = PsiTreeUtil.findFirstParent(element) { element -> element is GoTypeSpec } as GoTypeSpec
        var struct = PsiTreeUtil.findChildOfType(type, GoStructType::class.java)

        var text = StringBuilder()
        text.append(type.identifier.text)

        text.append(" {\n")

        var names = struct!!.fieldDefinitions
        var field = struct!!.fieldDeclarationList

        for (i in 0 until field.size) {
            if (null != field[i].type) {
                text.append("\t")
                text.append(names[i].identifier!!.text)
                text.append(" : val.")
                text.append(names[i].identifier!!.text)
                text.append(Utils.commentToLine(Utils.getFieldComment(field[i])))
                text.append(", \n")
            }
        }

        text.append("}\n\n")
        WindowFactory.show(project, text.toString())
    }

}
