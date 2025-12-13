package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import java.time.LocalDate

/**
 * Retrieves the theoretical account position on the specified date
 */
interface GetAccountPositionUseCase {
    fun invoke(targetDate: LocalDate = LocalDate.now()): Double
}

class GetAccountPositionUseCaseImpl : GetAccountPositionUseCase {
    override fun invoke(targetDate: LocalDate): Double {
        TODO("Not yet implemented")
    }
}
