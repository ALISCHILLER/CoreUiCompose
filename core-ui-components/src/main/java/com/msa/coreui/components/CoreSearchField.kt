package com.msa.coreui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.msa.coreui.foundation.CoreUiTheme
import java.util.Locale

/**
 * Search input with IME-search handling, standard search affordance, and an accessible clear action.
 * The query remains fully controlled by the caller.
 */
@Composable
fun CoreSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = CoreUiTheme.strings.search,
    placeholder: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    maxLength: Int? = null,
    showCharacterCount: Boolean = false,
    lengthPolicy: CoreTextLengthPolicy = CoreTextLengthPolicy.CODE_POINTS,
    lengthLocale: Locale = Locale.getDefault(),
    clearContentDescription: String = CoreUiTheme.strings.clear,
    leadingIcon: (@Composable () -> Unit)? = {
        CoreIcon(
            imageVector = CoreIcons.Search,
            contentDescription = null,
            decorative = true,
        )
    },
) {
    require(label.isNotBlank()) { "label must not be blank." }
    require(clearContentDescription.isNotBlank()) { "clearContentDescription must not be blank." }
    val focusManager = LocalFocusManager.current

    CoreTextField(
        value = query,
        onValueChange = onQueryChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        enabled = enabled,
        readOnly = readOnly,
        maxLength = maxLength,
        showCharacterCount = showCharacterCount,
        lengthPolicy = lengthPolicy,
        lengthLocale = lengthLocale,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch(query)
                focusManager.clearFocus()
            },
        ),
        leadingIcon = leadingIcon,
        trailingIcon = if (query.isNotEmpty() && enabled && !readOnly) {
            {
                CoreIconButton(
                    onClick = { onQueryChange("") },
                    contentDescription = clearContentDescription,
                ) {
                    CoreIcon(
                        imageVector = CoreIcons.Close,
                        contentDescription = null,
                        decorative = true,
                    )
                }
            }
        } else {
            null
        },
    )
}
