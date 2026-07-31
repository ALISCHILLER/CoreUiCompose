package com.msa.coreui.components

import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics

/** Accessible settings row backed by a switch. The entire row is the touch target. */
@Composable
fun CoreSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    enabled: Boolean = true,
) {
    CoreListItem(
        title = title,
        supportingText = supportingText,
        modifier = modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Switch,
            onValueChange = onCheckedChange,
        ),
        enabled = enabled,
        trailing = {
            Switch(
                checked = checked,
                onCheckedChange = null,
                enabled = enabled,
                modifier = Modifier.clearAndSetSemantics { },
            )
        },
    )
}

/** Accessible settings row backed by a checkbox. */
@Composable
fun CoreCheckboxRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    enabled: Boolean = true,
) {
    CoreListItem(
        title = title,
        supportingText = supportingText,
        modifier = modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Checkbox,
            onValueChange = onCheckedChange,
        ),
        enabled = enabled,
        trailing = {
            Checkbox(
                checked = checked,
                onCheckedChange = null,
                enabled = enabled,
                modifier = Modifier.clearAndSetSemantics { },
            )
        },
    )
}

/** Accessible single-choice row backed by a radio button. */
@Composable
fun CoreRadioRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    enabled: Boolean = true,
) {
    CoreListItem(
        title = title,
        supportingText = supportingText,
        modifier = modifier.selectable(
            selected = selected,
            enabled = enabled,
            role = Role.RadioButton,
            onClick = onClick,
        ),
        enabled = enabled,
        trailing = {
            RadioButton(
                selected = selected,
                onClick = null,
                enabled = enabled,
                modifier = Modifier.clearAndSetSemantics { },
            )
        },
    )
}
