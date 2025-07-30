package com.sewagadget.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Kelas data ini merepresentasikan gabungan antara satu transaksi
 * dengan data gadget yang bersangkutan.
 */
data class TransactionWithGadget(
    @Embedded
    val transaction: Transaction,

    @Relation(
        parentColumn = "gadgetId",
        entityColumn = "id"
    )
    val gadget: Gadget
)