package com.tecsup.cookplan.ui.planner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.cookplan.CookPlanApplication
import com.tecsup.cookplan.notifications.CookPlanNotificationHelper
import com.tecsup.cookplan.viewmodel.PlannerViewModel
import com.tecsup.cookplan.viewmodel.PlannerViewModelFactory
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private val DIAS = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
private val COMIDAS = listOf("Desayuno", "Almuerzo", "Cena")

@Composable
fun PlannerScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as CookPlanApplication
    val viewModel: PlannerViewModel = viewModel(
        factory = PlannerViewModelFactory(app.mealPlanRepository, app.recipeRepository)
    )
    val uiState by viewModel.uiState.collectAsState()

    var dialogTarget by remember { mutableStateOf<Pair<String, String>?>(null) }

    val monday = remember { LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong()) }
    val sunday = monday.plusDays(6)
    val mes = sunday.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es"))
    val rango = "Semana del ${monday.dayOfMonth} al ${sunday.dayOfMonth} de $mes"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Mi Plan Semanal", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(rango, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DIAS.forEachIndexed { index, dia ->
            val fecha = monday.plusDays(index.toLong())
            val esHoy = fecha == LocalDate.now()

            DayRow(
                dia = dia,
                fecha = fecha.dayOfMonth.toString(),
                esHoy = esHoy,
                meals = uiState.plan[dia] ?: emptyMap(),
                onAddMeal = { comida -> dialogTarget = dia to comida }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // El mismo Dialog de antes...
    dialogTarget?.let { (dia, comida) ->
        val current = uiState.plan[dia]?.get(comida)
        AlertDialog(
            onDismissRequest = { dialogTarget = null },
            title = { Text("$dia · $comida") },
            text = {
                if (uiState.recipes.isEmpty()) {
                    Text("No tienes recetas todavía. Crea o importa alguna primero.")
                } else {
                    Column(
                        modifier = Modifier
                            .heightIn(max = 320.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        uiState.recipes.forEach { recipe ->
                            Text(
                                recipe.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.assign(dia, comida, recipe)
                                        CookPlanNotificationHelper.scheduleMealReminder(
                                            context = context,
                                            recipeId = recipe.id,
                                            recipeName = recipe.name,
                                            mealType = comida
                                        )
                                        dialogTarget = null
                                    }
                                    .padding(vertical = 12.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            HorizontalDivider()
                        }
                    }
                }
            },
            confirmButton = {
                if (current != null) {
                    TextButton(onClick = {
                        viewModel.remove(dia, comida)
                        dialogTarget = null
                    }) {
                        Text("Quitar", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogTarget = null }) { Text("Cerrar") }
            }
        )
    }
}

@Composable
fun DayRow(
    dia: String,
    fecha: String,
    esHoy: Boolean,
    meals: Map<String, com.tecsup.cookplan.data.local.MealPlanEntity>,
    onAddMeal: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esHoy) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) 
                            else MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = if (esHoy) CardDefaults.cardElevation(4.dp) else CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (esHoy) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        fecha,
                        color = if (esHoy) Color.White else MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    dia,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (esHoy) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                if (esHoy) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = MaterialTheme.colorScheme.primary, shape = CircleShape) {
                        Text("HOY", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                COMIDAS.forEach { type ->
                    val recipe = meals[type]
                    MealSlot(type, recipe?.recipeName) { onAddMeal(type) }
                }
            }
        }
    }
}

@Composable
fun RowScope.MealSlot(label: String, recipeName: String?, onClick: () -> Unit) {
    val isSet = recipeName != null
    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            color = if (isSet) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f) else Color.Transparent,
            border = if (isSet) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
                if (isSet) {
                    Text(
                        recipeName!!,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
