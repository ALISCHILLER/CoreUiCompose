package com.msa.coreui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.msa.coreui.foundation.CoreUiTheme

/** UI-facing asynchronous state with explicit loading, empty, failure, and success branches. */
sealed interface CoreAsyncState<out T> {
    data object Loading : CoreAsyncState<Nothing>
    data object Empty : CoreAsyncState<Nothing>

    @Immutable
    data class Failure(
        val title: String,
        val message: String? = null,
    ) : CoreAsyncState<Nothing> {
        init {
            require(title.isNotBlank()) { "title must not be blank." }
            require(message == null || message.isNotBlank()) { "message must be null or non-blank." }
        }
    }

    data class Success<T>(val value: T) : CoreAsyncState<T>
}

/**
 * Renders every asynchronous branch explicitly so loading and failure behavior cannot be omitted by
 * accident. Callers can replace each default branch while keeping one source of truth.
 */
@Composable
fun <T> CoreAsyncContent(
    state: CoreAsyncState<T>,
    content: @Composable (T) -> Unit,
    empty: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    loading: @Composable () -> Unit = {
        CoreLoadingIndicator(label = CoreUiTheme.strings.loading)
    },
    failure: @Composable (CoreAsyncState.Failure, (() -> Unit)?) -> Unit = { value, retry ->
        CoreStateView(
            title = value.title,
            message = value.message,
            style = CoreStateStyle.ERROR,
            actionLabel = retry?.let { CoreUiTheme.strings.retry },
            onAction = retry,
        )
    },
) {
    Box(modifier = modifier) {
        when (state) {
            CoreAsyncState.Loading -> loading()
            CoreAsyncState.Empty -> empty()
            is CoreAsyncState.Failure -> failure(state, onRetry)
            is CoreAsyncState.Success -> content(state.value)
        }
    }
}
