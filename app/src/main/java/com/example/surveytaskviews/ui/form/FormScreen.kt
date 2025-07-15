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
    var currentAnswer by remember(question.id) { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                // MODIFIED: Added ?: "" to prevent crash if label is null
                text = question.label ?: "No Label Found",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            when (question.type) {
                "textInput", "numberInput" -> {
                    OutlinedTextField(
                        value = currentAnswer,
                        onValueChange = { currentAnswer = it },
                        label = { Text("Your answer") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                "radio" -> {
                    Column {
                        question.options?.forEach { option ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (currentAnswer == option.value),
                                        onClick = { currentAnswer = option.value }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (currentAnswer == option.value),
                                    onClick = { currentAnswer = option.value }
                                )
                                Text(
                                    // MODIFIED: Added ?: "" to prevent crash if option value is null
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

        Button(
            onClick = { onNextClicked(currentAnswer) },
            enabled = currentAnswer.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Next")
        }
    }
}