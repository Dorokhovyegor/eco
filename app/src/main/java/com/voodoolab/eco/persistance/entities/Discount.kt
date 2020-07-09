package com.voodoolab.eco.persistance.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "discounts")
data class Discount(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "pk")
    var id: Int

    // todo доделать этот класс

)