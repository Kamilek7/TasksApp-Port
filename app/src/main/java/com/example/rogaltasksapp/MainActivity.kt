package com.example.rogaltasksapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rogaltasksapp.ui.theme.RogalTasksAppTheme
import androidx.compose.ui.Alignment


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RogalTasksAppTheme {
                val repo = ZadaniaRepository(RetroFitInstance.api)
                val factory = TaskViewModelFactory(repo)
                MainNav(viewModel(factory=factory))
            }
        }
    }
}


@Composable
fun Ustawienia(nav: NavHostController, viewModel : TaskViewModel)
{
    Scaffold(
        Modifier.fillMaxWidth(),
        containerColor=Color(0xFF101010),
        bottomBar={DolnePrzyciski(nav)},
    )
    { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text("Ustawienia", color = Color(0xfffafafa), fontSize = 32.sp)
            Spacer(Modifier.height(24.dp))
            Button(onClick={
                viewModel.logout()

            },colors= ButtonDefaults.buttonColors(containerColor = Color(0xFFA67126)))
            {Text("Wyloguj się")}
        }
    }
}