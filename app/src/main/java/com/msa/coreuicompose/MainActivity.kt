package com.msa.coreuicompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.msa.corebase.models.base.UseCaseState
import com.msa.coreui.loading.LoadingThreePoints
import com.msa.coreuicompose.ui.theme.CoreUiComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoreUiComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        //LoadingThreePoints()
                        val currentSample = rememberSaveable { mutableStateOf<Sample?>(null) }
                        val onReset = { currentSample.value = null }
                        val onResetSheet: UseCaseState.() -> Unit = { currentSample.value = null }
                        CalendarSample1(closeSelection = onResetSheet)
                    }

                }
            }
        }
    }
}


@Composable
fun show(closeSelection: UseCaseState.() -> Unit) {

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CoreUiComposeTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            com.msa.coreui.loading.LoadingThreePoints()
        }
    }
}