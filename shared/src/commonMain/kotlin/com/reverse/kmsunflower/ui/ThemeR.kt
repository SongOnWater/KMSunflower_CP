///*
// * Copyright 2023 Google LLC
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.reverse.kmsunflower.ui
//import androidx.compose.foundation.isSystemInDarkTheme
//import androidx.compose.material3.ColorScheme
//import androidx.compose.material3.darkColorScheme
//import androidx.compose.material3.lightColorScheme
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import com.moriatsushi.insetsx.rememberWindowInsetsController
//import com.reverse.kmsunflower.MR.colors.md_theme_light_primary
//import com.reverse.kmsunflower.MR.colors.md_theme_light_onPrimary
//import com.reverse.kmsunflower.MR.colors.md_theme_light_primaryContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_light_onPrimaryContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_light_secondary
//import com.reverse.kmsunflower.MR.colors.md_theme_light_onSecondary
//import com.reverse.kmsunflower.MR.colors.md_theme_light_secondaryContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_light_onSecondaryContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_light_tertiary
//import com.reverse.kmsunflower.MR.colors.md_theme_light_onTertiary
//import com.reverse.kmsunflower.MR.colors.md_theme_light_tertiaryContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_light_onTertiaryContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_light_error
//import com.reverse.kmsunflower.MR.colors.md_theme_light_errorContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_light_onError
//import com.reverse.kmsunflower.MR.colors.md_theme_light_onErrorContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_light_background
//import com.reverse.kmsunflower.MR.colors.md_theme_light_onBackground
//import com.reverse.kmsunflower.MR.colors.md_theme_light_surface
//import com.reverse.kmsunflower.MR.colors.md_theme_light_onSurface
//import com.reverse.kmsunflower.MR.colors.md_theme_light_surfaceVariant
//import com.reverse.kmsunflower.MR.colors.md_theme_light_onSurfaceVariant
//import com.reverse.kmsunflower.MR.colors.md_theme_light_outline
//import com.reverse.kmsunflower.MR.colors.md_theme_light_inverseOnSurface
//import com.reverse.kmsunflower.MR.colors.md_theme_light_inverseSurface
//import com.reverse.kmsunflower.MR.colors.md_theme_light_inversePrimary
//import com.reverse.kmsunflower.MR.colors.md_theme_light_surfaceTint
//import com.reverse.kmsunflower.MR.colors.md_theme_light_outlineVariant
//import com.reverse.kmsunflower.MR.colors.md_theme_light_scrim
//
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_primary
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_onPrimary
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_primaryContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_onPrimaryContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_secondary
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_onSecondary
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_secondaryContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_onSecondaryContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_tertiary
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_onTertiary
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_tertiaryContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_onTertiaryContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_error
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_errorContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_onError
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_onErrorContainer
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_background
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_onBackground
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_surface
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_onSurface
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_surfaceVariant
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_onSurfaceVariant
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_outline
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_inverseOnSurface
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_inverseSurface
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_inversePrimary
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_surfaceTint
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_outlineVariant
//import com.reverse.kmsunflower.MR.colors.md_theme_dark_scrim
//
//import com.reverse.kmsunflower.utilities.Log
//import dev.icerock.moko.resources.compose.colorResource
//
//@Composable
//private fun LightColors() = lightColorScheme(
//    primary = colorResource(md_theme_light_primary),
//    onPrimary = colorResource(md_theme_light_onPrimary),
//    primaryContainer = colorResource(md_theme_light_primaryContainer),
//    onPrimaryContainer = colorResource(md_theme_light_onPrimaryContainer),
//    secondary = colorResource(md_theme_light_secondary),
//    onSecondary = colorResource(md_theme_light_onSecondary),
//    secondaryContainer = colorResource(md_theme_light_secondaryContainer),
//    onSecondaryContainer = colorResource(md_theme_light_onSecondaryContainer),
//    tertiary = colorResource(md_theme_light_tertiary),
//    onTertiary = colorResource(md_theme_light_onTertiary),
//    tertiaryContainer = colorResource(md_theme_light_tertiaryContainer),
//    onTertiaryContainer = colorResource(md_theme_light_onTertiaryContainer),
//    error = colorResource(md_theme_light_error),
//    errorContainer = colorResource(md_theme_light_errorContainer),
//    onError = colorResource(md_theme_light_onError),
//    onErrorContainer = colorResource(md_theme_light_onErrorContainer),
//    background = colorResource(md_theme_light_background),
//    onBackground = colorResource(md_theme_light_onBackground),
//    surface = colorResource(md_theme_light_surface),
//    onSurface = colorResource(md_theme_light_onSurface),
//    surfaceVariant = colorResource(md_theme_light_surfaceVariant),
//    onSurfaceVariant = colorResource(md_theme_light_onSurfaceVariant),
//    outline = colorResource(md_theme_light_outline),
//    inverseOnSurface = colorResource(md_theme_light_inverseOnSurface),
//    inverseSurface = colorResource(md_theme_light_inverseSurface),
//    inversePrimary = colorResource(md_theme_light_inversePrimary),
//    surfaceTint = colorResource(md_theme_light_surfaceTint),
//    outlineVariant = colorResource(md_theme_light_outlineVariant),
//    scrim = colorResource(md_theme_light_scrim),
//)
//
//@Composable
//private fun  DarkColors() = darkColorScheme(
//    primary = colorResource(md_theme_dark_primary),
//    onPrimary = colorResource(md_theme_dark_onPrimary),
//    primaryContainer = colorResource(md_theme_dark_primaryContainer),
//    onPrimaryContainer = colorResource(md_theme_dark_onPrimaryContainer),
//    secondary = colorResource(md_theme_dark_secondary),
//    onSecondary = colorResource(md_theme_dark_onSecondary),
//    secondaryContainer = colorResource(md_theme_dark_secondaryContainer),
//    onSecondaryContainer = colorResource(md_theme_dark_onSecondaryContainer),
//    tertiary = colorResource(md_theme_dark_tertiary),
//    onTertiary = colorResource(md_theme_dark_onTertiary),
//    tertiaryContainer = colorResource(md_theme_dark_tertiaryContainer),
//    onTertiaryContainer = colorResource(md_theme_dark_onTertiaryContainer),
//    error = colorResource(md_theme_dark_error),
//    errorContainer = colorResource(md_theme_dark_errorContainer),
//    onError = colorResource(md_theme_dark_onError),
//    onErrorContainer = colorResource(md_theme_dark_onErrorContainer),
//    background = colorResource(md_theme_dark_background),
//    onBackground = colorResource(md_theme_dark_onBackground),
//    surface = colorResource(md_theme_dark_surface),
//    onSurface = colorResource(md_theme_dark_onSurface),
//    surfaceVariant = colorResource(md_theme_dark_surfaceVariant),
//    onSurfaceVariant = colorResource(md_theme_dark_onSurfaceVariant),
//    outline = colorResource(md_theme_dark_outline),
//    inverseOnSurface = colorResource(md_theme_dark_inverseOnSurface),
//    inverseSurface = colorResource(md_theme_dark_inverseSurface),
//    inversePrimary = colorResource(md_theme_dark_inversePrimary),
//    surfaceTint = colorResource(md_theme_dark_surfaceTint),
//    outlineVariant = colorResource(md_theme_dark_outlineVariant),
//    scrim = colorResource(md_theme_dark_scrim),
//)
//
//@Composable
//fun SunflowerTheme(
//    colorScheme: ColorScheme =  if(isSystemInDarkTheme()) DarkColors() else  LightColors(),
//    // Dynamic color is available on Android 12+
////    dynamicColor: Boolean = false,
//    content: @Composable () -> Unit
//) {
////    val colorScheme = when {
////        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
////            val context = LocalContext.current
////            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
////        }
////
////        darkTheme -> DarkColors
////        else -> LightColors
////    }
////    val view = LocalView.current
////    if (!view.isInEditMode) {
////        val systemUiController = rememberSystemUiController()
////        val useDarkIcons = !isSystemInDarkTheme()
////        val window = (view.context as Activity).window
////        WindowCompat.setDecorFitsSystemWindows(window, false)
////        DisposableEffect(systemUiController, useDarkIcons) {
////            // Update all of the system bar colors to be transparent, and use
////            // dark icons if we're in light theme
////            systemUiController.setSystemBarsColor(
////                color = Color.Transparent,
////                darkIcons = useDarkIcons
////            )
////            onDispose {}
////        }
////    }
//
//    val windowInsetsController = rememberWindowInsetsController()
//    windowInsetsController?.apply {
//        Log.i("InsetsController")
//        setIsStatusBarsVisible(true)
//        setStatusBarContentColor(dark = isSystemInDarkTheme())
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        shapes = Shapes,
//        typography = Typography,
//        content = content
//    )
//}