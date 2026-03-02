package org.delcom.repositories

import org.delcom.entities.Motor

interface  IMotorRepository {
    suspend fun getMotors(search: String): List<Motor>
    suspend fun getMotorById(id: String): Motor?
    suspend fun getMotorByName(name: String): Motor?
    suspend fun addMotor(motor: Motor) : String
    suspend fun updateMotor(id: String, newMotor: Motor): Boolean
    suspend fun removeMotor(id: String): Boolean
}