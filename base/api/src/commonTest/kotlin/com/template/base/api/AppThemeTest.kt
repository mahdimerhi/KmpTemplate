package com.template.base.api

import kotlin.test.Test
import kotlin.test.assertEquals

class AppThemeTest {
    @Test
    fun `fromString should return SYSTEM for null`() {
        val result = AppTheme.fromString(null)
        assertEquals(AppTheme.SYSTEM, result)
    }

    @Test
    fun `fromString should return LIGHT for LIGHT`() {
        val result = AppTheme.fromString("LIGHT")
        assertEquals(AppTheme.LIGHT, result)
    }

    @Test
    fun `fromString should return DARK for DARK`() {
        val result = AppTheme.fromString("DARK")
        assertEquals(AppTheme.DARK, result)
    }

    @Test
    fun `fromString should return SYSTEM for SYSTEM`() {
        val result = AppTheme.fromString("SYSTEM")
        assertEquals(AppTheme.SYSTEM, result)
    }
}