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
        val element = PsiTreeUtil.findFirstParent(element) { it -> it is GoTypeSpec }
        var struct = PsiTreeUtil.findChildOfType(element, GoStructType::class.java)
        return null != struct
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        var type = PsiTreeUtil.findFirstParent(element) { element -> element is GoTypeSpec } as GoTypeSpec
        var struct = PsiTreeUtil.findChildOfType(type, GoStructType::class.java)

        var text = StringBuilder()
        text.append(Utils.commentToBack(Utils.getFieldComment(type.parent)))
        text.append("message ")
        text.append(Utils.nameUnderline(Utils.deletePackage(type.identifier.text)))

        text.append(" {\n")

        val names = struct!!.fieldDefinitions
        val field = struct.fieldDeclarationList

        var index = 1
        for (i in 0 until names.size) {
            if ( names[i] is GoFieldDefinition) {
                val type = names[i].parent as GoFieldDeclaration
                text.append("\t")
                text.append(Utils.toType(type.type!!, true))
                text.append(" ")
                text.append(Utils.nameUnderline(Utils.deletePackage(names[i].identifier!!.text)))
                text.append(" = ")
                text.append(index)
                text.append(";")
                text.append(Utils.commentToLine(Utils.getFieldComment(type)))
                text.append("\n")
                index++
            }
        }
        text.append("}\n\n")

        WindowFactory.show(project, text.toString())
    }


}
