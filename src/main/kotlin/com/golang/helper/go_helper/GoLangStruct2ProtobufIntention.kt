package com.golang.helper.go_helper

import com.goide.intentions.GoBaseIntentionAction
import com.goide.psi.*
import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.rd.util.string.println

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
        text.append("message ")
        text.append(Utils.uToLine(type.identifier.text))

        text.append(" {\n")

        var names=struct!!.fieldDefinitions
        var field=struct!!.fieldDeclarationList

        for (i in 0 until field.size) {
            text.append("\t")
            text.append(Utils.uToLine(toType(field[i].type!!)))
            text.append(" ")
            text.append(Utils.uToLine(names[i].identifier!!.text))
            text.append(" = ")
            text.append(i+1)
            text.append("; \n")
        }

        text.append("}\n\n")

        WindowFactory.show(project,text.toString())
    }

    private fun toType(typ: GoType): String {
        if(typ is GoArrayOrSliceType){
            return "repeated " + typ.type.presentationText
        } else if(typ is GoMapType){
            return "map<" + typ.keyType!!.presentationText +", " + typ.valueType!!.presentationText +">"
        }

        return typ.presentationText
    }

}