package com.tecsup.cookplan.data.remote

import java.text.Normalizer
import java.util.Locale

object MealSearchQueryTranslator {
    private val spanishToEnglish = mapOf(
        "pollo" to "chicken",
        "res" to "beef",
        "carne" to "beef",
        "cerdo" to "pork",
        "pescado" to "fish",
        "mariscos" to "seafood",
        "camaron" to "seafood",
        "camarones" to "seafood",
        "cordero" to "lamb",
        "pasta" to "pasta",
        "arroz" to "rice",
        "huevo" to "egg",
        "huevos" to "egg",
        "sopa" to "soup",
        "ensalada" to "salad",
        "postre" to "dessert",
        "torta" to "cake",
        "pastel" to "cake",
        "panqueque" to "pancake",
        "desayuno" to "breakfast",
        "vegetariano" to "vegetarian",
        "vegano" to "vegan",
        "papas" to "potato",
        "papa" to "potato",
        "tomate" to "tomato",
        "queso" to "cheese"
    )

    fun translate(query: String): String {
        val normalized = query.trim().withoutAccents().lowercase(Locale.ROOT)
        return spanishToEnglish[normalized] ?: query.trim()
    }

    private fun String.withoutAccents(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
    }
}
