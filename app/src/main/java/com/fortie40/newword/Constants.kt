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