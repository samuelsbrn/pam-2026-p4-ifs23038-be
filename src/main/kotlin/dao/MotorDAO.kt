package org.delcom.dao

import org.delcom.tables.MotorTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID


class MotorDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, MotorDAO>(MotorTable)

    var nama by MotorTable.nama
    var pathGambar by MotorTable.pathGambar
    var deskripsi by MotorTable.deskripsi
    var manfaat by MotorTable.manfaat
    var efekSamping by MotorTable.efekSamping
    var createdAt by MotorTable.createdAt
    var updatedAt by MotorTable.updatedAt
}