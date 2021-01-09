package me.scana.okgradle

class Copys {
    companion object {
        const val TITLE = "Ok, Gradle!"
        const val ADD_DEPENDENCY_ACTION = "Add dependency"
        const val MODULES_TITLE = "Select a module:"
        const val COPY_TO_CLIPBOARD_ACTION = "Copy to clipboard"

        const val PROMPT_HEADER = "Which library do you need?"
        const val RESULT_LIST_TITLE = "Select it from the list:"

        const val DEPENDENCY_ADDED_TITLE = "Dependency added to %s"
        const val DEPENDENCY_COPIED_TITLE = "Copied!"
        const val DEPENDENCY_COPIED_MSG = "Dependency statements have been copied to your clipboard."
        const val TIP = "Tip: %s"
        val TIPS = listOf(
                "Press down key after typing a search phrase to jump to the list directly",
                "Press enter after selecting an artifact from the list to automatically add it",
                "Some repositories might take longer to search through - give it a while",
                "You can set a custom shortcut for easier access to Ok, Gradle!"
        )
        const val SUGGESTION_FIRST_PART = "(did you mean: "
        const val SUGGESTION_LAST_PART = "?)"
        const val INPUT_HINT = "try typing \'retrofit\'"
        const val ADD_DEPENDENCY = "Add dependency"
    }
}
