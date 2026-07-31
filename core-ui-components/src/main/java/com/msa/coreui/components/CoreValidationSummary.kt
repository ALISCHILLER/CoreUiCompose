package com.msa.coreui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.msa.coreui.foundation.CoreUiTheme

/** One actionable validation error shown by [CoreValidationSummary]. */
@Immutable
data class CoreValidationIssue(
    val fieldLabel: String,
    val message: String,
) {
    init {
        require(fieldLabel.isNotBlank()) { "fieldLabel must not be blank." }
        require(message.isNotBlank()) { "message must not be blank." }
    }
}

/** Stable tags for validation-summary UI tests. */
object CoreValidationSummaryTestTags {
    const val ROOT: String = "core-validation-summary"
    fun issue(index: Int): String = "core-validation-summary-issue-$index"
}

/**
 * Accessible form-error summary for long or multi-step forms.
 *
 * When [onIssueClick] is supplied each row becomes an action so the host can request focus or scroll
 * to the associated field. Error details remain ordinary text and are announced as an assertive live
 * region when the summary enters the composition.
 */
@Composable
fun CoreValidationSummary(
    issues: List<CoreValidationIssue>,
    modifier: Modifier = Modifier,
    title: String = CoreUiTheme.strings.validationSummary,
    onIssueClick: ((index: Int) -> Unit)? = null,
) {
    require(issues.isNotEmpty()) { "issues must not be empty." }
    require(title.isNotBlank()) { "title must not be blank." }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag(CoreValidationSummaryTestTags.ROOT)
            .semantics { liveRegion = LiveRegionMode.Assertive },
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        border = BorderStroke(
            width = CoreUiTheme.stroke.strong,
            color = MaterialTheme.colorScheme.error,
        ),
    ) {
        Column(
            modifier = Modifier.padding(CoreUiTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.sm),
        ) {
            Text(
                text = title,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            issues.forEachIndexed { index, issue ->
                ValidationIssueRow(
                    issue = issue,
                    index = index,
                    onClick = onIssueClick?.let { callback -> { callback(index) } },
                )
            }
        }
    }
}

@Composable
private fun ValidationIssueRow(
    issue: CoreValidationIssue,
    index: Int,
    onClick: (() -> Unit)?,
) {
    val rowModifier = Modifier
        .fillMaxWidth()
        .heightIn(min = if (onClick == null) CoreUiTheme.spacing.xxl else CoreUiTheme.sizing.touchTarget)
        .testTag(CoreValidationSummaryTestTags.issue(index))
        .semantics {
            if (onClick != null) role = Role.Button
        }
        .then(
            if (onClick == null) {
                Modifier
            } else {
                Modifier.clickable(role = Role.Button, onClick = onClick)
            },
        )
        .padding(horizontal = CoreUiTheme.spacing.xs, vertical = CoreUiTheme.spacing.sm)

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(CoreUiTheme.sizing.iconMedium)
                .clearAndSetSemantics { },
            contentAlignment = Alignment.Center,
        ) {
            CoreIcon(
                imageVector = CoreIcons.Warning,
                contentDescription = null,
                decorative = true,
                tint = MaterialTheme.colorScheme.error,
            )
        }
        Spacer(Modifier.width(CoreUiTheme.spacing.sm))
        Column(verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.xxs)) {
            Text(
                text = issue.fieldLabel,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = issue.message,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
