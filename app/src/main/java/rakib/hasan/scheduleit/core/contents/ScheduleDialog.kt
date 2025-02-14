package rakib.hasan.scheduleit.core.contents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import rakib.hasan.scheduleit.feature.app_list.model.AppInfo
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDialog(
    app: AppInfo?,
    onSchedule: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(true) } // State to control which picker is shown
    var selectedDateTime by remember { mutableStateOf(Calendar.getInstance()) }

    // Date Picker State
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateTime.timeInMillis
    )

    // Time Picker State
    val timePickerState = rememberTimePickerState(
        initialHour = selectedDateTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = selectedDateTime.get(Calendar.MINUTE)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() // Wrap content height
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Scroll only if content is too large
            ) {
                Text(
                    text = "Schedule ${app?.name}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (showDatePicker) {
                    // Show Date Picker first
                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier.fillMaxWidth(),
                        title = { Text("Select Date") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                // Update selectedDateTime with the picked date
                                datePickerState.selectedDateMillis?.let { millis ->
                                    selectedDateTime.timeInMillis = millis
                                }
                                showDatePicker = false // Move to Time Picker
                            }
                        ) {
                            Text("Next")
                        }
                    }
                } else {
                    // Show Time Picker after date selection
                    TimePicker(
                        state = timePickerState,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { showDatePicker = true }) {
                            Text("Back")
                        }
                        Button(
                            onClick = {
                                // Update selectedDateTime with the picked time
                                selectedDateTime.apply {
                                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                    set(Calendar.MINUTE, timePickerState.minute)
                                }
                                onSchedule(selectedDateTime.timeInMillis) // Trigger schedule callback
                                onDismiss() // Close the dialog
                            }
                        ) {
                            Text("Schedule")
                        }
                    }
                }
            }
        }
    }
}