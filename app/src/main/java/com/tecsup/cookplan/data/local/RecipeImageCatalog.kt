package com.tecsup.cookplan.data.local

import androidx.annotation.DrawableRes
import com.tecsup.cookplan.R

data class RecipeImageOption(
    val key: String,
    val title: String,
    val category: String,
    @param:DrawableRes val resId: Int
)

object RecipeImageCatalog {
    val options = listOf(
        RecipeImageOption("cookplan_arroz_con_pollo", "Arroz con pollo", "Almuerzo", R.drawable.cookplan_arroz_con_pollo),
        RecipeImageOption("cookplan_caldo_gallina", "Caldo de gallina", "Cena", R.drawable.cookplan_caldo_gallina),
        RecipeImageOption("cookplan_jugo_papaya", "Jugo de papaya", "Desayuno", R.drawable.cookplan_jugo_papaya),
        RecipeImageOption("cookplan_lomo_saltado", "Lomo saltado", "Almuerzo", R.drawable.cookplan_lomo_saltado),
        RecipeImageOption("cookplan_papa_huancaina", "Papa a la huancaina", "Entrada", R.drawable.cookplan_papa_huancaina),
        RecipeImageOption("cookplan_papa_rellena", "Papa rellena", "Almuerzo", R.drawable.cookplan_papa_rellena),
        RecipeImageOption("cookplan_pollo_plancha", "Pollo a la plancha", "Almuerzo", R.drawable.cookplan_pollo_plancha),
        RecipeImageOption("cookplan_pure_de_papa", "Pure de papa", "Guarnicion", R.drawable.cookplan_pure_de_papa),
        RecipeImageOption("cookplan_quinua", "Quinua", "Desayuno", R.drawable.cookplan_quinua),
        RecipeImageOption("cookplan_tequenos", "Tequenos", "Entrada", R.drawable.cookplan_tequenos),
        RecipeImageOption("cookplan_trucha_frita", "Trucha frita", "Almuerzo", R.drawable.cookplan_trucha_frita)
    )

    fun byKey(key: String?): RecipeImageOption? = options.firstOrNull { it.key == key }
}
