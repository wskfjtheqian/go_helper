package com.golang.helper.go_helper

import com.goide.intentions.GoBaseIntentionAction
import com.goide.psi.*
import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class GoLangStruct2ProtobufIntention : GoBaseIntentionAction(), HighPriorityAction {
    override fun getFamilyName(): String {
        return "struct to protobuf"
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
        text.append(Utils.commentToBack(Utils.getFieldComment(type.parent)))
        text.append("message ")
        text.append(Utils.uToLine(type.identifier.text))

        text.append(" {\n")

        var names = struct!!.fieldDefinitions
        var field = struct!!.fieldDeclarationList

        var index = 1
        for (i in 0 until field.size) {
            if (null != field[i].type) {
                text.append("\t")
                text.append(Utils.toType(field[i].type!!, true))
                text.append(" ")
                text.append(Utils.uToLine(names[i].identifier!!.text))
                text.append(" = ")
                text.append(index)
                text.append(";")
                text.append(Utils.commentToLine(Utils.getFieldComment(field[i])))
                text.append("\n")
                index++
            }
        }

        text.append("}\n\n")

        WindowFactory.show(project, text.toString())
    }


}
