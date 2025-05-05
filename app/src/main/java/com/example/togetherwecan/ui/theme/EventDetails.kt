package com.example.togetherwecan.ui.theme

import android.app.DatePickerDialog
import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

data class volunter(
    val name : String? = null,
    val email : String? = null,
    val organization : Boolean? = null,
    val organizationNumber : String? = null,
)

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
    val volunteerList = remember {mutableListOf<volunter>()}

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
    val volunterRef = database.getReference("JoinedVolunter")

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


    LaunchedEffect(Unit) {
        volunterRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    var volunterEventID = childSnapshot.child("eventId").getValue(String::class.java) ?: ""
                    var volunterUserId = childSnapshot.child("userId").getValue(String::class.java) ?: ""

                    if(volunterEventID == eventId) {
                        val userRef = database.getReference("users").child("$volunterUserId")
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var volunterName = snapshot.child("name").getValue(String::class.java) ?: ""
                                var volunterEmail = snapshot.child("email").getValue(String::class.java) ?: ""
                                var organization = snapshot.child("organization").getValue(Boolean::class.java)
                                var organizationNumber = snapshot.child("organizationNumber").getValue(String::class.java) ?: ""

                                var newVolunter = volunter(volunterName, volunterEmail, organization, organizationNumber)

                                volunteerList.add(newVolunter)


                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("MyLogs", "Failed to load user", error.toException())
                            }
                        })
                    }

                    Log.d("MyLogs", " Volunter $volunterEventID")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MyLogs", "Failed to load user", error.toException())
            }
        })
    }

    Log.d("MyLogs", "List $volunteerList")

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
            .padding(20.dp)
            //.verticalScroll(scrollState),
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
            OutlinedTextField(
                value = eventTitle,
                onValueChange = {
                    eventTitle = it
                    madeChange = true
                                },
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

            Spacer(modifier = Modifier.height(20.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = eventType,
                    onValueChange = {
                        madeChange = true
                    },
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
                                eventType = option
                                expanded = false
                                madeChange = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = eventAddress,
                onValueChange = {
                    eventAddress = it
                    madeChange = true
                                },
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

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = eventDescription,
                onValueChange = {
                    eventDescription = it
                    madeChange = true
                                },
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

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    singleEventsRef.updateChildren(
                        mapOf(
                            "eventTitle" to eventTitle,
                            "eventType" to eventType,
                            "eventDescription" to eventDescription,
                            "eventAddress" to eventAddress,
                            "eventStartDate" to selectedStartDate,
                            "eventEndDate" to selectedEndDate,
                        )
                    ).addOnSuccessListener {
                        Log.d("MyLogs", "Event updated successfully!")
                    }.addOnFailureListener { error ->
                        Log.d("MyLogs","Failed to update event: ${error.message}")
                    }
                },
                enabled = madeChange,
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
                Text("Save Changes")
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Joined Volunteers",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                volunteerList.forEach { volunter ->
                    VolunterCard(volunter)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

        }

        Spacer(modifier = Modifier.height(40.dp))


    }
}


@Composable
fun VolunterCard(
    volunter: volunter
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F6F6)),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = volunter.name ?: "Unknown Title",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = volunter.email ?: "Unknown email",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

