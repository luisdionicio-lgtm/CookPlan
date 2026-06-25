package com.tecsup.cookplan.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.cookplan.data.local.MealPlanEntity
import com.tecsup.cookplan.data.local.RecipeEntity
import kotlinx.coroutines.tasks.await

class FirestoreSyncRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val uid: String?
        get() = auth.currentUser?.uid

    fun hasUser(): Boolean = uid != null

    suspend fun saveCurrentUserProfile(email: String?) {
        val userId = uid ?: return
        val data = mutableMapOf<String, Any?>(
            "uid" to userId,
            "createdOrUpdatedAt" to Timestamp.now()
        )
        if (!email.isNullOrBlank()) {
            data["email"] = email
        }

        firestore.collection("users")
            .document(userId)
            .set(data, com.google.firebase.firestore.SetOptions.merge())
            .await()
    }

    suspend fun saveRecipe(recipe: RecipeEntity) {
        val userId = uid ?: return
        firestore.collection("users")
            .document(userId)
            .collection("recetas")
            .document(recipe.id.toString())
            .set(recipe.toFirestoreMap())
            .await()
    }

    suspend fun deleteRecipe(recipeId: Long) {
        val userId = uid ?: return
        firestore.collection("users")
            .document(userId)
            .collection("recetas")
            .document(recipeId.toString())
            .delete()
            .await()
    }

    suspend fun saveMealPlan(mealPlan: MealPlanEntity) {
        val userId = uid ?: return
        firestore.collection("users")
            .document(userId)
            .collection("plan")
            .document(mealPlan.id.toString())
            .set(mealPlan.toFirestoreMap())
            .await()
    }

    suspend fun deleteMealSlot(day: String, mealType: String) {
        val userId = uid ?: return
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("plan")
            .whereEqualTo("diaSemana", day)
            .whereEqualTo("tipoComida", mealType)
            .get()
            .await()

        snapshot.documents.forEach { doc ->
            doc.reference.delete().await()
        }
    }

    suspend fun pushAll(recipes: List<RecipeEntity>, plans: List<MealPlanEntity>) {
        if (!hasUser()) return
        recipes.forEach { saveRecipe(it) }
        plans.forEach { saveMealPlan(it) }
    }

    suspend fun fetchRecipes(): List<RecipeEntity> {
        val userId = uid ?: return emptyList()
        return firestore.collection("users")
            .document(userId)
            .collection("recetas")
            .get()
            .await()
            .documents
            .mapNotNull { it.toRecipeEntity() }
    }

    suspend fun fetchPlans(): List<MealPlanEntity> {
        val userId = uid ?: return emptyList()
        return firestore.collection("users")
            .document(userId)
            .collection("plan")
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                val id = doc.id.toLongOrNull() ?: doc.getLong("id") ?: return@mapNotNull null
                val day = doc.getString("diaSemana") ?: return@mapNotNull null
                val mealType = doc.getString("tipoComida") ?: return@mapNotNull null
                val recipeId = doc.getLong("recetaId") ?: return@mapNotNull null
                val recipeName = doc.getString("recetaNombre") ?: ""
                MealPlanEntity(
                    id = id,
                    dayOfWeek = day,
                    mealType = mealType,
                    recipeId = recipeId,
                    recipeName = recipeName
                )
            }
    }

    private fun RecipeEntity.toFirestoreMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "nombre" to name,
        "categoria" to category,
        "ingredientes" to ingredients,
        "pasos" to instructions,
        "tiempoMin" to timeMinutes,
        "porciones" to servings,
        "imagenRuta" to imageUrl,
        "imagenUrl" to imageUrl,
        "esDeApi" to isExternal,
        "updatedAt" to Timestamp.now()
    )

    private fun MealPlanEntity.toFirestoreMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "diaSemana" to dayOfWeek,
        "tipoComida" to mealType,
        "recetaId" to recipeId,
        "recetaNombre" to recipeName,
        "updatedAt" to Timestamp.now()
    )

    private fun com.google.firebase.firestore.DocumentSnapshot.toRecipeEntity(): RecipeEntity? {
        val id = id.toLongOrNull() ?: getLong("id") ?: return null
        val name = getString("nombre") ?: return null
        return RecipeEntity(
            id = id,
            name = name,
            category = getString("categoria"),
            ingredients = getString("ingredientes") ?: "",
            instructions = getString("pasos") ?: "",
            timeMinutes = getLong("tiempoMin")?.toInt(),
            servings = getLong("porciones")?.toInt(),
            isExternal = getBoolean("esDeApi") ?: false,
            imageUrl = getString("imagenRuta") ?: getString("imagenUrl")
        )
    }
}
