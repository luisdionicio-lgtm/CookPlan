package com.tecsup.cookplan.data.local

import androidx.annotation.DrawableRes
import com.tecsup.cookplan.R

data class RecipeImageOption(
    val key: String,
    val title: String,
    val category: String,
    val ingredients: String,
    val instructions: String,
    val timeMinutes: Int,
    val servings: Int,
    @param:DrawableRes val resId: Int
)

object RecipeImageCatalog {
    val options = listOf(
        RecipeImageOption(
            key = "cookplan_arroz_con_pollo",
            title = "Arroz con pollo",
            category = "Almuerzo",
            ingredients = "Arroz\nPollo\nCulantro\nArvejas\nZanahoria\nAji amarillo",
            instructions = "Dorar el pollo. Licuar culantro con aji y cocinar el aderezo. Agregar arroz, verduras y caldo. Cocinar hasta que el arroz quede graneado.",
            timeMinutes = 50,
            servings = 4,
            resId = R.drawable.cookplan_arroz_con_pollo
        ),
        RecipeImageOption(
            key = "cookplan_caldo_gallina",
            title = "Caldo de gallina",
            category = "Cena",
            ingredients = "Gallina\nFideos\nPapa\nHuevo\nKion\nCebolla china",
            instructions = "Hervir la gallina con kion hasta que suelte sabor. Agregar papa y fideos. Servir caliente con huevo y cebolla china.",
            timeMinutes = 75,
            servings = 4,
            resId = R.drawable.cookplan_caldo_gallina
        ),
        RecipeImageOption(
            key = "cookplan_jugo_papaya",
            title = "Jugo de papaya",
            category = "Desayuno",
            ingredients = "Papaya\nAgua o leche\nAzucar o miel\nHielo",
            instructions = "Pelar y cortar la papaya. Licuar con agua o leche. Endulzar ligeramente y servir frio.",
            timeMinutes = 8,
            servings = 2,
            resId = R.drawable.cookplan_jugo_papaya
        ),
        RecipeImageOption(
            key = "cookplan_lomo_saltado",
            title = "Lomo saltado",
            category = "Almuerzo",
            ingredients = "Carne de res\nCebolla\nTomate\nPapas fritas\nSillao\nVinagre\nArroz",
            instructions = "Saltear la carne a fuego alto. Agregar cebolla, tomate, sillao y vinagre. Mezclar con papas fritas y servir con arroz.",
            timeMinutes = 35,
            servings = 4,
            resId = R.drawable.cookplan_lomo_saltado
        ),
        RecipeImageOption(
            key = "cookplan_papa_huancaina",
            title = "Papa a la huancaina",
            category = "Entrada",
            ingredients = "Papa sancochada\nAji amarillo\nQueso fresco\nLeche\nGalleta\nHuevo\nAceituna",
            instructions = "Licuar aji, queso, leche y galleta hasta formar crema. Servir sobre papas con huevo y aceituna.",
            timeMinutes = 25,
            servings = 4,
            resId = R.drawable.cookplan_papa_huancaina
        ),
        RecipeImageOption(
            key = "cookplan_papa_rellena",
            title = "Papa rellena",
            category = "Almuerzo",
            ingredients = "Papa\nCarne molida\nCebolla\nHuevo\nAceituna\nHarina",
            instructions = "Prensar la papa sancochada. Preparar relleno de carne con cebolla. Formar, rellenar, enharinar y freir hasta dorar.",
            timeMinutes = 55,
            servings = 4,
            resId = R.drawable.cookplan_papa_rellena
        ),
        RecipeImageOption(
            key = "cookplan_pollo_plancha",
            title = "Pollo a la plancha",
            category = "Almuerzo",
            ingredients = "Pechuga de pollo\nAjo\nLimon\nSal\nPimienta\nEnsalada",
            instructions = "Sazonar el pollo con ajo, limon, sal y pimienta. Cocinar a la plancha por ambos lados y servir con guarnicion.",
            timeMinutes = 25,
            servings = 2,
            resId = R.drawable.cookplan_pollo_plancha
        ),
        RecipeImageOption(
            key = "cookplan_pure_de_papa",
            title = "Pure de papa",
            category = "Guarnicion",
            ingredients = "Papa amarilla\nLeche\nMantequilla\nSal",
            instructions = "Sancochar y prensar las papas. Mezclar con leche caliente, mantequilla y sal hasta lograr textura cremosa.",
            timeMinutes = 30,
            servings = 4,
            resId = R.drawable.cookplan_pure_de_papa
        ),
        RecipeImageOption(
            key = "cookplan_quinua",
            title = "Quinua",
            category = "Desayuno",
            ingredients = "Quinua\nAgua\nCanela\nClavo\nLeche\nAzucar",
            instructions = "Lavar la quinua. Cocinar con canela y clavo hasta que ablande. Agregar leche y endulzar al gusto.",
            timeMinutes = 30,
            servings = 4,
            resId = R.drawable.cookplan_quinua
        ),
        RecipeImageOption(
            key = "cookplan_tequenos",
            title = "Tequeños",
            category = "Entrada",
            ingredients = "Masa wantan\nQueso\nAceite\nSalsa de palta",
            instructions = "Enrollar queso en masa wantan y sellar bordes. Freir hasta dorar y servir con salsa.",
            timeMinutes = 20,
            servings = 4,
            resId = R.drawable.cookplan_tequenos
        ),
        RecipeImageOption(
            key = "cookplan_trucha_frita",
            title = "Trucha frita",
            category = "Almuerzo",
            ingredients = "Trucha\nAjo\nLimon\nHarina\nSal\nPimienta\nYuca o ensalada",
            instructions = "Sazonar la trucha con ajo, limon, sal y pimienta. Pasar por harina y freir hasta dorar.",
            timeMinutes = 30,
            servings = 2,
            resId = R.drawable.cookplan_trucha_frita
        )
    )

    fun byKey(key: String?): RecipeImageOption? = options.firstOrNull { it.key == key }
}
