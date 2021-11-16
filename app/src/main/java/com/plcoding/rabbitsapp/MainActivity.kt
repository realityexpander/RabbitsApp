package com.plcoding.rabbitsapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.plcoding.rabbitsapp.ui.theme.RabbitsAppTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalCoilApi
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationGraphicsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RabbitsAppTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    val viewModel: MainViewModel = hiltViewModel()
                    val rabbit = viewModel.state.value.rabbit
                    val isLoading = viewModel.state.value.isLoading

                    var atEnd by remember { mutableStateOf(false) }
//                    val image = animatedVectorResource(id = R.drawable.animatorvectordrawable)
                    val image = animatedVectorResource(id = R.drawable.circular_progress_bar)
                    val scope = rememberCoroutineScope()
                    var isRunning by remember { mutableStateOf(true) }

                    suspend fun runAnimation() {
                        while (isRunning) {
                            delay(2000) // set here your delay between animations
                            atEnd = !atEnd
                        }
                    }

                    DisposableEffect(Unit) {
                        Log.d("****MAIN", "DisposableEffect: atEnd-$atEnd")
//                        atEnd = !atEnd
                        onDispose {
                            atEnd = false
                            Log.d("****MAIN", "DisposableEffect onDispose: atEnd-$atEnd")
                        }
                    }
                    LaunchedEffect(image) {
//                        runAnimation()
                        Log.d("****MAIN", "LaunchedEffect: atEnd-$atEnd")
                    }


                    rabbit?.let {
                        Image(
                            painter = rememberImagePainter(
                                data = rabbit.imageUrl,
                                builder = {
                                    crossfade(true)
                                    // placeholder is only shown after network call and during conversion to display
                                    placeholder(R.drawable.circular_shape)
                                    error(R.drawable.ic_launcher_background) // error is shown is imageUrl fails
                                    size(1000, 500)
                                },
                            ),
                            contentDescription = "Rabbit",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Image(
                            painter = image.painterFor( atEnd ),
                            contentDescription = "Your content description",
                            modifier = Modifier
                                .size(64.dp)
                                .clickable {
                                    isRunning = !isRunning // start/stop animation
                                    if (isRunning) // run the animation if isRunning is true.
                                        scope.launch {
                                            runAnimation()
                                        }
                                }
                                .fillMaxSize()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = rabbit.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = rabbit.description)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Button(
                        onClick = {
                            (viewModel::getRandomRabbit)()
                            Log.d("****MAIN", "onClick: atEnd-$atEnd")
                            atEnd = !atEnd
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = "Next rabbit!")
                    }
                    Spacer(Modifier.height(8.dp))
                    if (isLoading) {
                        CircularProgressIndicator() // only shown while network call is happening
                    }
                }
            }
        }
    }
}


fun main() {

    println("Hello World")

}