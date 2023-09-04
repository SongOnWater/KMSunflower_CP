/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reverse.kmsunflower.compose.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import com.reverse.kmsunflower.getPlatform
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.option.Scale
import com.seiko.imageloader.rememberImageAction
import com.seiko.imageloader.rememberImageActionPainter


/**
 * Wrapper around a [GlideImage] so that composable previews work.
 * This can be removed once https://github.com/bumptech/glide/issues/4977 is fixed.
 */
@Composable
fun SunflowerImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    var boxSize by remember { mutableStateOf(Size.Zero) }
    val density = LocalDensity.current.density
    Box(Modifier.onGloballyPositioned { coordinates ->
        boxSize = coordinates.size.toSize()
    }, Alignment.Center) {
        val request = remember(model) {
            ImageRequest {
                data(model)
                scale(Scale.FIT)
                addInterceptor(NullDataInterceptor)
                options {
                    maxImageSize = (boxSize.width.toInt()* density).toInt()
                }
            }
        }
        val action by rememberImageAction(request)
        val painter = rememberImageActionPainter(action)
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter
        )
    }

}