package fr.laforge.benoist.financialmanager.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionCategory
import fr.laforge.benoist.model.TransactionType

@Composable
fun Transaction.getAmountColor(): Color {
    return if (type == TransactionType.Income) {
        colorResource(R.color.green_1)
    } else {
        MaterialTheme.colorScheme.onBackground
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
        TextStyle(background = Color.Transparent)
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
fun TransactionCategory.GetCategoryIcon(modifier: Modifier = Modifier) {
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

    val color = when(this) {
        TransactionCategory.None -> colorResource(id = R.color.green_3)
        TransactionCategory.Food -> colorResource(id = R.color.red_3)
        TransactionCategory.Bank -> colorResource(id = R.color.grey_3)
        TransactionCategory.EducationAndFamily -> colorResource(id = R.color.orange_3)
        TransactionCategory.Saving -> colorResource(id = R.color.yellow_3)
        TransactionCategory.Taxes -> colorResource(id = R.color.blue_5)
        TransactionCategory.Juridic -> colorResource(id = R.color.green_3)
        TransactionCategory.Accommodation -> colorResource(id = R.color.grey_4)
        TransactionCategory.Leisure -> colorResource(id = R.color.purple_3)
        TransactionCategory.Income -> colorResource(id = R.color.green_4)
        TransactionCategory.Health -> Color.Red
        TransactionCategory.Shopping -> colorResource(id = R.color.yellow_4)
        TransactionCategory.Transport -> colorResource(id = R.color.pink_3)
        TransactionCategory.Sport -> colorResource(id = R.color.blue_4)
        TransactionCategory.Vehicle -> colorResource(id = R.color.blue_3)
        TransactionCategory.Telecom -> colorResource(id = R.color.black)
        TransactionCategory.Pet -> colorResource(id = R.color.brown_3)
    }

    return Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .size(60.dp)
            .padding(10.dp)
            .clip(CircleShape)
            .background(color)

    ) {
        Icon(
            imageVector = vectorResource,
            contentDescription = "",
            tint = Color.White,
            modifier = modifier
                .padding(5.dp)
        )
    }
}

@Composable
fun TransactionCategory.getCategoryColor(): Color {
    return when(this) {
        TransactionCategory.None -> colorResource(id = R.color.green_3)
        TransactionCategory.Food -> colorResource(id = R.color.red_3)
        TransactionCategory.Bank -> colorResource(id = R.color.grey_3)
        TransactionCategory.EducationAndFamily -> colorResource(id = R.color.orange_3)
        TransactionCategory.Saving -> colorResource(id = R.color.yellow_3)
        TransactionCategory.Taxes -> colorResource(id = R.color.blue_5)
        TransactionCategory.Juridic -> colorResource(id = R.color.green_3)
        TransactionCategory.Accommodation -> colorResource(id = R.color.grey_4)
        TransactionCategory.Leisure -> colorResource(id = R.color.purple_3)
        TransactionCategory.Income -> colorResource(id = R.color.green_4)
        TransactionCategory.Health -> Color.Red
        TransactionCategory.Shopping -> colorResource(id = R.color.yellow_4)
        TransactionCategory.Transport -> colorResource(id = R.color.pink_3)
        TransactionCategory.Sport -> colorResource(id = R.color.blue_4)
        TransactionCategory.Vehicle -> colorResource(id = R.color.blue_3)
        TransactionCategory.Telecom -> colorResource(id = R.color.black)
        TransactionCategory.Pet -> colorResource(id = R.color.brown_3)
        else -> {
            colorResource(id = R.color.white)}
    }
}

@Preview
@Composable
fun GetCategoryIconPreview() {
    TransactionCategory.Accommodation.GetCategoryIcon()
}
