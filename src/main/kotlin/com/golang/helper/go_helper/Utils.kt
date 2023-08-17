package com.golang.helper.go_helper

import com.goide.psi.GoArrayOrSliceType
import com.goide.psi.GoMapType
import com.goide.psi.GoPointerType
import com.goide.psi.GoType
import java.util.*
import java.util.regex.Pattern

object Utils {
    fun uToLine(text: String): String {
        val matcher = Pattern.compile("[A-Z_]").matcher(text)
        val buffer = StringBuffer()
        var index = 0
        while (matcher.find()) {
            buffer.append("_")
            buffer.append(text.substring(index, matcher.start()))
            index = matcher.start()
        }
        if (index < text.length) {
            buffer.append("_")
            buffer.append(text.substring(index))
        }
        var temp = buffer.toString().replace("[_]+".toRegex(), "_").substring(1).lowercase(Locale.getDefault())
        index = temp.indexOf(".")
        if (-1 != index) {
            temp = temp.substring(index + 1)
        }
        if (temp.startsWith("_")) {
            temp = temp.substring(1)
        }
        return temp
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
        return uToLine(text)
    }
}
