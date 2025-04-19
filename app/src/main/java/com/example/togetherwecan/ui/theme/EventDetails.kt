package com.example.togetherwecan.ui.theme

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.togetherwecan.Events
import com.example.togetherwecan.TopAppBar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetails(navController: NavController, navBackStackEntry: NavBackStackEntry) {
    val orgId = navBackStackEntry.arguments?.getString("orgId") ?: ""
    val eventId = navBackStackEntry.arguments?.getString("eventId") ?: ""

    var eventTitle by remember { mutableStateOf("") }
    var eventAddress by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var eventType by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }
    var selectedStartDate by remember { mutableStateOf("") }
    var selectedEndDate by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var madeChange by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val scrollState = rememberScrollState()

    val options = listOf(
        "volunteerDrives",
        "charityFundraisers",
        "meetups",
        "gameNights",
        "culturalCelebrations",
        "workshopsTutorials",
        "webinars",
        "studyGroups",
        "groupWorkouts",
        "mentalHealthSupport",
        "wellnessRetreats",
        "artExhibitions",
        "openMicNights",
        "photographyWalks",
        "jobFairs",
        "networkingEvents",
        "mentorshipSessions",
        "ecoFriendlyActivities",
        "zeroWasteChallenges",
        "recyclingDrives",
        "clothingOrFoodDrives",
        "bloodDonationCamps",
        "crowdfundingEvents",
        "hikingOrNatureWalks",
        "campingTrips",
        "sportsTournaments",
        "birthdayParties",
        "anniversaryCelebrations",
        "holidayGatherings",
        "griefSupportGroups",
        "parentingOrFamilySupportGroups",
        "recoveryMeetings",
        "protestsDemonstrations",
        "petitionsAndAdvocacyCampaigns"
    )
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val singleEventsRef = database.getReference("Events").child("$orgId").child("$eventId")


    LaunchedEffect(Unit) {
        singleEventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventTitle = snapshot.child("eventTitle").getValue(String::class.java) ?: ""
                eventType = snapshot.child("eventType").getValue(String::class.java) ?: ""
                eventDescription = snapshot.child("eventDescription").getValue(String::class.java) ?: ""
                eventAddress = snapshot.child("eventAddress").getValue(String::class.java) ?: ""
                selectedStartDate = snapshot.child("eventStartDate").getValue(String::class.java) ?: ""
                selectedEndDate = snapshot.child("eventEndDate").getValue(String::class.java) ?: ""

                Log.d("MyLogs", "$eventTitle  eventTitle")
                Log.d("MyLogs", "$eventDescription eventDescription")
                Log.d("MyLogs", "$snapshot")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MyEvents", "Failed to load user", error.toException())
            }
        })
    }

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

    Column(
        modifier = Modifier
            .padding(20.dp),
    ) {
        CenterAlignedTopAppBar(
            title = { Text("Event Description") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            "Event $eventTitle",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            //modifier = Modifier.padding(20.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier.verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Title",
                modifier = Modifier
                    .align(Alignment.Start)
                )

            TextField(
                value = eventTitle,
                onValueChange = { newText ->
                    eventTitle = newText
                    madeChange = true
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = eventType,
                    onValueChange = {
                        madeChange = true
                    },
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
                                eventType = option
                                expanded = false
                                madeChange = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            TextField(
                value = eventAddress,
                onValueChange = { newText ->
                    eventAddress = newText
                    madeChange = true
                },
                label = { Text("Enter Event Adress") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = eventDescription,
                onValueChange = { newText ->
                    eventDescription = newText
                    madeChange = true
                                },
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

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {},
                enabled = madeChange
            ) {
                Text("Save Changes")
            }

        }
    }
}

