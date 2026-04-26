package com.example.rogaltasksapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController

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

    Scaffold(
        Modifier.fillMaxWidth(),
        containerColor=Color(0xFF101010),
        bottomBar={DolnePrzyciski(nav)},
    )
    { padding ->
        if (!uiState.isLoading)
        Column(
            modifier = Modifier.padding(padding).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text("Harmonogram", color = Color(0xfffafafa), fontSize = 22.sp)
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
                        focusedBorderColor = Color(0xFF80571F),
                        focusedTextColor = Color(0xfffafafa),
                        unfocusedTextColor = Color(0xffeaeaea),
                        focusedLabelColor = Color(0xFF80571F),
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
            if (selectedID==0)
            {
                var newName by remember {mutableStateOf("")}
                Spacer(Modifier.height(16.dp))
                Text("Nowy wpis", color = Color(0xfffafafa), fontSize = 22.sp)
                Spacer(Modifier.height(16.dp))
                Button(onClick = {})
                {
                    Text("Dodaj")
                }

                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = newName,
                    placeholder = {Text("Nazwa wpisu")},
                    onValueChange = {newName=it},
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF80571F),
                        focusedTextColor = Color(0xfffafafa),
                        unfocusedTextColor = Color(0xffeaeaea)
                    )
                )
            }
            else
            {
                Spacer(Modifier.height(16.dp))
                Text("Edytuj wpis", color = Color(0xfffafafa), fontSize = 22.sp)
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
                        focusedBorderColor = Color(0xFF80571F),
                        focusedTextColor = Color(0xfffafafa),
                        unfocusedTextColor = Color(0xffeaeaea),
                        focusedLabelColor = Color(0xFF80571F),
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
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = intervalSelect,
                label = {Text("Odstep")},
                onValueChange = {intervalSelect=it},
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF80571F),
                    focusedTextColor = Color(0xfffafafa),
                    unfocusedTextColor = Color(0xffeaeaea)
                ),
                isError = intervalSelect.isBlank() || intervalSelect.toIntOrNull() == null
            )
            if (selected==options[0])
            {

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
