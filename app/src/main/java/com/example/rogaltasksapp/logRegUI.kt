package com.example.rogaltasksapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController

fun checkVals(login:String,  haslo:String) : String
{
    if (login=="" && haslo=="")
        return "Wpisz login i hasło"
    else if (login=="")
        return "Wpisz login"
    else if (haslo=="")
        return "Wpisz hasło"
    else
        return ""
}


@Composable
fun Login(nav: NavHostController, viewModel : TaskViewModel)
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showPassword by remember { mutableStateOf(false) }
    var login by remember {mutableStateOf("")}
    var pass by remember {mutableStateOf("")}
    var check by remember {mutableStateOf("Wpisz login i hasło!")}
    Scaffold(
        Modifier.fillMaxWidth(),
        containerColor=Color(0xFF101010)
    )
    {
            padding->
        Column(modifier = Modifier.padding(padding).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally)
        {
            Text("Zaloguj się", color = Color(0xfffafafa), fontSize = 22.sp)
            Spacer(Modifier.height(24.dp))
            Text("Login", color = Color(0xfffafafa), fontSize = 22.sp)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = login,
                placeholder = {Text("Login")},
                onValueChange = {login=it; check=checkVals(login,pass)},
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF80571F),
                    focusedTextColor = Color(0xfffafafa),
                    unfocusedTextColor = Color(0xffeaeaea)
                )
            )
            Spacer(Modifier.height(24.dp))
            Text("Hasło", color = Color(0xfffafafa), fontSize = 22.sp)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = pass,
                onValueChange = {pass=it; check=checkVals(login,pass)},
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF80571F),
                    focusedTextColor = Color(0xfffafafa),
                    unfocusedTextColor = Color(0xffeaeaea)
                ),
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
            )
            Spacer(Modifier.height(16.dp))
            Text(text=if (check!="") check else uiState.info ?: "", color=Color.Red)
            Spacer(Modifier.height(24.dp))
            Button(onClick={
                nav.navigate(Screen.Rejestrowanie.route)
            },colors= ButtonDefaults.buttonColors(containerColor = Color(0xFF80571F)))
            {Text("Zarejestuj się")}
            Button(onClick={
                viewModel.login(login, pass)

            },colors= ButtonDefaults.buttonColors(containerColor = Color(0xFFA67126)))
            {Text("Zaloguj się")}
        }
    }

}

@Composable
fun Rejestruj(nav: NavHostController, viewModel : TaskViewModel)
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showPassword by remember { mutableStateOf(false) }
    var login by remember {mutableStateOf("")}
    var pass by remember {mutableStateOf("")}
    var check by remember {mutableStateOf("Wpisz login i hasło!")}
    Scaffold(
        Modifier.fillMaxWidth(),
        containerColor=Color(0xFF101010)
    )
    {
            padding->
        Column(modifier = Modifier.padding(padding).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally)
        {
            Text("Załóż konto", color = Color(0xfffafafa), fontSize = 22.sp)
            Spacer(Modifier.height(24.dp))
            Text("Login", color = Color(0xfffafafa), fontSize = 22.sp)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = login,
                placeholder = {Text("Login")},
                onValueChange = {login=it; check=checkVals(login,pass)},
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF80571F),
                    focusedTextColor = Color(0xfffafafa),
                    unfocusedTextColor = Color(0xffeaeaea)
                )
            )
            Spacer(Modifier.height(24.dp))
            Text("Hasło", color = Color(0xfffafafa), fontSize = 22.sp)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = pass,
                onValueChange = {pass=it; check=checkVals(login,pass)},
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF80571F),
                    focusedTextColor = Color(0xfffafafa),
                    unfocusedTextColor = Color(0xffeaeaea)
                ),
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
            )
            Spacer(Modifier.height(16.dp))
            Text(text=if (check!="") check else uiState.info ?: "", color=Color.Red)
            Spacer(Modifier.height(24.dp))
            Button(onClick={
                nav.navigate(Screen.Logowanie.route)
            },colors= ButtonDefaults.buttonColors(containerColor = Color(0xFF80571F)))
            {Text("Zaloguj się")}
            Button(onClick={
                viewModel.register(login, pass)

            },colors= ButtonDefaults.buttonColors(containerColor = Color(0xFFA67126)))
            {Text("Zarejestruj się")}
        }
    }

}
