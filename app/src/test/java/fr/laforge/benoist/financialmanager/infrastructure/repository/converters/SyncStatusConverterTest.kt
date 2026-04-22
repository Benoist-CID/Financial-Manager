package fr.laforge.benoist.financialmanager.infrastructure.repository.converters

import fr.laforge.benoist.financialmanager.domain.model.sync.SyncStatus
import org.amshove.kluent.`should be equal to`
import org.junit.Test

class SyncStatusConverterTest {

    private val converter = SyncStatusConverter()

    // =========================================================================
    // Happy path — round-trip for every known status
    // =========================================================================

    @Test
    fun `fromSyncStatus produces the enum name string`() {
        converter.fromSyncStatus(SyncStatus.PENDING) `should be equal to` "PENDING"
        converter.fromSyncStatus(SyncStatus.IN_SYNC) `should be equal to` "IN_SYNC"
        converter.fromSyncStatus(SyncStatus.NEW_FROM_BANK) `should be equal to` "NEW_FROM_BANK"
    }

    @Test
    fun `toSyncStatus parses each known status name`() {
        converter.toSyncStatus("PENDING") `should be equal to` SyncStatus.PENDING
        converter.toSyncStatus("IN_SYNC") `should be equal to` SyncStatus.IN_SYNC
        converter.toSyncStatus("NEW_FROM_BANK") `should be equal to` SyncStatus.NEW_FROM_BANK
    }

    // =========================================================================
    // Edge case — unknown value falls back to PENDING
    // =========================================================================

    @Test
    fun `toSyncStatus falls back to PENDING for an unknown string`() {
        // Arrange — a status that might come from a future app version
        val unknownStatus = "FUTURE_STATUS"

        // Act
        val result = converter.toSyncStatus(unknownStatus)

        // Assert
        result `should be equal to` SyncStatus.PENDING
    }

    // =========================================================================
    // Edge case — round-trip integrity
    // =========================================================================

    @Test
    fun `round-trip produces the original status for all enum constants`() {
        SyncStatus.entries.forEach { status ->
            val serialized = converter.fromSyncStatus(status)
            val deserialized = converter.toSyncStatus(serialized)
            deserialized `should be equal to` status
        }
    }
}
