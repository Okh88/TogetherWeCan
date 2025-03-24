package com.example.togetherwecan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.togetherwecan.ui.theme.TogetherWeCanTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TogetherWeCanTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "TogetherWeCan",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val database = Firebase.database
    val myRef = database.getReference("test_connection")


        Text(
            text = "Hello $name!",
            modifier = modifier
        )

    Button(onClick = {
        myRef.setValue("test test!")
    }) {
        Text("SAVE")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TogetherWeCanTheme {
        Greeting("TogetherWeCan")
    }
}
