package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object MotorTable : UUIDTable("motors") {
    val nama = varchar("nama", 100)
    val pathGambar = varchar("path_gambar", 255)
    val deskripsi = text("deskripsi")
    val spesifikasi = text("spesifikasi")
    val harga = text("harga")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}