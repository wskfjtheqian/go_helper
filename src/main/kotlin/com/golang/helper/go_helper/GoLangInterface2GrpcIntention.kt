package com.golang.helper.go_helper

import com.goide.intentions.GoBaseIntentionAction
import com.goide.psi.*
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class GoLangInterface2GrpcIntention : GoBaseIntentionAction() {
    override fun getFamilyName(): String {
        return "Interface to grpc"
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        var element = PsiTreeUtil.findFirstParent(element) { element -> element is GoTypeSpec }
        var iface = PsiTreeUtil.findChildOfType(element, GoInterfaceType::class.java)
        return null != iface
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        var type = PsiTreeUtil.findFirstParent(element) { element -> element is GoTypeSpec } as GoTypeSpec
        var iface = PsiTreeUtil.findChildOfType(type, GoInterfaceType::class.java)

        var text = StringBuilder()
        text.append("service ")
        text.append(Utils.uToLine(type.identifier.text))

        text.append(" {\n")

        var msg = StringBuilder()
        iface!!.methods.forEach {
            text.append("\t rpc ")
            text.append(Utils.uToLine(it.name!!))
            text.append("(")
            text.append(Utils.uToLine(it.name!!))
            text.append("_req) returns (")
            text.append(Utils.uToLine(it.name!!))
            text.append("_resp); \n\n")

            toReq(msg, it)
            toResp(msg, it)
        }

        text.append("}\n\n")

        WindowFactory.show(project,text.toString() + msg.toString())
    }

    private fun toReq(text: StringBuilder, method: GoMethodSpec) {
        text.append("message ")
        text.append(Utils.uToLine(method.name!!))
        text.append("_req { \n")

        var parameters = method.signature!!.parameters.definitionList
        var parametersTypes = method.signature!!.parameters.parameterDeclarationList

        var index = 0
        for (i in 0 until parameters.size) {
            text.append("\t")
            text.append(Utils.toType(parametersTypes[index].type!!, true))
            text.append(" ")
            text.append(Utils.uToLine(parameters[i].name!!))
            text.append(" = ")
            text.append(i + 1)
            text.append("; \n")
            if (parameters[i].nextSibling.text == " ") {
                index++
            }
        }

        text.append("}\n\n")
    }

    private fun toResp(text: StringBuilder, method: GoMethodSpec) {
        text.append("message ")
        text.append(Utils.uToLine(method.name!!))
        text.append("_resp { \n")

        var result = method.signature!!.result
        if (null != result) {
            if (result.type != null) {
                if (result.type!!.text != "error") {
                    text.append("\t")
                    text.append(Utils.toType(result.type!!, true))
                    text.append(" ")
                    text.append("result")
                    text.append(" = ")
                    text.append(1)
                    text.append("; \n")
                }
            } else if (result.parameters != null) {
                var parameters = result.parameters!!.definitionList
                var parametersTypes = result.parameters!!.parameterDeclarationList

                var index = 1
                for (i in 0 until parametersTypes.size) {
                    if (parametersTypes[i].type!!.text != "error") {
                        text.append("\t")
                        text.append(Utils.toType(parametersTypes[i].type!!, true))
                        text.append(" ")

                        if (parameters != null && !parameters.isEmpty()) {
                            text.append(Utils.uToLine(parameters[i].identifier.text))
                        } else {
                            text.append("result")
                            text.append(index)
                        }

                        text.append(" = ")
                        text.append(index)
                        text.append("; \n")
                        index++
                    }
                }
            }
        }

        text.append("}\n\n")
    }


}
