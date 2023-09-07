package com.golang.helper.go_helper

import com.goide.intentions.GoBaseIntentionAction
import com.goide.psi.GoMethodDeclaration
import com.goide.psi.GoStructType
import com.goide.psi.GoTypeSpec
import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.protobuf.lang.psi.PbMessageBody
import com.intellij.protobuf.lang.psi.PbMessageDefinition
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import io.netty.handler.codec.protobuf.ProtobufEncoder

class GoLangStruct2InterfaceIntention : GoBaseIntentionAction(), HighPriorityAction {
    override fun getFamilyName(): String {
        return "Struct to interface"
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        var element = PsiTreeUtil.findFirstParent(element) { element -> element is GoTypeSpec }
        var struct = PsiTreeUtil.findChildOfType(element, GoStructType::class.java)
        return null != struct
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        var type = PsiTreeUtil.findFirstParent(element) { element -> element is GoTypeSpec } as GoTypeSpec

        var text = StringBuilder()
        text.append(Utils.commentToBack(Utils.getFieldComment(type.parent)))
        text.append("type ")
        text.append(type.identifier.text)

        text.append("RepoI interface {\n")

        var methods = type!!.methods
        for (i in 0 until methods.size) {
            var comm = Utils.commentToBack(Utils.getFieldComment(methods[i]))
            if(comm.isNotEmpty()){
                text.append("\t")
                text.append(comm)
            }
            text.append("\t")
            text.append(toFunc(methods[i]))
            text.append(" \n\n")
        }

        text.append("}\n\n")
        WindowFactory.show(project,text.toString())
    }

    private fun toFunc(method: GoMethodDeclaration): String {
        var text = method.text;
        var start = text.indexOf(")")
        var end = text.indexOf("{")
        return text.substring(start + 1, end)
    }

}
