package com.golang.helper.go_helper

import com.goide.intentions.GoBaseIntentionAction
import com.goide.psi.*
import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class GoLangGrpcImplementIntention : GoBaseIntentionAction(), HighPriorityAction {
    override fun getFamilyName(): String {
        return "Grpc　Implement"
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        var element = PsiTreeUtil.findFirstParent(element) { element -> element is GoTypeSpec }
        var iface = PsiTreeUtil.findChildOfType(element, GoInterfaceType::class.java)
        return null != iface
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        var method = PsiTreeUtil.findFirstParent(element) { element -> element is GoMethodSpec } as GoMethodSpec
        if (null != method) {
            WindowFactory.show(project, toMethod(method))
            return
        }

        var type = PsiTreeUtil.findFirstParent(element) { element -> element is GoTypeSpec } as GoTypeSpec
        var iface = PsiTreeUtil.findChildOfType(type, GoInterfaceType::class.java)

        var text = StringBuilder()
        text.append(Utils.commentToBack(Utils.getFieldComment(type.parent)))
        text.append("service ")
        text.append(Utils.nameUnderline(type.identifier.text))

        text.append(" {\n")

        var msg = StringBuilder()
        iface!!.methods.forEach {
            text.append(toMethod(it))
        }

        text.append("}\n\n")

        WindowFactory.show(project, text.toString() + msg.toString())
    }

    private fun toMethod(method: GoMethodSpec?): String {
        if (method == null) {
            return "";
        }
        var text = StringBuilder()
        text.append("func (s *Server) ")
        text.append(Utils.nameCapitalized(method.name!!))
        text.append("(ctx, rep *proto.")
        text.append(Utils.nameCapitalized(method.name!!))
        text.append("Rep) (*proto.")
        text.append(Utils.nameCapitalized(method.name!!))
        text.append(", error) {\n")
        text.append("\t")

        toVal(text, method)

        text.append(" err := u.Repo.")
        text.append(Utils.nameCapitalized(method.name!!))
        text.append("(ctx, \n")
        toReq(text, method)
        text.append(")\n")
        text.append("\tif err != nil {\n")
        text.append("\t\treturn nil, err\n")
        text.append("\t}\n")

        text.append("\treturn &proto.")
        toResp(text, method)
        text.append(", nil\n")
        text.append("}\n\n")
        return text.toString();
    }

    private fun toReq(text: StringBuilder, method: GoMethodSpec) {
        val parameters = method.signature!!.parameters.definitionList

        for (i in 0 until parameters.size) {
            text.append("\t\trep.Get")
            text.append(Utils.nameCapitalized(parameters[i].name!!))
            text.append("(), \n")
        }
        text.append("\t}")
    }

    private fun toVal(text: StringBuilder, method: GoMethodSpec) {
        var result = method.signature!!.result
        if (null != result) {
            if (result.type != null) {
                if (result.type!!.text != "error") {
                    text.append("result")
                    text.append(",")
                }
            } else if (result.parameters != null) {
                val parameters = result.parameters!!.definitionList
                val parametersTypes = result.parameters!!.parameterDeclarationList

                for (i in 0 until parametersTypes.size) {
                    if (parametersTypes[i].type!!.text != "error") {
                        if (parameters.isNotEmpty()) {
                            text.append(Utils.nameLowercase(parameters[i].identifier.text))
                        } else {
                            text.append("result")
                        }
                        text.append(", ")
                    }
                }
            }
        }

    }

    private fun toResp(text: StringBuilder, method: GoMethodSpec) {
        text.append(Utils.nameCapitalized(method.name!!))
        text.append(" { \n")

        var result = method.signature!!.result
        if (null != result) {
            if (result.type != null) {
                if (result.type!!.text != "error") {
                    text.append("\t\tResult: result")
                    text.append(",\n")
                }
            } else if (result.parameters != null) {
                val parameters = result.parameters!!.definitionList
                val parametersTypes = result.parameters!!.parameterDeclarationList

                for (i in 0 until parametersTypes.size) {
                    if (parametersTypes[i].type!!.text != "error") {
                        text.append("\t\t")
                        if (parameters.isNotEmpty()) {
                            text.append(Utils.nameCapitalized(parameters[i].identifier.text))
                            text.append(": ")
                            text.append(Utils.nameLowercase(parameters[i].identifier.text))
                        } else {
                            text.append("Result: result")
                        }
                        text.append(", \n")
                    }
                }
            }
        }

        text.append("\t}")
    }


}
