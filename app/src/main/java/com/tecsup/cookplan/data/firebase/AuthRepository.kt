package com.tecsup.cookplan.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Fuente única para la autenticación con Firebase. Las vistas/ViewModels no usan
 * FirebaseAuth directamente (RNF-02).
 */
class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    val currentUser: FirebaseUser? get() = auth.currentUser

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun login(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onResult(
                    if (task.isSuccessful) Result.success(Unit)
                    else Result.failure(task.exception ?: Exception("Error desconocido"))
                )
            }
    }

    fun register(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onResult(
                    if (task.isSuccessful) Result.success(Unit)
                    else Result.failure(task.exception ?: Exception("Error desconocido"))
                )
            }
    }

    fun logout() = auth.signOut()
}
