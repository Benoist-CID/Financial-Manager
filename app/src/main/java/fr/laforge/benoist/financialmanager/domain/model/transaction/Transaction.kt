package fr.laforge.benoist.financialmanager.domain.model.transaction

import fr.laforge.benoist.financialmanager.domain.model.sync.SyncStatus
import java.time.LocalDateTime

/**
 * Core domain entity representing a single financial movement (income or expense).
 *
 * A [Transaction] can be either a **manual entry** (parent == 0, isPeriodic == false),
 * a **periodic template** (isPeriodic == true), or a **generated child instance**
 * (isPeriodic == false, parent != 0) automatically created from a template each period.
 *
 * @property uid       Unique database identifier. 0 for unsaved/new transactions.
 * @property dateTime  The date and time of the transaction.
 * @property amount    The absolute monetary value. Always positive; [type] determines
 *                     whether it is added to or subtracted from the balance.
 * @property description Human-readable label (e.g. "Netflix", "Salary").
 * @property type      [TransactionType.Income] or [TransactionType.Expense].
 * @property isPeriodic `true` when this row is a recurring-transaction **template**.
 *                      Templates are never counted in balance calculations; they are
 *                      used solely to generate child instances.
 * @property period    The recurrence interval; meaningful only when [isPeriodic] is `true`.
 * @property parent    Foreign key to the template's [uid] for generated child instances.
 *                     0 for manual entries and templates.
 * @property category    The budget category this transaction belongs to.
 * @property syncStatus  The reconciliation state of this transaction against the bank account
 *                       statement. Defaults to [SyncStatus.PENDING] for all new transactions.
 *                       [SyncStatus.IN_SYNC] once matched to a bank entry;
 *                       [SyncStatus.NEW_FROM_BANK] when auto-created by the sync process.
 */
data class Transaction(
    val uid: Int = 0,
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val amount: Float = 0F,
    val description: String = "",
    val type: TransactionType = TransactionType.Expense,
    val isPeriodic: Boolean = false,
    val period: TransactionPeriod = TransactionPeriod.None,
    val parent: Int = 0,
    val category: TransactionCategory = TransactionCategory.None,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
)
