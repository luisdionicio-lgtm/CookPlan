package com.tecsup.cookplan.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MealResponse(
    @SerializedName("meals") val meals: List<MealDto>?
)

data class MealDto(
    @SerializedName("idMeal") val id: String,
    @SerializedName("strMeal") val name: String,
    @SerializedName("strInstructions") val instructions: String?,
    @SerializedName("strMealThumb") val thumbUrl: String?,
    @SerializedName("strIngredient1") val ing1: String?,
    @SerializedName("strIngredient2") val ing2: String?,
    @SerializedName("strIngredient3") val ing3: String?,
    @SerializedName("strMeasure1") val ms1: String?,
    @SerializedName("strMeasure2") val ms2: String?,
    @SerializedName("strMeasure3") val ms3: String?
) {
    fun toIngredientsString(): String {
        val list = mutableListOf<String>()
        if (!ing1.isNullOrBlank()) list.add("$ms1 $ing1".trim())
        if (!ing2.isNullOrBlank()) list.add("$ms2 $ing2".trim())
        if (!ing3.isNullOrBlank()) list.add("$ms3 $ing3".trim())
        return list.joinToString(", ")
    }
}
