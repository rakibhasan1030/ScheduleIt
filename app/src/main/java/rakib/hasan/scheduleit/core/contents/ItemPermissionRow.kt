package rakib.hasan.scheduleit.core.contents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rakib.hasan.scheduleit.R
import rakib.hasan.scheduleit.ui.theme.Black
import rakib.hasan.scheduleit.ui.theme.PrimaryBlueDark
import rakib.hasan.scheduleit.ui.theme.SuccessGreen

@Composable
fun ItemPermissionRow(
    permissionName: String = "Permission Name",
    permissionDescription: String = "This app requires overlay permission to display system-alert windows. Please grant this permission to continue.",
    icon: ImageVector = Icons.Default.KeyboardArrowRight,
    isDescriptionVisible: Boolean = false,
    isPermissionGranted: Boolean = false,
    modifier: Modifier = Modifier,
    onPermissionNameClicked: () -> Unit,
    onIconClicked: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = permissionName,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.cabin_bold)),
                modifier = modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onPermissionNameClicked.invoke() }
                    .padding(4.dp)
            )
            Box(
                modifier = modifier
                    .padding(start = 8.dp)
                    .clip(CircleShape)
                    .clickable(enabled = !isPermissionGranted) { // Disable click if permission is granted
                        onIconClicked.invoke()
                    },
            ) {
                Icon(
                    imageVector = icon,
                    tint = if (icon == Icons.Default.CheckCircle) SuccessGreen else Black,
                    contentDescription = "Check",
                    modifier = modifier
                        .padding(8.dp)
                )
            }
        }

        if (isDescriptionVisible) {
            Text(
                text = permissionDescription,
                fontFamily = FontFamily(Font(R.font.cabin_regular)),
                fontSize = 14.sp,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemPermissionRowPreview(modifier: Modifier = Modifier) {
    ItemPermissionRow(
        permissionName = "Overlay Permission",
        permissionDescription = "This app requires overlay permission to display system-alert windows. Please grant this permission to continue.",
        icon = Icons.Default.KeyboardArrowRight,
        onPermissionNameClicked = {

        },
        onIconClicked = {

        }
    )
}














