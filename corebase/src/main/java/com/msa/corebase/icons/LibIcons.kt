@file:Suppress("PropertyName")

package com.msa.corebase.icons

import androidx.compose.ui.graphics.vector.ImageVector

sealed class LibIcons {

    abstract var EmojiEmotions: ImageVector
    abstract var EmojiNature: ImageVector
    abstract var EmojiFoodBeverage: ImageVector
    abstract var EmojiTransportation: ImageVector
    abstract var EmojiEvents: ImageVector
    abstract var EmojiObjects: ImageVector
    abstract var EmojiSymbols: ImageVector
    abstract var EmojiFlags: ImageVector
    abstract var ContentCopy: ImageVector
    abstract var ContentPaste: ImageVector
    abstract var Apps: ImageVector
    abstract var Tune: ImageVector
    abstract var NotInterested: ImageVector
    abstract var Backspace: ImageVector
    abstract var Clear: ImageVector
    abstract var ChevronRight: ImageVector
    abstract var ChevronLeft: ImageVector
    abstract var ExpandMore: ImageVector
    abstract var Check: ImageVector
    abstract var Star: ImageVector
    abstract var Info: ImageVector
    abstract var Error: ImageVector

    object Rounded : LibIcons() {
        override var EmojiEmotions = com.msa.corebase.icons.rounded.EmojiEmotions
        override var EmojiNature = com.msa.corebase.icons.rounded.EmojiNature
        override var EmojiFoodBeverage = com.msa.corebase.icons.rounded.EmojiFoodBeverage
        override var EmojiTransportation = com.msa.corebase.icons.rounded.EmojiTransportation
        override var EmojiEvents = com.msa.corebase.icons.rounded.EmojiEvents
        override var EmojiObjects = com.msa.corebase.icons.rounded.EmojiObjects
        override var EmojiSymbols = com.msa.corebase.icons.rounded.EmojiSymbols
        override var EmojiFlags = com.msa.corebase.icons.rounded.EmojiFlags
        override var ContentCopy = com.msa.corebase.icons.rounded.ContentCopy
        override var ContentPaste = com.msa.corebase.icons.rounded.ContentPaste
        override var Apps = com.msa.corebase.icons.rounded.Apps
        override var Tune = com.msa.corebase.icons.rounded.Tune
        override var NotInterested = com.msa.corebase.icons.rounded.NotInterested
        override var Backspace = com.msa.corebase.icons.rounded.Backspace
        override var Clear = com.msa.corebase.icons.rounded.Clear
        override var ChevronRight = com.msa.corebase.icons.rounded.ChevronRight
        override var ChevronLeft = com.msa.corebase.icons.rounded.ChevronLeft
        override var ExpandMore = com.msa.corebase.icons.rounded.ExpandMore
        override var Check = com.msa.corebase.icons.rounded.Check
        override var Star = com.msa.corebase.icons.rounded.Star
        override var Info = com.msa.corebase.icons.rounded.Info
        override var Error = com.msa.corebase.icons.rounded.Error

    }
}