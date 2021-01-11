package dev.entao.theme

import dev.entao.views.GroupParams
import dev.entao.views.height
import dev.entao.views.width

object Dimen {
    const val textTitle = 18
    const val textPrimary = 16
    const val textSecondary = 14
    const val textThirdly = 12

    const val dialogCorner = 6
    const val barHeight = 50

    const val buttonCorner = 4
    const val buttonHeight = 46
    const val buttonHeightSecondary = 38
    const val buttonHeightSearch = 38
    const val buttonWidthLarge = 240
//    const val buttonWidth = 80
//    const val buttonWidthSecondary = 65

    const val editHeight = 42
    const val editHeightSmall = 38
    const val editHeightSearch = 38
    const val editCorner = 6

}

val <T : GroupParams> T.sizeButtonLarge: T
    get() {
        height(Dimen.buttonHeight)
        width(Dimen.buttonWidthLarge)
        return this
    }
val <T : GroupParams> T.widthButtonLarge: T
    get() {
        return width(Dimen.buttonWidthLarge)
    }
val <T : GroupParams> T.heightButton: T
    get() {
        return height(Dimen.buttonHeight)
    }

val <T : GroupParams> T.heightButtonSecondary: T
    get() {
        return height(Dimen.buttonHeightSecondary)
    }


val <T : GroupParams> T.heightEdit: T
    get() {
        return height(Dimen.editHeight)
    }


val <T : GroupParams> T.heightEditSmall: T
    get() {
        return height(Dimen.editHeightSmall)
    }


val <T : GroupParams> T.heightEditSearch: T
    get() {
        return height(Dimen.editHeightSearch)
    }

val <T : GroupParams> T.heightBar: T
    get() {
        return height(Dimen.barHeight)
    }
