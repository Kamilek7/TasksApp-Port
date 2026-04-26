package com.example.rogaltasksapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import java.util.Locale

data class DniTygodnia(
    val nazwa: String,
    var check: Boolean = false,
    val id: Int = nextId()
) {
    companion object {
        private var counter = 0

        private fun nextId(): Int {
            return counter++
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Harmonogram(nav: NavHostController, viewModel : TaskViewModel)
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var harmo = listOf(Harmonogram(0,"Dodaj nowy", "")) + uiState.wpisyHarmo
    var selectExpanded by remember {mutableStateOf(false)}
    var selectedName by remember {mutableStateOf(harmo[0].nazwa)}
    var selectedID by remember {mutableStateOf(0)}
    val options = listOf("Dni", "Tygodnie", "Miesiace", "Lata")
    var selectExpanded1 by remember {mutableStateOf(false)}
    var selected by remember { mutableStateOf(options[0]) }
    var intervalSelect by remember { mutableStateOf("1") }
    var showDialogTime by remember {mutableStateOf(false)}
    val dniTygodnia = remember {mutableStateListOf(DniTygodnia("Poniedziałek"),DniTygodnia("Wtorek"), DniTygodnia("Środa"), DniTygodnia("Czwartek"), DniTygodnia("Piątek"), DniTygodnia("Sobota"), DniTygodnia("Niedziela"))}

    val timePickerDayState = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0,
        is24Hour = true,
    )

    Scaffold(
        Modifier.fillMaxWidth(),
        bottomBar={DolnePrzyciski(nav)},
    )
    { padding ->
        if (!uiState.isLoading)
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            item {
                Text("Harmonogram", fontSize = 22.sp)
                ExposedDropdownMenuBox(
                    expanded = selectExpanded,
                    onExpandedChange = { selectExpanded = !selectExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedName?:"",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Wybierz wpis") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = selectExpanded)
                        },
                        modifier = Modifier.menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = Color(0xffeaeaea),
                        ),
                    )

                    ExposedDropdownMenu(
                        expanded = selectExpanded,
                        onDismissRequest = { selectExpanded = false }
                    ) {
                        harmo.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.nazwa?: "") },
                                onClick = {
                                    selectedName = item.nazwa?: ""
                                    selectedID = item.ID?: 0
                                    selectExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            item {
                if (selectedID==0)
                {
                    var newName by remember {mutableStateOf("")}
                    Spacer(Modifier.height(16.dp))
                    Text("Nowy wpis", fontSize = 22.sp)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {})
                    {
                        Text("Dodaj")
                    }

                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newName,
                        label = {Text("Nazwa wpisu")},
                        placeholder = {Text("Ćwiczenia")},
                        onValueChange = {newName=it},
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = Color(0xffeaeaea)
                        )
                    )
                }
                else
                {
                    Spacer(Modifier.height(16.dp))
                    Text("Edytuj wpis", fontSize = 22.sp)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {})
                    {
                        Text("Edytuj")
                    }
                }
                Spacer(Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    expanded = selectExpanded1,
                    onExpandedChange = { selectExpanded1 = !selectExpanded1 }
                ) {
                    OutlinedTextField(
                        value = selected,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Odstep czasu") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = selectExpanded1)
                        },
                        modifier = Modifier.menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = Color(0xffeaeaea),
                        ),
                    )

                    ExposedDropdownMenu(
                        expanded = selectExpanded1,
                        onDismissRequest = { selectExpanded1 = false }
                    ) {
                        options.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    selected = item
                                    selectExpanded1 = false
                                }
                            )
                        }
                    }

                }
            }
            item {
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = intervalSelect,
                    label = {Text("Odstep")},
                    onValueChange = {intervalSelect=it},
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = Color(0xffeaeaea)
                    ),
                    isError = intervalSelect.isBlank() || intervalSelect.toIntOrNull() == null
                )
                Spacer(Modifier.height(16.dp))
                AnimatedVisibility(
                    visible = selected==options[0],
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                )
                {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally)
                    {
                        Box() {
                            OutlinedTextField(
                                label = {Text("Godzina")},
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedTextColor = Color(0xffeaeaea)
                                ),
                                value = String.format(Locale.getDefault(), "%02d:%02d", timePickerDayState.hour,timePickerDayState.minute),
                                onValueChange = { },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showDialogTime = !showDialogTime }) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Select date"
                                        )
                                    }
                                },
                            )

                            if (showDialogTime) {
                                Popup(
                                    onDismissRequest = { showDialogTime = false },
                                    alignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xfd303030), shape= RoundedCornerShape(16.dp))
                                            .padding(16.dp)
                                    ) {

                                        TimePicker(
                                            state = timePickerDayState,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {

                AnimatedVisibility(
                    visible = selected==options[1],
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                )
                {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally)
                    {
                        Text("Dni tygodnia", fontSize = 22.sp)
                        dniTygodnia.forEach{item ->  Row(modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(vertical = 2.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween)
                            {
                                Text(item.nazwa)
                                Checkbox(
                                    checked = item.check,
                                    onCheckedChange = { dniTygodnia[item.id] = dniTygodnia[item.id].copy(check = it) }
                                )
                            }
                            }
                        Box() {
                            OutlinedTextField(
                                label = {Text("Godzina")},
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedTextColor = Color(0xffeaeaea)
                                ),
                                value = String.format(Locale.getDefault(), "%02d:%02d", timePickerDayState.hour,timePickerDayState.minute),
                                onValueChange = { },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showDialogTime = !showDialogTime }) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Select date"
                                        )
                                    }
                                },
                            )

                            if (showDialogTime) {
                                Popup(
                                    onDismissRequest = { showDialogTime = false },
                                    alignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xfd303030), shape= RoundedCornerShape(16.dp))
                                            .padding(16.dp)
                                    ) {

                                        TimePicker(
                                            state = timePickerDayState,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else
        {
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier.padding(padding).fillMaxSize()
            )
            {
                CircularProgressIndicator()
            }
        }

    }
}
