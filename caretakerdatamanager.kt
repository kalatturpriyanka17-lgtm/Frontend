package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences

/**
 * Simple data manager to store and retrieve caretaker information
 * In a real app, this would use a database
 */
object CaretakerDataManager {
    
    private const val PREFS_NAME = "caretaker_prefs"
    private const val KEY_HAS_CARETAKER = "has_caretaker"
    private const val KEY_CARETAKER_NAME = "caretaker_name"
    private const val KEY_CARETAKER_RELATION = "caretaker_relation"
    private const val KEY_CARETAKER_PHONE = "caretaker_phone"
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Save caretaker information
     */
    fun saveCaretaker(context: Context, name: String, relation: String, phone: String) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_HAS_CARETAKER, true)
            putString(KEY_CARETAKER_NAME, name)
            putString(KEY_CARETAKER_RELATION, relation)
            putString(KEY_CARETAKER_PHONE, phone)
            apply()
        }
    }
    
    /**
     * Check if caretaker is added
     */
    fun hasCaretaker(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_HAS_CARETAKER, false)
    }
    
    /**
     * Get caretaker name
     */
    fun getCaretakerName(context: Context): String {
        return getPrefs(context).getString(KEY_CARETAKER_NAME, "") ?: ""
    }
    
    /**
     * Get caretaker relation
     */
    fun getCaretakerRelation(context: Context): String {
        return getPrefs(context).getString(KEY_CARETAKER_RELATION, "") ?: ""
    }
    
    /**
     * Get caretaker phone
     */
    fun getCaretakerPhone(context: Context): String {
        return getPrefs(context).getString(KEY_CARETAKER_PHONE, "") ?: ""
    }
    
    /**
     * Remove caretaker
     */
    fun removeCaretaker(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
    
    /**
     * Get all caretaker info as a data class
     */
    data class CaretakerInfo(
        val name: String,
        val relation: String,
        val phone: String
    )
    
    fun getCaretakerInfo(context: Context): CaretakerInfo? {
        return if (hasCaretaker(context)) {
            CaretakerInfo(
                getCaretakerName(context),
                getCaretakerRelation(context),
                getCaretakerPhone(context)
            )
        } else {
            null
        }
    }
}
