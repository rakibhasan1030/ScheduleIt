package rakib.hasan.scheduleit.core.contents

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import rakib.hasan.scheduleit.R
import rakib.hasan.scheduleit.core.utils.Const.loadAppIcon
import rakib.hasan.scheduleit.feature.schedule.domain.model.ScheduledApp
import rakib.hasan.scheduleit.feature.schedule.presentation.view.formatTime
import rakib.hasan.scheduleit.ui.theme.Gray900

@Composable
fun ItemScheduledApp(
    context: Context,
    scheduledApp: ScheduledApp,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onScheduleClicked: () -> Unit,
) {
    val status =
        if (scheduledApp.lastExecutionTime != null && scheduledApp.lastExecutionTime >= scheduledApp.scheduledTime!!) {
            "Completed"
        } else {
            "Pending"
        }

    // State to manage the visibility of the dropdown menu
    var showMenu = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val iconBitmap = loadAppIcon(scheduledApp.packageName, context)
                    if (iconBitmap != null) {
                        Image(
                            bitmap = iconBitmap.toBitmap().asImageBitmap(),
                            contentDescription = scheduledApp.name,
                            modifier = Modifier
                                .size(64.dp)
                                .padding(end = 12.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "App Icon",
                            modifier = Modifier
                                .size(64.dp)
                                .padding(end = 8.dp)
                        )
                    }
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = scheduledApp.name,
                            fontFamily = FontFamily(Font(R.font.cabin_bold)),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = scheduledApp.packageName,
                            fontFamily = FontFamily(Font(R.font.cabin_italic)),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                InfoRow(
                    key = "Status",
                    value = status
                )
                InfoRow(
                    key = "Repeat",
                    value = getRepeatText(scheduledApp.repeatInterval, scheduledApp.repeatValue)
                )
                InfoRow(
                    key = "Scheduled at",
                    value = formatTime(scheduledApp.scheduledTime)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier,
                ) {
                    IconButton(
                        onClick = { showMenu.value = true }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More Options"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu.value,
                        onDismissRequest = { showMenu.value = false }
                    ) {
                        DropdownMenuItem(
                            modifier = Modifier
                                .padding(start = 4.dp, end = 4.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            onClick = {
                                showMenu.value = false
                                onEditClicked.invoke()
                            },
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        tint = Gray900,
                                        contentDescription = "Edit"
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "Edit",
                                        modifier = Modifier
                                    )
                                }
                            }
                        )
                        DropdownMenuItem(
                            modifier = Modifier
                                .padding(start = 4.dp, end = 4.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            onClick = {
                                showMenu.value = false
                                onDeleteClicked.invoke()
                            },
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        tint = Gray900,
                                        contentDescription = "Delete"
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "Delete",
                                        modifier = Modifier
                                    )
                                }
                            }
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp, bottom = 8.dp, end = 8.dp)
                        .clip(CircleShape)
                        .clickable { onScheduleClicked.invoke() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_alarm_enable),
                        contentDescription = "Alarm",
                        modifier = Modifier
                            .padding(8.dp),
                    )
                }
            }
        }
    }
}

fun getRepeatText(repeatInterval: Int, repeatValue: Int): String {
    return when (repeatInterval) {
        1 -> "Every $repeatValue Minute"
        2 -> "Every $repeatValue Hour"
        3 -> "Every $repeatValue Day"
        4 -> "Every $repeatValue Month"
        else -> "No Repeat"
    }
}