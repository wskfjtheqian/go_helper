package com.golang.helper.go_helper

import com.goide.actions.GoIntentionBasedAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class GoLangStruct2InterfaceAction  @JvmOverloads
constructor(intention: IntentionAction = GoLangStruct2InterfaceIntention()) :
        GoIntentionBasedAction(intention) {

}
