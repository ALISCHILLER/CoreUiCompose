package com.msa.coreui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Small dependency-free icon set used by Core UI primitives and available to consumers. */
@Immutable
object CoreIcons {
    val Search: ImageVector by lazy {
        strokedIcon("Search") {
            moveTo(10.8f, 4f)
            curveTo(7.04f, 4f, 4f, 7.04f, 4f, 10.8f)
            curveTo(4f, 14.56f, 7.04f, 17.6f, 10.8f, 17.6f)
            curveTo(14.56f, 17.6f, 17.6f, 14.56f, 17.6f, 10.8f)
            curveTo(17.6f, 7.04f, 14.56f, 4f, 10.8f, 4f)
            close()
            moveTo(16f, 16f)
            lineTo(21f, 21f)
        }
    }

    val Close: ImageVector by lazy {
        strokedIcon("Close") {
            moveTo(5f, 5f)
            lineTo(19f, 19f)
            moveTo(19f, 5f)
            lineTo(5f, 19f)
        }
    }

    val Add: ImageVector by lazy {
        strokedIcon("Add") {
            moveTo(12f, 4f)
            verticalLineTo(20f)
            moveTo(4f, 12f)
            horizontalLineTo(20f)
        }
    }

    val Check: ImageVector by lazy {
        strokedIcon("Check") {
            moveTo(4f, 12.5f)
            lineTo(9.5f, 18f)
            lineTo(20f, 6f)
        }
    }

    val ChevronDown: ImageVector by lazy {
        strokedIcon("ChevronDown") {
            moveTo(5f, 9f)
            lineTo(12f, 16f)
            lineTo(19f, 9f)
        }
    }

    val ChevronLeft: ImageVector by lazy {
        strokedIcon("ChevronLeft") {
            moveTo(15f, 5f)
            lineTo(8f, 12f)
            lineTo(15f, 19f)
        }
    }

    val ChevronRight: ImageVector by lazy {
        strokedIcon("ChevronRight") {
            moveTo(9f, 5f)
            lineTo(16f, 12f)
            lineTo(9f, 19f)
        }
    }

    val ArrowBack: ImageVector by lazy {
        strokedIcon("ArrowBack") {
            moveTo(20f, 12f)
            horizontalLineTo(5f)
            moveTo(11f, 6f)
            lineTo(5f, 12f)
            lineTo(11f, 18f)
        }
    }

    val Menu: ImageVector by lazy {
        strokedIcon("Menu") {
            moveTo(4f, 6f)
            horizontalLineTo(20f)
            moveTo(4f, 12f)
            horizontalLineTo(20f)
            moveTo(4f, 18f)
            horizontalLineTo(20f)
        }
    }

    val MoreVertical: ImageVector by lazy {
        filledIcon("MoreVertical") {
            moveTo(12f, 5f)
            arcToRelative(1.5f, 1.5f, 0f, false, true, 0f, 3f)
            arcToRelative(1.5f, 1.5f, 0f, false, true, 0f, -3f)
            close()
            moveTo(12f, 10.5f)
            arcToRelative(1.5f, 1.5f, 0f, false, true, 0f, 3f)
            arcToRelative(1.5f, 1.5f, 0f, false, true, 0f, -3f)
            close()
            moveTo(12f, 16f)
            arcToRelative(1.5f, 1.5f, 0f, false, true, 0f, 3f)
            arcToRelative(1.5f, 1.5f, 0f, false, true, 0f, -3f)
            close()
        }
    }

    val Home: ImageVector by lazy {
        strokedIcon("Home") {
            moveTo(3f, 11f)
            lineTo(12f, 3f)
            lineTo(21f, 11f)
            verticalLineTo(20f)
            horizontalLineTo(15f)
            verticalLineTo(14f)
            horizontalLineTo(9f)
            verticalLineTo(20f)
            horizontalLineTo(3f)
            close()
        }
    }

    val Settings: ImageVector by lazy {
        strokedIcon("Settings") {
            moveTo(4f, 6f)
            horizontalLineTo(10f)
            moveTo(14f, 6f)
            horizontalLineTo(20f)
            moveTo(12f, 4f)
            verticalLineTo(8f)
            moveTo(4f, 12f)
            horizontalLineTo(5f)
            moveTo(9f, 12f)
            horizontalLineTo(20f)
            moveTo(7f, 10f)
            verticalLineTo(14f)
            moveTo(4f, 18f)
            horizontalLineTo(14f)
            moveTo(18f, 18f)
            horizontalLineTo(20f)
            moveTo(16f, 16f)
            verticalLineTo(20f)
        }
    }

    val Calendar: ImageVector by lazy {
        strokedIcon("Calendar") {
            moveTo(5f, 4f)
            horizontalLineTo(19f)
            curveTo(20.1f, 4f, 21f, 4.9f, 21f, 6f)
            verticalLineTo(19f)
            curveTo(21f, 20.1f, 20.1f, 21f, 19f, 21f)
            horizontalLineTo(5f)
            curveTo(3.9f, 21f, 3f, 20.1f, 3f, 19f)
            verticalLineTo(6f)
            curveTo(3f, 4.9f, 3.9f, 4f, 5f, 4f)
            close()
            moveTo(7f, 2.5f)
            verticalLineTo(7f)
            moveTo(17f, 2.5f)
            verticalLineTo(7f)
            moveTo(3f, 9f)
            horizontalLineTo(21f)
        }
    }

    val Visibility: ImageVector by lazy {
        strokedIcon("Visibility") {
            moveTo(2.5f, 12f)
            curveTo(5f, 7.5f, 8.2f, 5.5f, 12f, 5.5f)
            curveTo(15.8f, 5.5f, 19f, 7.5f, 21.5f, 12f)
            curveTo(19f, 16.5f, 15.8f, 18.5f, 12f, 18.5f)
            curveTo(8.2f, 18.5f, 5f, 16.5f, 2.5f, 12f)
            close()
            moveTo(12f, 9f)
            curveTo(10.34f, 9f, 9f, 10.34f, 9f, 12f)
            curveTo(9f, 13.66f, 10.34f, 15f, 12f, 15f)
            curveTo(13.66f, 15f, 15f, 13.66f, 15f, 12f)
            curveTo(15f, 10.34f, 13.66f, 9f, 12f, 9f)
            close()
        }
    }

    val VisibilityOff: ImageVector by lazy {
        strokedIcon("VisibilityOff") {
            moveTo(3f, 3f)
            lineTo(21f, 21f)
            moveTo(10.2f, 5.8f)
            curveTo(10.8f, 5.6f, 11.4f, 5.5f, 12f, 5.5f)
            curveTo(15.8f, 5.5f, 19f, 7.5f, 21.5f, 12f)
            curveTo(20.6f, 13.6f, 19.6f, 14.9f, 18.4f, 16f)
            moveTo(15.7f, 18f)
            curveTo(14.5f, 18.4f, 13.3f, 18.5f, 12f, 18.5f)
            curveTo(8.2f, 18.5f, 5f, 16.5f, 2.5f, 12f)
            curveTo(3.5f, 10.2f, 4.7f, 8.7f, 6.1f, 7.6f)
            moveTo(9.9f, 9.9f)
            curveTo(9.3f, 10.4f, 9f, 11.2f, 9f, 12f)
            curveTo(9f, 13.66f, 10.34f, 15f, 12f, 15f)
            curveTo(12.8f, 15f, 13.6f, 14.7f, 14.1f, 14.1f)
        }
    }

    val Info: ImageVector by lazy {
        strokedIcon("Info") {
            moveTo(12f, 3f)
            curveTo(7.03f, 3f, 3f, 7.03f, 3f, 12f)
            curveTo(3f, 16.97f, 7.03f, 21f, 12f, 21f)
            curveTo(16.97f, 21f, 21f, 16.97f, 21f, 12f)
            curveTo(21f, 7.03f, 16.97f, 3f, 12f, 3f)
            close()
            moveTo(12f, 10.5f)
            verticalLineTo(17f)
            moveTo(12f, 7f)
            lineTo(12f, 7.1f)
        }
    }

    val Warning: ImageVector by lazy {
        strokedIcon("Warning") {
            moveTo(12f, 3f)
            lineTo(22f, 20f)
            horizontalLineTo(2f)
            close()
            moveTo(12f, 9f)
            verticalLineTo(14f)
            moveTo(12f, 17f)
            lineTo(12f, 17.1f)
        }
    }

    val Refresh: ImageVector by lazy {
        strokedIcon("Refresh") {
            moveTo(20f, 7f)
            verticalLineTo(3f)
            horizontalLineTo(16f)
            moveTo(20f, 3f)
            lineTo(16.5f, 6.5f)
            curveTo(15.2f, 5.2f, 13.6f, 4.5f, 11.8f, 4.5f)
            curveTo(7.7f, 4.5f, 4.5f, 7.7f, 4.5f, 11.8f)
            curveTo(4.5f, 15.9f, 7.7f, 19.1f, 11.8f, 19.1f)
            curveTo(15.3f, 19.1f, 18.2f, 16.7f, 19f, 13.5f)
        }
    }
}

/** Material icon wrapper with a required accessible label unless [decorative] is true. */
@Composable
fun CoreIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    decorative: Boolean = false,
    tint: Color = LocalContentColor.current,
) {
    require(decorative || !contentDescription.isNullOrBlank()) {
        "contentDescription must be non-blank unless the icon is decorative."
    }
    Icon(
        imageVector = imageVector,
        contentDescription = if (decorative) null else contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

private fun strokedIcon(
    name: String,
    block: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit,
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    path(
        fill = null,
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
        pathBuilder = block,
    )
}.build()

private fun filledIcon(
    name: String,
    block: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit,
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    path(
        fill = SolidColor(Color.Black),
        pathBuilder = block,
    )
}.build()
