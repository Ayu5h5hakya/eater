package com.example.eater.ui.components

import android.content.res.Resources
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.example.eater.model.SnackbarManager
import com.example.eater.ui.theme.EaterTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Wrap Material [androidx.compose.material3.Scaffold] and set [EaterTheme] colors.
 */
@Composable
fun EaterScaffold(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    topBar: @Composable (() -> Unit) = {},
    bottomBar: @Composable (() -> Unit) = {},
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    floatingActionButton: @Composable (() -> Unit) = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    backgroundColor: Color = EaterTheme.colors.uiBackground,
    contentColor: Color = EaterTheme.colors.textSecondary,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = {
            snackbarHost(snackBarHostState)
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = backgroundColor,
        contentColor = contentColor,
        content = content
    )
}

/**
 * Remember and creates an instance of [EaterScaffoldState]
 */
@Composable
fun rememberEaterScaffoldState(
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): EaterScaffoldState = remember(snackBarHostState, snackbarManager, resources, coroutineScope) {
    EaterScaffoldState(snackBarHostState, snackbarManager, resources, coroutineScope)
}

/**
 * Responsible for holding [ScaffoldState], handles the logic of showing snackbar messages
 */
@Stable
class EaterScaffoldState(
    val snackBarHostState: SnackbarHostState,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {
    // Process snackbars coming from SnackbarManager
    init {
        coroutineScope.launch {
            snackbarManager.messages.collect { currentMessages ->
                if (currentMessages.isNotEmpty()) {
                    val message = currentMessages[0]
                    val text = resources.getText(message.messageId)
                    // Notify the SnackbarManager so it can remove the current message from the list
                    snackbarManager.setMessageShown(message.id)
                    // Display the snackbar on the screen. `showSnackbar` is a function
                    // that suspends until the snackbar disappears from the screen
                    snackBarHostState.showSnackbar(text.toString())
                }
            }
        }
    }
}

/**
 * A composable function that returns the [Resources]. It will be recomposed when `Configuration`
 * gets updated.
 */
@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}
