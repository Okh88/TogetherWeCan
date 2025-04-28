package com.example.togetherwecan

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import androidx.compose.ui.tooling.preview.Preview

data class Event(
    val eventTitle: String,
    val eventType: String,
    val eventAddress: String,
    val eventDescription: String,
    val eventStartDate: String,
    val eventEndDate: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AddEvent() {
    var title by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
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
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("") }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val scrollState = rememberScrollState()

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref = database.reference.child("/Events")
    val firebaseAuth = FirebaseAuth.getInstance()
    val currUser = firebaseAuth.currentUser
    var orgId: String? by remember { mutableStateOf(null) }

    val userRef = database.getReference("users").child("${currUser?.uid}")
    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            orgId = snapshot.child("organizationNumber").getValue(String::class.java)
        }

        override fun onCancelled(error: DatabaseError) {}
    })

    var selectedStartDate by remember { mutableStateOf("") }
    var selectedEndDate by remember { mutableStateOf("") }

    val endDatePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            selectedEndDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        }, year, month, day
    )

    val startDatePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            selectedStartDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            val startDateCalendar = Calendar.getInstance()
            startDateCalendar.set(selectedYear, selectedMonth, selectedDay)
            endDatePickerDialog.datePicker.minDate = startDateCalendar.timeInMillis
        }, year, month, day
    )
    startDatePickerDialog.datePicker.minDate = System.currentTimeMillis()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add New Event",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF446E84),
            modifier = Modifier.padding(10.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))


        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Event Title") },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4796B6),
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color(0xFF446E84)
            )
        )


        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Type of Event") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4796B6),
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color(0xFF446E84)
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedType = option
                            expanded = false
                        }
                    )
                }
            }
        }


        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Event Address") },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4796B6),
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color(0xFF446E84)
            )
        )


        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Event Description") },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(vertical = 6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4796B6),
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color(0xFF446E84)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))


        Button(
            onClick = { startDatePickerDialog.show() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4796B6)
            ),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Text(
                text = if (selectedStartDate.isNotEmpty()) selectedStartDate else "Pick Start Date",
                color = Color.White
            )
        }


        Button(
            onClick = { endDatePickerDialog.show() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4796B6)
            ),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Text(
                text = if (selectedEndDate.isNotEmpty()) selectedEndDate else "Pick End Date",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(30.dp))


        Button(
            onClick = {
                if (title.isBlank() || selectedType.isBlank() || address.isBlank() || description.isBlank() || selectedStartDate.isBlank() || selectedEndDate.isBlank()) {
                    Toast.makeText(context, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
                } else {
                    val event = Event(
                        eventTitle = title,
                        eventType = selectedType,
                        eventAddress = address,
                        eventDescription = description,
                        eventStartDate = selectedStartDate,
                        eventEndDate = selectedEndDate
                    )

                    if (orgId != null) {
                        val eventsRef = ref.child("$orgId").child(UUID.randomUUID().toString())
                        eventsRef.setValue(event)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Event added successfully!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to add event.", Toast.LENGTH_SHORT).show()
                            }
                    }


                    title = ""
                    selectedType = ""
                    address = ""
                    description = ""
                    selectedStartDate = ""
                    selectedEndDate = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(50.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF97D0E8), Color(0xFF4796B6), Color(0xFF446E84))
                    ),
                    shape = RoundedCornerShape(50)
                ),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Text("Save Event", color = Color.White, fontSize = 16.sp)
        }
    }
}
