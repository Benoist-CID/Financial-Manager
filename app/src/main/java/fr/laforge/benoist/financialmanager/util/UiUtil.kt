package fr.laforge.benoist.financialmanager.util

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.model.TransactionType
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionCategory

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

@Composable
fun TransactionCategory.GetCategoryIcon() {
    val vectorResource =
        when(this) {
            TransactionCategory.None -> ImageVector.vectorResource(id = R.drawable.block)
            TransactionCategory.Food -> ImageVector.vectorResource(id = R.drawable.store)
            TransactionCategory.Bank -> ImageVector.vectorResource(id = R.drawable.bank)
            TransactionCategory.EducationAndFamily -> ImageVector.vectorResource(id = R.drawable.family)
            TransactionCategory.Saving -> ImageVector.vectorResource(id = R.drawable.savings)
            TransactionCategory.Taxes -> ImageVector.vectorResource(id = R.drawable.taxes)
            TransactionCategory.Juridic -> ImageVector.vectorResource(id = R.drawable.juridic)
            TransactionCategory.Accommodation -> ImageVector.vectorResource(id = R.drawable.accomodation)
            TransactionCategory.Leisure -> ImageVector.vectorResource(id = R.drawable.leisure)
            TransactionCategory.Income -> ImageVector.vectorResource(id = R.drawable.income)
            TransactionCategory.Health -> ImageVector.vectorResource(id = R.drawable.health)
            TransactionCategory.Shopping -> ImageVector.vectorResource(id = R.drawable.shopping)
            TransactionCategory.Transport -> ImageVector.vectorResource(id = R.drawable.transport)
            TransactionCategory.Sport -> ImageVector.vectorResource(id = R.drawable.sport)
            TransactionCategory.Vehicle -> ImageVector.vectorResource(id = R.drawable.car)
            TransactionCategory.Telecom -> ImageVector.vectorResource(id = R.drawable.telecom)
            TransactionCategory.Pet -> ImageVector.vectorResource(id = R.drawable.pet)
        }

    return Icon(
            imageVector = vectorResource,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
}
