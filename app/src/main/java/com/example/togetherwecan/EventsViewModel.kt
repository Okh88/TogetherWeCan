package com.example.togetherwecan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EventsViewModel : ViewModel() {
    private val db = Firebase.database.reference

    private val _events = mutableStateListOf<VolunteerEvent>()
    val events: List<VolunteerEvent> = _events

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        db.child("Events").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _events.clear()

                for (userSnapshot in snapshot.children) {
                    for (eventSnapshot in userSnapshot.children) {
                        val id = eventSnapshot.key ?: ""
                        val eventTitle = eventSnapshot.child("eventTitle").getValue(String::class.java) ?: ""
                        val eventAddress = eventSnapshot.child("eventAddress").getValue(String::class.java) ?: ""
                        val eventStartDate = eventSnapshot.child("eventStartDate").getValue(String::class.java) ?: ""
                        val eventEndDate = eventSnapshot.child("eventEndDate").getValue(String::class.java) ?: ""
                        val eventType = eventSnapshot.child("eventType").getValue(String::class.java) ?: ""
                        val eventDescription = eventSnapshot.child("eventDescription").getValue(String::class.java) ?: ""

                        val event = VolunteerEvent(
                            id = id,
                            eventTitle = eventTitle,
                            eventAddress = eventAddress,
                            eventStartDate = eventStartDate,
                            eventEndDate = eventEndDate,
                            eventType = eventType,
                            eventDescription = eventDescription
                        )
                        _events.add(event)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }
}

@Preview (showBackground = true)
@Composable
fun EventsScreenPreview() {
    val viewModel = EventsViewModel()
    EventsScreen(viewModel)
}

@Composable
fun EventsScreen(viewModel: EventsViewModel) {
    TODO("Not yet implemented")
}
