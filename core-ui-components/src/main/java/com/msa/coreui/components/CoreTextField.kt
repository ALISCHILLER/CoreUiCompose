package com.msa.coreui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import java.util.Locale
import com.msa.coreui.foundation.CoreUiTheme

enum class CoreFieldStatus { DEFAULT, SUCCESS, WARNING, ERROR }

/** Material 3 text field with validation, length constraints, semantics and supporting text. */
@Composable
fun CoreTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    status: CoreFieldStatus = CoreFieldStatus.DEFAULT,
    supportingText: String? = null,
    statusDescription: String? = supportingText,
    placeholder: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    required: Boolean = false,
    maxLength: Int? = null,
    showCharacterCount: Boolean = maxLength != null,
    lengthPolicy: CoreTextLengthPolicy = CoreTextLengthPolicy.CODE_POINTS,
    lengthLocale: Locale = Locale.getDefault(),
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
) {
    require(label.isNotBlank()) { "label must not be blank." }
    require(supportingText == null || supportingText.isNotBlank()) {
        "supportingText must be null or non-blank."
    }
    require(statusDescription == null || statusDescription.isNotBlank()) {
        "statusDescription must be null or non-blank."
    }
    require(placeholder == null || placeholder.isNotBlank()) {
        "placeholder must be null or non-blank."
    }
    require(minLines > 0) { "minLines must be greater than zero." }
    require(maxLines >= minLines) { "maxLines must be greater than or equal to minLines." }
    require(!singleLine || (minLines == 1 && maxLines == 1)) {
        "singleLine fields must use minLines = 1 and maxLines = 1."
    }
    require(maxLength == null || maxLength > 0) { "maxLength must be null or greater than zero." }
    val characterCount = value.textLength(lengthPolicy, lengthLocale)
    require(!showCharacterCount || maxLength != null) {
        "showCharacterCount requires maxLength."
    }

    val exceedsMaxLength = maxLength?.let { characterCount > it } == true
    val effectiveStatus = if (exceedsMaxLength) CoreFieldStatus.ERROR else status
    val lengthValidationMessage = maxLength
        ?.takeIf { exceedsMaxLength }
        ?.let { limit -> CoreUiTheme.strings.charactersOverLimit(characterCount - limit) }
    val effectiveStatusDescription = lengthValidationMessage ?: statusDescription
    val effectiveSupportingText = lengthValidationMessage ?: supportingText
    val statusColor: Color = when (effectiveStatus) {
        CoreFieldStatus.DEFAULT -> MaterialTheme.colorScheme.primary
        CoreFieldStatus.SUCCESS -> CoreUiTheme.statusColors.success
        CoreFieldStatus.WARNING -> CoreUiTheme.statusColors.warning
        CoreFieldStatus.ERROR -> MaterialTheme.colorScheme.error
    }
    val visualCharacterCount = maxLength?.let { limit ->
        "${CoreUiTheme.strings.formatNumber(characterCount)} / ${CoreUiTheme.strings.formatNumber(limit)}"
    }
    val remainingCharactersDescription = maxLength?.let { limit ->
        if (characterCount <= limit) {
            CoreUiTheme.strings.charactersRemaining(limit - characterCount)
        } else {
            CoreUiTheme.strings.charactersOverLimit(characterCount - limit)
        }
    }
    val semanticStates = listOfNotNull(
        effectiveStatusDescription.takeIf { effectiveStatus != CoreFieldStatus.ERROR },
        CoreUiTheme.strings.required.takeIf { required },
        remainingCharactersDescription.takeUnless { exceedsMaxLength },
    )
    val fieldModifier = modifier.semantics {
        if (effectiveStatus == CoreFieldStatus.ERROR) effectiveStatusDescription?.let(::error)
        if (semanticStates.isNotEmpty()) stateDescription = semanticStates.joinToString(", ")
    }

    OutlinedTextField(
        value = value,
        onValueChange = { candidate ->
            if (shouldAcceptTextChange(value, candidate, maxLength, lengthPolicy, lengthLocale)) {
                onValueChange(candidate)
            }
        },
        modifier = fieldModifier,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        label = { Text(if (required) "$label *" else label) },
        placeholder = placeholder?.let { { Text(it) } },
        supportingText = if (effectiveSupportingText != null || showCharacterCount) {
            {
                Row(Modifier.fillMaxWidth()) {
                    if (effectiveSupportingText != null) {
                        Text(
                            text = effectiveSupportingText,
                            modifier = Modifier.weight(1f, fill = false),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                    if (showCharacterCount && visualCharacterCount != null) {
                        Text(
                            text = visualCharacterCount,
                            modifier = Modifier.clearAndSetSemantics { },
                        )
                    }
                }
            }
        } else {
            null
        },
        isError = effectiveStatus == CoreFieldStatus.ERROR,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = statusColor,
            focusedLabelColor = statusColor,
            focusedSupportingTextColor = statusColor,
            unfocusedBorderColor = if (effectiveStatus == CoreFieldStatus.DEFAULT) {
                MaterialTheme.colorScheme.outline
            } else {
                statusColor
            },
            unfocusedLabelColor = if (effectiveStatus == CoreFieldStatus.DEFAULT) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                statusColor
            },
            unfocusedSupportingTextColor = if (effectiveStatus == CoreFieldStatus.DEFAULT) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                statusColor
            },
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorLabelColor = MaterialTheme.colorScheme.error,
            errorSupportingTextColor = MaterialTheme.colorScheme.error,
        ),
    )
}
