package rakib.hasan.scheduleit.core.contents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rakib.hasan.scheduleit.feature.app_list.model.AppInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// this is from my side and this is from his side... is this okay?
// I don't know if it is okay or mjn
@Composable
fun AppListItem(
    app: AppInfo,
    onScheduleClick: (AppInfo) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = app.getIconAsBitmap(),
                contentDescription = app.name,
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 8.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall
                )
                app.scheduledTime?.let { scheduledTime ->
                    Text(
                        text = "Scheduled: ${formatDateTime(scheduledTime)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Button(
                onClick = { onScheduleClick(app) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Schedule")
            }
        }
    }
}

private fun formatDateTime(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}