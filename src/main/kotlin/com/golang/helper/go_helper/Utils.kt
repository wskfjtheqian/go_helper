package com.golang.helper.go_helper

import com.goide.psi.GoArrayOrSliceType
import com.goide.psi.GoMapType
import com.goide.psi.GoPointerType
import com.goide.psi.GoType
import com.goide.psi.impl.GoCommentImpl
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

object Utils {
    //　下划线
    fun nameUnderline(text: String): String {
        val list: MutableList<String> = split(text)

        val buffer = StringBuffer()
        list.forEach {
            var temp = it.replace("[_]+".toRegex(), "")
            if (temp.isNotEmpty()) {
                buffer.append("_")
                buffer.append(temp.lowercase(Locale.getDefault()))
            }
        }
        return buffer.substring(1)
    }

    //首字母大写
    fun nameCapitalized(text: String): String {
        val list: MutableList<String> = split(text)
        val buffer = StringBuffer()
        list.forEach {
            var temp = it.replace("[_]+".toRegex(), "")
            if (temp.isNotEmpty()) {
                buffer.append(temp.substring(0, 1).uppercase(Locale.getDefault()))
                buffer.append(temp.substring(1).lowercase(Locale.getDefault()))
            }
        }
        return buffer.toString()
    }


    //首字母小写
    fun nameLowercase(text: String): String {
        val list: MutableList<String> = split(text)
        val buffer = StringBuffer()
        var index = 0
        list.forEach {
            var temp = it.replace("[_]+".toRegex(), "")
            if (temp.isNotEmpty()) {
                if (index == 0) {
                    buffer.append(temp.substring(0, 1).lowercase(Locale.getDefault()))
                } else {
                    buffer.append(temp.substring(0, 1).uppercase(Locale.getDefault()))
                }
                buffer.append(temp.substring(1).lowercase(Locale.getDefault()))
            }
        }
        return buffer.toString()
    }

    private fun split(text: String): MutableList<String> {
        val matcher = Pattern.compile("[A-Z_]").matcher(text)
        val list: MutableList<String> = ArrayList()

        var index = 0
        while (matcher.find()) {
            list.add(text.substring(index, matcher.start()))
            index = matcher.start()
        }
        if (index < text.length) {
            list.add(text.substring(index))
        }
        return list
    }

    var typeMap = mapOf(
            "int" to "uint64",
            "uint" to "uint32",
            "int8" to "int32",
            "uint8" to "uint32",
            "int16" to "int32",
            "uint16" to "uint32",
            "int32" to "int32",
            "uint32" to "uint32",
            "int64" to "int64",
            "uint64" to "uint64",
            "float32" to "float",
            "float64" to "double",
            "string" to "string",
            "bool" to "bool",
    )

    fun toType(typ: GoType, b: Boolean): String {
        if (typ is GoArrayOrSliceType) {
            var temp = toType(typ.type, false)
            if(temp == "Byte"){
                return "bytes"
            }
            return "repeated " +temp
        } else if (typ is GoMapType) {
            return "map<" + toType(typ.keyType!!, true) + ", " + toType(typ.valueType!!, true) + ">"
        } else if (typ is GoPointerType) {
            if (b) {
                return "optional " + toType(typ.type!!, true)
            }
            return toType(typ.type!!, true)
        }
        var text = typ.presentationText
        var value = typeMap[text]
        if (value != null) {
            return value
        }
        return nameCapitalized(deletePackage(text))
    }

    fun getFieldComment(element: PsiElement): MutableList<String> {
        val list: MutableList<String> = ArrayList()
        var next = element.prevSibling
        while (null != next && (next is GoCommentImpl || next is PsiWhiteSpace)) {
            if (next is GoCommentImpl && isSelfComment(next.prevSibling)) {
                list.add(next.text.substring(2))
            }
            next = next.prevSibling
        }

        next = element.nextSibling
        while (null != next && (next is GoCommentImpl || (next is PsiWhiteSpace && !next.text.contains("\n")))) {
            if (next is GoCommentImpl) {
                list.add(next.text.substring(2))
                break
            }
            next = next.nextSibling
        }
        return list
    }

    private fun isSelfComment(value: PsiElement?): Boolean {
        var next = value
        while (null != next && next is PsiWhiteSpace) {
            if (next.text.contains("\n")) {
                return true
            }
            next = next.prevSibling
        }
        return false
    }


    fun commentToBack(list: MutableList<String>): String {
        val buffer = StringBuffer()
        list.forEach {
            buffer.append("//").append(it).append("\n")
        }
        return buffer.toString()
    }

    fun commentToLine(list: MutableList<String>): String {
        val buffer = StringBuffer()
        list.forEach {
            if (buffer.isNotEmpty()) {
                buffer.append(", ")
            }
            buffer.append(it)
        }
        return buffer.insert(0, "\t//").toString()
    }

    //删除包名
    fun deletePackage(text: String): String {
        val index = text.indexOf(".")
        if (-1 != index) {
            return text.substring(index + 1)
        }
        return text
    }
}
