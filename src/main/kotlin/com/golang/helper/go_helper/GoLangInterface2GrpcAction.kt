package com.golang.helper.go_helper

import com.goide.actions.GoIntentionBasedAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class GoLangInterface2GrpcAction @JvmOverloads
constructor(intention: IntentionAction = GoLangInterface2GrpcIntention()) :
        GoIntentionBasedAction(intention) {

}
