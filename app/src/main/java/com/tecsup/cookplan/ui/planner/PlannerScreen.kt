package com.tecsup.cookplan.ui.planner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.cookplan.CookPlanApplication
import com.tecsup.cookplan.data.local.MealPlanEntity
import com.tecsup.cookplan.data.local.RecipeEntity
import com.tecsup.cookplan.notifications.CookPlanNotificationHelper
import com.tecsup.cookplan.ui.components.CookPlanHero
import com.tecsup.cookplan.ui.components.RecipeImage
import com.tecsup.cookplan.viewmodel.PlannerViewModel
import com.tecsup.cookplan.viewmodel.PlannerViewModelFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

private val DIAS = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
private val COMIDAS = listOf("Desayuno", "Almuerzo", "Cena")

@OptIn(ExperimentalMaterial3Api::class)
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
    val month = sunday.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es"))
    val range = "Semana del ${monday.dayOfMonth} al ${sunday.dayOfMonth} de $month"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        CookPlanHero(
            title = "Mi plan semanal",
            subtitle = range,
            icon = Icons.Default.CalendarToday,
            badge = "Recordatorios"
        )

        Spacer(modifier = Modifier.height(18.dp))

        DIAS.forEachIndexed { index, day ->
            val date = monday.plusDays(index.toLong())
            DayRow(
                dia = day,
                fecha = date.dayOfMonth.toString(),
                esHoy = date == LocalDate.now(),
                meals = uiState.plan[day] ?: emptyMap(),
                onAddMeal = { meal -> dialogTarget = day to meal }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    dialogTarget?.let { (day, mealType) ->
        val current = uiState.plan[day]?.get(mealType)
        var selectedRecipe by remember(day, mealType, current, uiState.recipes) {
            mutableStateOf(
                uiState.recipes.firstOrNull { it.id == current?.recipeId }
                    ?: uiState.recipes.firstOrNull()
            )
        }
        val timePickerState = rememberTimePickerState(
            initialHour = when (mealType) {
                "Desayuno" -> 8
                "Almuerzo" -> 13
                else -> 20
            },
            initialMinute = 0,
            is24Hour = true
        )
        var showClock by remember(day, mealType) { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { dialogTarget = null },
            shape = RoundedCornerShape(28.dp),
            title = {
                Column {
                    Text("$day · $mealType")
                    Text(
                        "Selecciona receta y hora del recordatorio",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column {
                    if (uiState.recipes.isEmpty()) {
                        Text("No tienes recetas todavía. Crea o importa alguna primero.")
                    } else {
                        Column(
                            modifier = Modifier
                                .heightIn(max = 270.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.recipes.forEach { recipe ->
                                RecipeChoiceRow(
                                    recipe = recipe,
                                    selected = selectedRecipe?.id == recipe.id,
                                    onClick = { selectedRecipe = recipe }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showClock = !showClock },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Alarm, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Recordar a las %02d:%02d".format(timePickerState.hour, timePickerState.minute))
                    }

                    AnimatedVisibility(visible = showClock) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TimePicker(state = timePickerState)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = selectedRecipe != null,
                    onClick = {
                        val recipe = selectedRecipe ?: return@Button
                        viewModel.assign(day, mealType, recipe)
                        CookPlanNotificationHelper.scheduleMealReminderAt(
                            context = context,
                            recipeId = recipe.id,
                            recipeName = recipe.name,
                            mealType = mealType,
                            triggerAtMillis = nextReminderMillis(day, timePickerState.hour, timePickerState.minute)
                        )
                        dialogTarget = null
                    }
                ) {
                    Text("Planificar")
                }
            },
            dismissButton = {
                Row {
                    if (current != null) {
                        TextButton(onClick = {
                            viewModel.remove(day, mealType)
                            dialogTarget = null
                        }) {
                            Text("Quitar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(onClick = { dialogTarget = null }) {
                        Text("Cerrar")
                    }
                }
            }
        )
    }
}

@Composable
private fun RecipeChoiceRow(
    recipe: RecipeEntity,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        label = "recipeChoiceBorder"
    )
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecipeImage(
                imageRef = recipe.imageUrl,
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(recipe.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(recipe.category ?: "Sin categoria", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (selected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun DayRow(
    dia: String,
    fecha: String,
    esHoy: Boolean,
    meals: Map<String, MealPlanEntity>,
    onAddMeal: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esHoy) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.62f)
            else MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = if (esHoy) CardDefaults.cardElevation(7.dp) else CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                .height(76.dp),
            shape = RoundedCornerShape(18.dp),
            color = if (isSet) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f) else MaterialTheme.colorScheme.background.copy(alpha = 0.55f),
            border = if (isSet) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(5.dp)) {
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

private fun nextReminderMillis(day: String, hour: Int, minute: Int): Long {
    val dayIndex = DIAS.indexOf(day).coerceAtLeast(0)
    val monday = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
    var dateTime = LocalDateTime.of(monday.plusDays(dayIndex.toLong()), java.time.LocalTime.of(hour, minute))
    if (dateTime.isBefore(LocalDateTime.now())) {
        dateTime = dateTime.plusWeeks(1)
    }
    return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
