package com.cesoft.cesnostr.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
//Text(
//AnnotatedString.fromHtml(
//htmlText,
//linkStyles = TextLinkStyles(
//style = SpanStyle(
//textDecoration = TextDecoration.Underline,
//fontStyle = FontStyle.Italic,
//color = Color.Blue

//https://developer.android.com/develop/ui/compose/text/style-text?hl=es-419

@Composable
fun StyledField(name: String, value: String?) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                append(name)
                append(": ")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(value) }
        }
    )
}