package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Motor

@Serializable
data class MotorRequest(
    var nama: String = "",
    var deskripsi: String = "",
    var spesifikasi: String = "",
    var harga: String = "",
    var pathGambar: String = "",
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "deskripsi" to deskripsi,
            "spesifikasi" to spesifikasi, // UBAH "manfaat" JADI INI
            "harga" to harga,             // UBAH "efekSamping" JADI INI
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Motor {
        return Motor(
            nama = nama,
            deskripsi = deskripsi,
            spesifikasi = spesifikasi,
            harga = harga,
            pathGambar = pathGambar,
        )
    }
}