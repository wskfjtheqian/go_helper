package com.golang.helper.go_helper

import com.goide.actions.GoIntentionBasedAction
import com.intellij.codeInsight.intention.IntentionAction

class GoLangStruct2ProtobufAction @JvmOverloads
constructor(intention: IntentionAction = GoLangStruct2ProtobufIntention()) :
        GoIntentionBasedAction(intention) {

}
