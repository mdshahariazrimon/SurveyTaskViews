package com.example.surveytaskviews.ui.submitted

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun SubmittedDataScreen(
    viewModel: SubmittedDataViewModel = hiltViewModel()
) {
    val forms by viewModel.submittedForms.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Submitted Survey Answers",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (forms.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No submitted answers yet.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(forms) { form ->
                    SubmittedFormCard(form.answersJson)
                }
            }
        }
    }
}

@Composable
fun SubmittedFormCard(answersJson: String) {
    val gson = Gson()
    val type = object : TypeToken<Map<String, String>>() {}.type
    val answersMap: Map<String, String> = gson.fromJson(answersJson, type)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            answersMap.forEach { (questionId, answer) ->
                Row {
                    Text(
                        text = "Q$questionId: ",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}