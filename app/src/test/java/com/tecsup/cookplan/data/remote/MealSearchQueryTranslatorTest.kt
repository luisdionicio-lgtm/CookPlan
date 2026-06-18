package com.tecsup.cookplan.data.remote

import org.junit.Assert.assertEquals
import org.junit.Test

class MealSearchQueryTranslatorTest {
    @Test
    fun translate_mapsCommonSpanishTermsToEnglish() {
        assertEquals("chicken", MealSearchQueryTranslator.translate("pollo"))
        assertEquals("beef", MealSearchQueryTranslator.translate("carne"))
        assertEquals("seafood", MealSearchQueryTranslator.translate("camarón"))
    }

    @Test
    fun translate_keepsUnknownSearchesAsTypedWithoutOuterSpaces() {
        assertEquals("burger", MealSearchQueryTranslator.translate("  burger  "))
    }
}
