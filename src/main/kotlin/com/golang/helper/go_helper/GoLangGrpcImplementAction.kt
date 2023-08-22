package com.golang.helper.go_helper

import com.goide.actions.GoIntentionBasedAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.actionSystem.AnAction

class GoLangGrpcImplementAction @JvmOverloads
constructor(intention: IntentionAction = GoLangGrpcImplementIntention()) :
        GoIntentionBasedAction(intention) {

}