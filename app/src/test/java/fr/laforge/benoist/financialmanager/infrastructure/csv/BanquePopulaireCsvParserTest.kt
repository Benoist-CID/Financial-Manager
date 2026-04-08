package fr.laforge.benoist.financialmanager.infrastructure.csv

import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldHaveSize
import org.junit.Test
import java.time.LocalDate

class BanquePopulaireCsvParserTest {

    private val parser = BanquePopulaireCsvParser()

    private val header =
        "Date de comptabilisation;Libelle simplifie;Libelle operation;Reference;" +
            "Informations complementaires;Type operation;Categorie;Sous categorie;" +
            "Debit;Credit;Date operation;Date de valeur;Pointage operation"

    // =========================================================================
    // Happy path — expense (Debit column)
    // =========================================================================

    @Test
    fun `invoke parses an expense row and maps all fields correctly`() {
        // Arrange
        val csv = "$header\n" +
            "27/03/2026;HYPER U;HYPER U FR PERTUIS;3ZDW7ZJ;" +
            "260326 CB****8872-20,47EUR 1 EURO = 1,000000;" +
            "Carte bancaire;Alimentation;Hyper/supermarche;" +
            "-20,47;;27/03/2026;27/03/2026;0"

        // Act
        val result = parser(csv)

        // Assert
        result.isSuccess `should be` true
        val txs = result.getOrThrow()
        txs shouldHaveSize 1
        with(txs[0]) {
            bankId shouldBeEqualTo "3ZDW7ZJ"
            description shouldBeEqualTo "HYPER U"
            amount shouldBeEqualTo 20.47f
            type `should be` TransactionType.Expense
            valueDate shouldBeEqualTo LocalDate.of(2026, 3, 27)
            bookingDate shouldBeEqualTo LocalDate.of(2026, 3, 27)
        }
    }

    // =========================================================================
    // Happy path — income (Credit column)
    // =========================================================================

    @Test
    fun `invoke parses an income row and sets type to Income`() {
        // Arrange
        val csv = "$header\n" +
            "26/03/2026;Wise;VIR INST Wise;WJSP8NH;" +
            "2026LaForgeMonth1 Credence ID-1797130142;" +
            "Virement recu;A categoriser;Virement recu;;+5789,31;26/03/2026;26/03/2026;0"

        // Act
        val result = parser(csv)

        // Assert
        result.isSuccess `should be` true
        with(result.getOrThrow()[0]) {
            bankId shouldBeEqualTo "WJSP8NH"
            amount shouldBeEqualTo 5789.31f
            type `should be` TransactionType.Income
        }
    }

    // =========================================================================
    // Happy path — booking date differs from value date
    // =========================================================================

    @Test
    fun `invoke uses value date (col 11) not booking date (col 0) for valueDate`() {
        // Arrange — booking 25/03, value 24/03
        val csv = "$header\n" +
            "25/03/2026;COTISATIONS BANCAIRES;COTIS CRISTAL PREMIUM;0018844;" +
            "XCCNV069;Frais bancaires;Banque et assurances;Frais bancaires;" +
            "-27,45;;24/03/2026;24/03/2026;0"

        // Act
        val result = parser(csv)

        // Assert
        result.isSuccess `should be` true
        with(result.getOrThrow()[0]) {
            bookingDate shouldBeEqualTo LocalDate.of(2026, 3, 25)
            valueDate shouldBeEqualTo LocalDate.of(2026, 3, 24)
        }
    }

    // =========================================================================
    // Happy path — multiple rows → all returned
    // =========================================================================

    @Test
    fun `invoke returns all rows when multiple data lines are present`() {
        // Arrange
        val csv = "$header\n" +
            "27/03/2026;HYPER U;HYPER U FR PERTUIS;REF1;info;type;cat;sub;-20,47;;27/03/2026;27/03/2026;0\n" +
            "26/03/2026;Wise;Wise;REF2;info;type;cat;sub;;+5789,31;26/03/2026;26/03/2026;0\n" +
            "23/03/2026;SFR;SFR;REF3;info;type;cat;sub;-28,99;;23/03/2026;23/03/2026;0"

        // Act
        val result = parser(csv)

        // Assert
        result.isSuccess `should be` true
        result.getOrThrow() shouldHaveSize 3
    }

    // =========================================================================
    // Edge case — header-only CSV → empty list, not failure
    // =========================================================================

    @Test
    fun `invoke returns empty list when CSV contains only the header row`() {
        // Act
        val result = parser(header)

        // Assert
        result.isSuccess `should be` true
        result.getOrThrow().`should be empty`()
    }

    // =========================================================================
    // Edge case — both Debit and Credit empty → row skipped
    // =========================================================================

    @Test
    fun `invoke silently skips rows where both Debit and Credit are empty`() {
        // Arrange
        val csv = "$header\n" +
            "27/03/2026;SOME TX;SOME TX;REF001;info;type;cat;subcat;;;27/03/2026;27/03/2026;0"

        // Act
        val result = parser(csv)

        // Assert
        result.isSuccess `should be` true
        result.getOrThrow().`should be empty`()
    }

    // =========================================================================
    // Edge case — row with too few columns → skipped, valid rows still returned
    // =========================================================================

    @Test
    fun `invoke skips malformed rows but still returns valid rows`() {
        // Arrange — first row malformed, second row valid
        val csv = "$header\n" +
            "27/03/2026;INCOMPLETE\n" +
            "26/03/2026;Wise;Wise;REF2;info;type;cat;sub;;+1000,00;26/03/2026;26/03/2026;0"

        // Act
        val result = parser(csv)

        // Assert
        result.isSuccess `should be` true
        result.getOrThrow() shouldHaveSize 1
        result.getOrThrow()[0].bankId shouldBeEqualTo "REF2"
    }

    // =========================================================================
    // Edge case — blank lines between data rows are ignored
    // =========================================================================

    @Test
    fun `invoke ignores blank lines between data rows`() {
        // Arrange
        val csv = "$header\n" +
            "27/03/2026;HYPER U;HYPER U FR PERTUIS;REF1;info;type;cat;sub;-20,47;;27/03/2026;27/03/2026;0\n" +
            "\n" +
            "26/03/2026;Wise;Wise;REF2;info;type;cat;sub;;+5789,31;26/03/2026;26/03/2026;0"

        // Act
        val result = parser(csv)

        // Assert
        result.isSuccess `should be` true
        result.getOrThrow() shouldHaveSize 2
    }
}
