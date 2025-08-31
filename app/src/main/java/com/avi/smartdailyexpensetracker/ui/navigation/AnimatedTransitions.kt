package com.avi.smartdailyexpensetracker.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry

@ExperimentalAnimationApi
fun slideInTransition(): (AnimatedContentScope.() -> EnterTransition) = {
    slideInHorizontally(
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        initialOffsetX = { fullWidth -> fullWidth }
    ) + fadeIn(
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    )
}

@ExperimentalAnimationApi
fun slideOutTransition(): (AnimatedContentScope.() -> ExitTransition) = {
    slideOutHorizontally(
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        targetOffsetX = { fullWidth -> -fullWidth }
    ) + fadeOut(
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    )
}

@ExperimentalAnimationApi
fun slideInFromBottomTransition(): (AnimatedContentScope.() -> EnterTransition) = {
    slideInVertically(
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        initialOffsetY = { fullHeight -> fullHeight }
    ) + fadeIn(
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    )
}

@ExperimentalAnimationApi
fun slideOutToBottomTransition(): (AnimatedContentScope.() -> ExitTransition) = {
    slideOutVertically(
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        targetOffsetY = { fullHeight -> fullHeight }
    ) + fadeOut(
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    )
}

@ExperimentalAnimationApi
fun scaleInTransition(): (AnimatedContentScope.() -> EnterTransition) = {
    scaleIn(
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        initialScale = 0.8f
    ) + fadeIn(
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    )
}

@ExperimentalAnimationApi
fun scaleOutTransition(): (AnimatedContentScope.() -> ExitTransition) = {
    scaleOut(
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        targetScale = 0.8f
    ) + fadeOut(
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    )
}
