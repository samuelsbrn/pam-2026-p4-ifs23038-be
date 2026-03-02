package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Motor

@Serializable
data class MotorRequest(
    var nama: String = "",
    var deskripsi: String = "",
    var manfaat: String = "",
    var efekSamping: String = "",
    var pathGambar: String = "",
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "deskripsi" to deskripsi,
            "manfaat" to manfaat,
            "efekSamping" to efekSamping,
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Motor {
        return Motor(
            nama = nama,
            deskripsi = deskripsi,
            manfaat = manfaat,
            efekSamping = efekSamping,
            pathGambar =  pathGambar,
        )
    }

}