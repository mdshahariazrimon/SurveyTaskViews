package com.example.surveytaskviews.ui.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.surveytaskviews.data.model.Question

@Composable
fun FormScreen(viewModel: FormViewModel = hiltViewModel()) {
    val uiState by viewModel.formState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is FormUiState.Loading -> CircularProgressIndicator()
            is FormUiState.Success -> {
                QuestionView(
                    question = state.currentQuestion,
                    onNextClicked = { answer -> viewModel.onNextClicked(answer) }
                )
            }
            is FormUiState.Error -> Text(text = "Error: ${state.message}")
        }
    }
}

@Composable
fun QuestionView(
    question: Question,
    onNextClicked: (String) -> Unit
) {
    var currentAnswer by remember(question.id) { mutableStateOf<Any>("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = question.label ?: "No Label Found",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            when (question.type) {
                "textInput", "numberInput" -> {
                    OutlinedTextField(
                        value = currentAnswer as? String ?: "",
                        onValueChange = { currentAnswer = it },
                        label = { Text("Your answer") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                "radio" -> {
                    if (currentAnswer !is String) currentAnswer = ""
                    val answer = currentAnswer as String
                    Column {
                        question.options?.forEach { option ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (answer == option.value),
                                        onClick = { currentAnswer = option.value }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (answer == option.value),
                                    onClick = { currentAnswer = option.value }
                                )
                                Text(
                                    text = option.value ?: "",
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
                "multipleChoice" -> {
                    // MODIFIED: This block is updated to handle mutable sets correctly.
                    if (currentAnswer !is Set<*>) currentAnswer = mutableSetOf<String>()
                    val selectedAnswers = (currentAnswer as Set<*>).filterIsInstance<String>().toMutableSet()

                    Column {
                        question.options?.forEach { option ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (option.value in selectedAnswers),
                                        onClick = {
                                            if (option.value in selectedAnswers) {
                                                selectedAnswers.remove(option.value)
                                            } else {
                                                selectedAnswers.add(option.value)
                                            }
                                            currentAnswer = selectedAnswers.toSet()
                                        }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = (option.value in selectedAnswers),
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            selectedAnswers.add(option.value)
                                        } else {
                                            selectedAnswers.remove(option.value)
                                        }
                                        currentAnswer = selectedAnswers.toSet()
                                    }
                                )
                                Text(
                                    text = option.value ?: "",
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
                "checkbox" -> Text(text = "Checkbox type UI to be implemented.")
                else -> Text(text = "Unsupported question type: ${question.type}")
            }
        }

        val (finalAnswer, isEnabled) = when (val ans = currentAnswer) {
            is String -> ans to ans.isNotBlank()
            is Set<*> -> (ans as Set<String>).joinToString(", ") to ans.isNotEmpty()
            else -> "" to false
        }

        Button(
            onClick = { onNextClicked(finalAnswer) },
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Next")
        }
    }
}