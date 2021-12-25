package com.example.simplepomodoro.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "work_package_table",
    foreignKeys = [ForeignKey(
        entity = LabelEntity::class,
        parentColumns = ["name"],
        childColumns = ["labelName"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["labelName"])]
)
data class WorkPackageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val labelName: String?,
    val date: LocalDateTime
)
