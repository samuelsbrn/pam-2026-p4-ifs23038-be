package org.delcom.repositories

import org.delcom.dao.MotorDAO
import org.delcom.entities.Motor
import org.delcom.helpers.daoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.MotorTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class MotorRepository : IMotorRepository {
    override suspend fun getMotors(search: String): List<Motor> = suspendTransaction {
        if (search.isBlank()) {
            MotorDAO.all()
                .orderBy(MotorTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"

            MotorDAO
                .find {
                    MotorTable.nama.lowerCase() like keyword
                }
                .orderBy(MotorTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToModel)
        }
    }

    override suspend fun getMotorById(id: String): Motor? = suspendTransaction {
        MotorDAO
            .find { (MotorTable.id eq UUID.fromString(id)) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun getMotorByName(name: String): Motor? = suspendTransaction {
        MotorDAO
            .find { (MotorTable.nama eq name) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addMotor(motor: Motor): String = suspendTransaction {
        val motorDAO = MotorDAO.new {
            nama = motor.nama
            pathGambar = motor.pathGambar
            deskripsi = motor.deskripsi
            spesifikasi = motor.spesifikasi
            harga = motor.harga
            createdAt = motor.createdAt
            updatedAt = motor.updatedAt
        }

        motorDAO.id.value.toString()
    }

    override suspend fun updateMotor(id: String, newMotor: Motor): Boolean = suspendTransaction {
        val motorDAO = MotorDAO
            .find { MotorTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (motorDAO != null) {
            motorDAO.nama = newMotor.nama
            motorDAO.pathGambar = newMotor.pathGambar
            motorDAO.deskripsi = newMotor.deskripsi
            motorDAO.spesifikasi = newMotor.spesifikasi
            motorDAO.harga = newMotor.harga
            motorDAO.updatedAt = newMotor.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeMotor(id: String): Boolean = suspendTransaction {
        val rowsDeleted = MotorTable.deleteWhere {
            MotorTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}