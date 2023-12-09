package fr.laforge.benoist.financialmanager.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.model.TransactionType
import fr.laforge.benoist.model.Transaction

@Composable
fun Transaction.getAmountColor(): Color {
    return if (type == TransactionType.Income) {
        colorResource(R.color.green_1)
    } else {
        colorResource(R.color.red_1)
    }
}

fun Transaction.getAmountFontWeight(): FontWeight {
    return if (type == TransactionType.Income) {
        FontWeight.Bold
    } else {
        FontWeight.Bold
    }
}

@Composable
fun Transaction.getAmountTextStyle(): TextStyle {
    return if (type == TransactionType.Income) {
        TextStyle(background = colorResource(R.color.green_2))
    } else {
        TextStyle(background = colorResource(R.color.red_2))
    }
}

fun Transaction.getFinancialInputText(): String {
    return if (type == TransactionType.Income) {
        " +$amount € "
    } else {
        " -$amount € "
    }
}
