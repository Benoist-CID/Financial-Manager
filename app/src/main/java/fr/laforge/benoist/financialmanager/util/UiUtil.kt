package fr.laforge.benoist.financialmanager.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.InputType

fun Transaction.getAmountColor(): Color {
    return if (type == InputType.Income) {
        Color(0xff22471d)
    } else {
        Color(0xff5e0505)
    }
}

fun Transaction.getAmountFontWeight(): FontWeight {
    return if (type == InputType.Income) {
        FontWeight.Bold
    } else {
        FontWeight.Bold
    }
}

fun Transaction.getAmountTextStyle(): TextStyle {
    return if (type == InputType.Income) {
        TextStyle(background = Color(0xffb1e6aa))
    } else {
        TextStyle(background = Color(0xaafc6d6d))
    }
}

fun Transaction.getFinancialInputText(): String {
    return if (type == InputType.Income) {
        "+$amount €"
    } else {
        "-$amount €"
    }
}
