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

    fun toType(typ: GoType, b: Boolean): String {
        if (typ is GoArrayOrSliceType) {
            return "repeated " + toType(typ.type, false)
        } else if (typ is GoMapType) {
            return "map<" + toType(typ.keyType!!, true) + ", " + toType(typ.valueType!!, true) + ">"
        } else if (typ is GoPointerType) {
            if (b) {
                return "optional " + toType(typ.type!!, true)
            }
            return toType(typ.type!!, true)
        }
        var text = typ.presentationText
        if (text == "int") {
            return "int32"
        } else if (text == "int8") {
            return "int32"
        }
        return nameUnderline(text)
    }

    fun getFieldComment(element: PsiElement): MutableList<String> {
        val list: MutableList<String> = ArrayList()
        var next = element.prevSibling
        while (null != next && (next is GoCommentImpl || next is PsiWhiteSpace)) {
            if (next is GoCommentImpl) {
                list.add(next.text.substring(2))
            }
            next = next.prevSibling
        }

        next = element
        while (null != next && next.text.contains("\n")) {
            if (next is GoCommentImpl) {
                list.add(next.text.substring(2))
                break
            }
            next = next.nextSibling
        }
        return list
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
            buffer.append(", ").append(it)
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
