package com.example.surveytaskviews.ui.form

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.surveytaskviews.data.model.Question
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    viewModel: FormViewModel = hiltViewModel(),
    onNavigateToSubmittedData: () -> Unit
) {
    val uiState by viewModel.formState.collectAsState()

    // NEW: Log the current state every time the UI recomposes
    Log.d("FormScreen", "Recomposing with state: ${uiState::class.simpleName}")

    LaunchedEffect(uiState) {
        if (uiState is FormUiState.Submitted) {
            delay(2000)
            viewModel.startNewForm()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Survey") },
                actions = {
                    TextButton(onClick = onNavigateToSubmittedData) {
                        Text("View Submissions")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is FormUiState.Loading -> CircularProgressIndicator()
                is FormUiState.Submitted -> {
                    Text("Form submitted successfully! Restarting...")
                }
                is FormUiState.Success -> {
                    QuestionView(
                        question = state.currentQuestion,
                        onNextClicked = { answer ->
                            viewModel.onNextClicked(state.currentQuestion.id, answer)
                        },
                        onSkipClicked = { viewModel.onSkipClicked() }
                    )
                }
                is FormUiState.Error -> Text(text = "Error: ${state.message}")
            }
        }
    }
}


@Composable
fun QuestionView(
    question: Question,
    onNextClicked: (String) -> Unit,
    onSkipClicked: () -> Unit
) {
    var currentAnswer by remember(question.id) { mutableStateOf<Any>("") }
    var isInputValid by remember(question.id) { mutableStateOf(true) }

    fun validate(input: String): Boolean {
        return if (question.regex.isNullOrEmpty()) {
            true
        } else {
            Regex(question.regex).matches(input)
        }
    }

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
                    val text = currentAnswer as? String ?: ""
                    isInputValid = validate(text)

                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            currentAnswer = it
                            isInputValid = validate(it)
                        },
                        label = { Text("Your answer") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = !isInputValid && text.isNotEmpty(),
                        supportingText = {
                            if (!isInputValid && text.isNotEmpty()) {
                                Text("Invalid input.")
                            }
                        }
                    )
                }
                "radio" -> {
                    isInputValid = true
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
                    isInputValid = true
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
                else -> Text("Unsupported type: ${question.type}")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (question.skip.id != "-1") {
                TextButton(onClick = onSkipClicked) {
                    Text("Skip")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            val (finalAnswer, isAnswered) = when (val ans = currentAnswer) {
                is String -> ans to ans.isNotBlank()
                is Set<*> -> (ans as Set<String>).joinToString(", ") to ans.isNotEmpty()
                else -> "" to false
            }

            val isSubmitButton = question.referTo?.id == "submit"

            Button(
                onClick = { onNextClicked(finalAnswer) },
                enabled = isAnswered && isInputValid
            ) {
                Text(if (isSubmitButton) "Submit" else "Next")
            }
        }
    }
}