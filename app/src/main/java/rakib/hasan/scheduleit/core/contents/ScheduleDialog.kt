package rakib.hasan.scheduleit.core.contents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import rakib.hasan.scheduleit.feature.model.AppInfo
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDialog(
    app: AppInfo,
    onSchedule: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedDateTime by remember {
        mutableStateOf(Calendar.getInstance())
    }

    var datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateTime.timeInMillis
    )

    var timePickerState = rememberTimePickerState(
        initialHour = selectedDateTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = selectedDateTime.get(Calendar.MINUTE)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Schedule ${app.name}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.fillMaxWidth(),
                    title = { Text("Select Date") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.fillMaxWidth()
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
                            // Update selectedDateTime with picked date and time
                            selectedDateTime.apply {
                                set(Calendar.YEAR, datePickerState.selectedDateMillis?.let {
                                    Calendar.getInstance().apply {
                                        timeInMillis = it
                                    }.get(Calendar.YEAR)
                                } ?: get(Calendar.YEAR))

                                set(Calendar.MONTH, datePickerState.selectedDateMillis?.let {
                                    Calendar.getInstance().apply {
                                        timeInMillis = it
                                    }.get(Calendar.MONTH)
                                } ?: get(Calendar.MONTH))

                                set(Calendar.DAY_OF_MONTH, datePickerState.selectedDateMillis?.let {
                                    Calendar.getInstance().apply {
                                        timeInMillis = it
                                    }.get(Calendar.DAY_OF_MONTH)
                                } ?: get(Calendar.DAY_OF_MONTH))

                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                            }

                            onSchedule(selectedDateTime.timeInMillis)
                        }
                    ) {
                        Text("Schedule")
                    }
                }
            }
        }
    }
}
