package com.example.wally.test

import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat

class TestWally {

    private val rotation = FloatPropKey()
    val dec = DecimalFormat("#,###")

    @Composable
    fun DoTest() {

        var rotationValue by remember { mutableStateOf(0.0f) }

        // How to STOP recomposition (Is that really a thing?)
        // ----------------------------------------------
        // In this case a @Composable function is nested inside a @Composable function.
        // When the callback from DoRotation changes the (stored) value of 'rotationValue',
        // that value is propagated into DoZotation ONLY and does not trigger a recomposition of
        // DoRotation.
        // I struggled with this as my code kept triggering a recursive recomposition of
        // DoRotation each time the rotation value changed (it changes ALOT!), which created a
        // steaming pile of shit in the UI.

        @Composable
        fun DoZotation() {
            Text("current rotation: ${dec.format(rotationValue)} degrees")
        }


        DoRotation() {
            rotationValue = it
        }


        DoZotation()
    }


    @Composable
    fun DoRotation(callback: (Float) -> Unit) {

        var duration by remember {
            mutableStateOf(1000)
        }

        var easingValues = remember { mutableStateListOf(0f, 0f, 0f, 0f) }

        RotatingSquareComponent(
            duration, easingValues,
            { d ->
                duration += 1000
            },
            { a ->
                easingValues[0] += a
            },
            { b ->
                easingValues[1] += b
            },
            { c ->
                easingValues[2] += c
            },
            { d ->
                easingValues[3] += d
            },
            { d ->
                callback(d)
            }
        )

    }

    private fun getCustomEasing(A: Float, B: Float, C: Float, D: Float): CubicBezierEasing {
        return CubicBezierEasing(A, B, C, D)
    }

    private fun getRotationTransitionDefinition(
        duration: Int,
        easingValues: MutableList<Float>
    ): TransitionDefinition<String> {

        return transitionDefinition {

            state("A") {
                this[rotation] = 0f
            }

            state("B") {
                this[rotation] = 360f
            }

            transition(fromState = "A", toState = "B") {

                rotation using infiniteRepeatable(
                    animation = tween(
                        durationMillis = duration,
                        easing = getCustomEasing(
                            easingValues[0],
                            easingValues[1],
                            easingValues[2],
                            easingValues[3]
                        )
                    )
                )

            }
        }
    }


    @Composable
    fun RotatingSquareComponent(
        duration: Int,
        easingValues: MutableList<Float>,
        callback: (Int) -> Unit,
        changeA: (Float) -> Unit,
        changeB: (Float) -> Unit,
        changeC: (Float) -> Unit,
        changeD: (Float) -> Unit,
        rotationValue: (Float) -> Unit
    ) {

        var duration by remember {
            mutableStateOf(duration)
        }

        var rpm by remember {
            mutableStateOf(60.0f)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            content = {



                val state = transition(
                    definition = getRotationTransitionDefinition(duration, easingValues),
                    initState = "A",
                    toState = "B"
                )

                Canvas(modifier = Modifier.preferredSize(200.dp)) {

                    rotate(state[rotation]) {
                        drawRect(color = Color(255, 138, 128))
                        drawRect(color = Color(100, 150, 3), size = Size(200f, 200f))
                        drawCircle(color = Color.Black, radius = 110f)

                        val paint = android.graphics.Paint()
                        paint.textSize = 56f
                        paint.color = 0xffff0000.toInt()

                        drawIntoCanvas {

                            it.nativeCanvas.drawText(
                                dec.format(state[rotation]),
                                center.x,
                                center.y,
                                paint
                            )
                            rotationValue(state[rotation])
                        }

                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {

                    Button(onClick = {
                        duration -= 1000
                        if (duration == 0) {
                            duration = 1000
                        }
                        rpm = (1000f / duration.toFloat()) * 60f
                        callback(duration)
                    }) {
                        Text("duration--")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(onClick = {
                        duration += 1000
                        rpm = (1000f / duration.toFloat()) * 60f
                        callback(duration)
                    }) {
                        Text("duration++")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text("rpm = $rpm")

                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {

                    Button(onClick = {
                        changeA(-0.1f)
                    }) {
                        Text("a--")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(onClick = {
                        changeA(0.1f)
                    }) {
                        Text("a++")
                    }

                    Text("${easingValues[0]}")

                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {

                    Button(onClick = {
                        changeB(-0.1f)
                    }) {
                        Text("b--")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(onClick = {
                        changeB(0.1f)
                    }) {
                        Text("b++")
                    }

                    Text("${easingValues[1]}")

                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {

                    Button(onClick = {
                        changeC(-0.1f)
                    }) {
                        Text("c--")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(onClick = {
                        changeC(0.1f)
                    }) {
                        Text("c++")
                    }

                    Text("${easingValues[2]}")

                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {

                    Button(onClick = {
                        changeD(-0.1f)
                    }) {
                        Text("d--")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(onClick = {
                        changeD(0.1f)
                    }) {
                        Text("d++")
                    }

                    Text("${easingValues[3]}")

                }

                Spacer(modifier = Modifier.height(16.dp))

                var arg by remember {
                    mutableStateOf("arg")
                }

                TwoLambda(arg, { r ->
                    arg = r
                }, { t ->
                    arg = t
                }, { v ->
                    arg = v
                })
            }
        )

    }


    /**
     * This steaming pile of shit is an example of how to handle MULTIPLE lambdas (callbacks) from
     * one function.
     */
    @Composable
    fun TwoLambda(
        arg: String,
        callback: (String) -> Unit,
        backcall: (String) -> Unit,
        foocall: (String) -> Unit
    ) {

        when (arg) {
            "ZERO" -> Row(modifier = Modifier.background(Color.Cyan)) {

                Text(arg)

                Spacer(modifier = Modifier.width(8.dp))

                Text("ZERO", modifier = Modifier.clickable {
                    callback("ZERO")
                })

                Spacer(modifier = Modifier.width(8.dp))

                Text("ONE", modifier = Modifier.clickable {
                    backcall("ONE")
                })

                Spacer(modifier = Modifier.width(8.dp))

                Text("TWO", modifier = Modifier.clickable {
                    foocall("TWO")
                })
            }

            "ONE" -> Row(modifier = Modifier.background(Color.Green)) {

                Text(arg)

                Spacer(modifier = Modifier.width(8.dp))

                Text("ZERO", modifier = Modifier.clickable {
                    callback("ZERO")
                })

                Spacer(modifier = Modifier.width(8.dp))

                Text("ONE", modifier = Modifier.clickable {
                    backcall("ONE")
                })

                Spacer(modifier = Modifier.width(8.dp))

                Text("TWO", modifier = Modifier.clickable {
                    foocall("TWO")
                })
            }

            "TWO" -> Row(modifier = Modifier.background(Color.LightGray)) {

                Text(arg)

                Spacer(modifier = Modifier.width(8.dp))

                Text("ZERO", modifier = Modifier.clickable {
                    callback("ZERO")
                })

                Spacer(modifier = Modifier.width(8.dp))

                Text("ONE", modifier = Modifier.clickable {
                    backcall("ONE")
                })

                Spacer(modifier = Modifier.width(8.dp))

                Text("TWO", modifier = Modifier.clickable {
                    foocall("TWO")
                })
            }
            else -> Row(modifier = Modifier.background(Color.White)) {

                Text(arg)

                Spacer(modifier = Modifier.width(8.dp))

                Text("ZERO", modifier = Modifier.clickable {
                    callback("ZERO")
                })

                Spacer(modifier = Modifier.width(8.dp))

                Text("ONE", modifier = Modifier.clickable {
                    backcall("ONE")
                })

                Spacer(modifier = Modifier.width(8.dp))

                Text("TWO", modifier = Modifier.clickable {
                    foocall("TWO")
                })
            }

        }


    }

} // end class