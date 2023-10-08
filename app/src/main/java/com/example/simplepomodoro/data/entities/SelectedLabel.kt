package com.example.simplepomodoro.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.ForeignKey.Companion.SET_NULL
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = LabelEntity::class,
        parentColumns = ["name"],
        childColumns = ["selectedLabelName"],
        onDelete = SET_NULL,
        onUpdate = CASCADE,
    )]
)
data class SelectedLabel(
    @PrimaryKey val id: Long = 0,
    val selectedLabelName: String? = null
)
