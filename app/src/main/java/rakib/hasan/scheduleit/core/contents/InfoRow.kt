package rakib.hasan.scheduleit.core.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import rakib.hasan.scheduleit.R
import rakib.hasan.scheduleit.ui.theme.Black
import rakib.hasan.scheduleit.ui.theme.Gray900

@Composable
fun InfoRow(
    key: String,
    value: String,
    isTitle: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Companion.Top,
        horizontalArrangement = if (isTitle) Arrangement.Center else Arrangement.Start
    ) {
        Text(
            text = key,
            fontFamily = FontFamily(Font(R.font.cabin_bold)),
            style = MaterialTheme.typography.bodySmall,
            color = Black,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = " : ",
            fontFamily = FontFamily(Font(R.font.cabin_regular)),
            style = MaterialTheme.typography.bodySmall,
            color = Black,
            modifier = Modifier
        )
        Text(
            text = value,
            fontFamily = FontFamily(Font(R.font.cabin_regular)),
            style = MaterialTheme.typography.bodySmall,
            color = Gray900,
            modifier = Modifier
                .weight(0.6f)
        )
    }
}