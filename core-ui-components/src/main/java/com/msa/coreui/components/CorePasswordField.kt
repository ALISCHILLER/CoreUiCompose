package com.msa.coreui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.msa.coreui.foundation.CoreUiTheme
import java.util.Locale

/** Controlled password field with accessible visibility toggle and shared validation behavior. */
@Composable
fun CorePasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
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
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    CoreTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        status = status,
        supportingText = supportingText,
        statusDescription = statusDescription,
        placeholder = placeholder,
        enabled = enabled,
        readOnly = readOnly,
        required = required,
        maxLength = maxLength,
        showCharacterCount = showCharacterCount,
        lengthPolicy = lengthPolicy,
        lengthLocale = lengthLocale,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        keyboardActions = keyboardActions,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        leadingIcon = leadingIcon,
        trailingIcon = {
            CoreIconButton(
                onClick = { onPasswordVisibilityChange(!passwordVisible) },
                contentDescription = if (passwordVisible) {
                    CoreUiTheme.strings.hidePassword
                } else {
                    CoreUiTheme.strings.showPassword
                },
                enabled = enabled && !readOnly,
            ) {
                CoreIcon(
                    imageVector = if (passwordVisible) CoreIcons.VisibilityOff else CoreIcons.Visibility,
                    contentDescription = null,
                    decorative = true,
                )
            }
        },
    )
}
