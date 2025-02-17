package rakib.hasan.scheduleit.core.contents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThoughtInputField(
    thought: String, // Current text value
    onThoughtChange: (String) -> Unit, // Callback for text changes
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = thought,
        onValueChange = onThoughtChange,
        modifier = modifier
            .fillMaxWidth(),
        label = {
            Text(
                text = "Enter your thoughts",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Label color
            )
        },
        placeholder = {
            Text(
                text = "E.g., This schedule is important for my meeting.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) // Placeholder color
            )
        },
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface, // Text color
            fontSize = 16.sp
        ),
        singleLine = false, // Allow multiple lines
        maxLines = 4, // Maximum lines before scrolling
        shape = RoundedCornerShape(12.dp), // Rounded corners
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary, // Border color when focused
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline, // Border color when unfocused
            cursorColor = MaterialTheme.colorScheme.primary, // Cursor color
            focusedLabelColor = MaterialTheme.colorScheme.primary, // Label color when focused
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Label color when unfocused
        )
    )
}