package com.example.simplepomodoro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "label_table")
data class LabelEntity(
    @PrimaryKey val name: String
)
