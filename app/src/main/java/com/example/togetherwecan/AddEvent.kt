package com.example.togetherwecan

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AddEvent() {

    var name by remember { mutableStateOf("") }
    val options = listOf("Work", "Personal", "Family", "Other")
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[0]) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val scrollState = rememberScrollState()


    var selectedStartDate by remember { mutableStateOf("") }
    var selectedEndDate by remember { mutableStateOf("") }


    // Date picker dialog
    val endDatePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            selectedEndDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        year, month, day
    )


    val startDatePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            selectedStartDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"

            // After selecting the start date, set the minDate for the end date picker
            val startDateCalendar = Calendar.getInstance()
            startDateCalendar.set(selectedYear, selectedMonth, selectedDay)

            // Set the minimum date for the end date picker
            endDatePickerDialog.datePicker.minDate = startDateCalendar.timeInMillis
        },
        year, month, day
    )

    startDatePickerDialog.datePicker.minDate = System.currentTimeMillis() // Disable past dates


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color.White),
        //verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add New Event",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            )

        Spacer(modifier = Modifier.height(40.dp))


        Column(
            modifier = Modifier
                .width(300.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Title:",
                modifier = Modifier.align(Alignment.Start)
            )
            TextField(
                value = name,
                onValueChange = { name = "it" },  // Correct way to update state
                label = { Text("Enter Event Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedOption,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type of Event") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedOption = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = name,
                onValueChange = { name = "it" },  // Correct way to update state
                label = { Text("Enter Event Adress") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = name,
                onValueChange = { name = "it" },  // Correct way to update state
                label = { Text("Event description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                singleLine = false,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Start Date",
                    modifier = Modifier
                        .width(90.dp)
                )

                Button(
                    onClick = { startDatePickerDialog.show() },
                ) {
                    Text(text = if (selectedStartDate.isNotEmpty()) "$selectedStartDate" else "Pick Date")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "End Date",
                    modifier = Modifier
                        .width(90.dp)
                )

                Button(
                    onClick = { endDatePickerDialog.show() },
                ) {
                    Text(text = if (selectedEndDate.isNotEmpty()) "$selectedEndDate" else "Pick Date")
                }
            }

        }

    }
}