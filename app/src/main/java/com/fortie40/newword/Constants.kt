package com.fortie40.newword

// key for passing bundle from wordsFragment to DeleteDialog
const val NUMBER_OF_ITEMS = "number_of_items"

// delete dialog tag when dialog.show is called
const val DELETE_DIALOG = "delete_dialog"

// delete dialog progress tag when dialog.show is called
const val DELETE_DIALOG_PROGRESS = "delete_dialog_progress"

// key for passing bundle from wordsFragment to DeleteDialogProgress
const val NUMBER_OF_ITEMS_TO_DELETE = "number_of_items_to_delete"

// progress min and max for progress bar in delete_dialog_progress
// progress is set in DeleteDialogProgress class
const val PROGRESS_MIN = 0
const val PROGRESS_MAX = 100

// selection id for SelectionTracker.Builder
const val MY_SELECTION = "my_selection"

// Save ActionMode state boolean in a bundle
// WordsFragment onSaveInstanceState
const val IS_IN_ACTION_MODE = "is_in_action_mode"