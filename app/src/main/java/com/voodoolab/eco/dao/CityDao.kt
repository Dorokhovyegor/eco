package com.voodoolab.eco.dao

import androidx.room.*
import com.voodoolab.eco.entities.CityEntity


@Dao
interface CityDao {
    @get:Query("SELECT * FROM cities")
    val all: List<CityEntity>

    @Query("SELECT * FROM cities WHERE id = :id")
    fun getById(id: Long): CityEntity

    @Insert
    fun insert(city: CityEntity)

    @Update
    fun update(city: CityEntity)

    @Delete
    fun delete(city: CityEntity)
}