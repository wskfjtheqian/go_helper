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
        val text = StringBuilder()
        val msg = StringBuilder()
        val method = PsiTreeUtil.findFirstParent(element) { element -> element is GoMethodSpec } as GoMethodSpec?

        if (null != method) {
            extracted(method, text, msg)
        }else{
            var type = PsiTreeUtil.findFirstParent(element) { element -> element is GoTypeSpec } as GoTypeSpec
            var iface = PsiTreeUtil.findChildOfType(type, GoInterfaceType::class.java)

            text.append(Utils.commentToBack(Utils.getFieldComment(type.parent)))
            text.append("service ")
            text.append(Utils.nameCapitalized(Utils.deletePackage(type.identifier.text)))

            text.append(" {\n")

            iface!!.methods.forEach {
                extracted(it, text, msg)
            }
            text.append("}\n\n")
        }

        WindowFactory.show(project, text.toString() + msg.toString())
    }

    private fun extracted(it: GoMethodSpec, text: StringBuilder, msg: StringBuilder) {
        var comm = Utils.commentToBack(Utils.getFieldComment(it))
        if (comm.isNotEmpty()) {
            text.append("\t")
            text.append(comm)
        }
        text.append("\t rpc ")
        text.append(Utils.nameCapitalized(Utils.deletePackage(it.name!!)))
        text.append("(")
        text.append(Utils.nameCapitalized(Utils.deletePackage(it.name!!)))
        text.append("Req) returns (")
        text.append(Utils.nameCapitalized(Utils.deletePackage(it.name!!)))
        text.append("Resp); \n\n")

        toReq(msg, it, comm)
        toResp(msg, it, comm)
    }

    private fun toReq(text: StringBuilder, method: GoMethodSpec, comm: String) {
        if(comm.isNotEmpty()){
            text.append(comm)
            text.append("// 请求")
            text.append("\n")
        }

        text.append("message ")
        text.append(Utils.nameCapitalized(Utils.deletePackage(method.name!!)))
        text.append("Req { \n")

        val parameters = method.signature!!.parameters.definitionList
        val parametersTypes = method.signature!!.parameters.parameterDeclarationList

        var index = 0
        for (i in 0 until parameters.size) {
            text.append("\t")
            text.append(Utils.toType(parametersTypes[index].type!!, true))
            text.append(" ")
            text.append(Utils.nameUnderline(Utils.deletePackage(parameters[i].name!!)))
            text.append(" = ")
            text.append(i + 1)
            text.append("; \n")
            if (parameters[i].nextSibling.text == " ") {
                index++
            }
        }

        text.append("}\n\n")
    }

    private fun toResp(text: StringBuilder, method: GoMethodSpec, comm: String) {
        if(comm.isNotEmpty()){
            text.append(comm)
            text.append("// 响应")
            text.append("\n")
        }
        text.append("message ")
        text.append(Utils.nameCapitalized(Utils.deletePackage(method.name!!)))
        text.append("Resp { \n")

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
                            text.append(Utils.nameUnderline(Utils.deletePackage(parameters[i].identifier.text)))
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
