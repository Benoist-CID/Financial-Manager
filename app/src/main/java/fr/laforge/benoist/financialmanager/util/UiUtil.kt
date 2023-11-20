package fr.laforge.benoist.financialmanager.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.InputType

fun Transaction.getAmountColor(): Color {
    return if (type == InputType.Income) {
        Color(0xFF22471d)
    } else {
        Color.Black
    }
}

fun Transaction.getAmountFontWeight(): FontWeight {
    return if (type == InputType.Income) {
        FontWeight.Bold
    } else {
        FontWeight.Normal
    }
}

fun Transaction.getAmountTextStyle(): TextStyle {
    return if (type == InputType.Income) {
        TextStyle(background = Color(0xFFb1e6aa))
    } else {
        TextStyle()
    }
}

fun Transaction.getFinancialInputText(): String {
    return if (type == InputType.Income) {
        "$amount €"
    } else {
        "-$amount €"
    }
}
