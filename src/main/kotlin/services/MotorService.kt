package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.MotorRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IMotorRepository
import java.io.File
import java.util.*

class MotorService(private val motorRepository: IMotorRepository) {
    // Mengambil semua data Motor
    suspend fun getAllMotors(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""

        val motors = motorRepository.getMotors(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar Motor",
            mapOf(Pair("motors", motors))
        )
        call.respond(response)
    }

    // Mengambil data Motor berdasarkan id
    suspend fun getMotorById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID Motor tidak boleh kosong!")

        val motor = motorRepository.getMotorById(id) ?: throw AppException(404, "Data Motor tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data Motor",
            mapOf(Pair("motor", motor))
        )
        call.respond(response)
    }

    // Ambil data request
    private suspend fun getMotorRequest(call: ApplicationCall): MotorRequest {
        // Buat object penampung
        val motorReq = MotorRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                // Ambil request berupa teks
                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> motorReq.nama = part.value.trim()
                        "deskripsi" -> motorReq.deskripsi = part.value
                        "manfaat" -> motorReq.manfaat = part.value
                        "efekSamping" -> motorReq.efekSamping = part.value
                    }
                }

                // Upload file
                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/motors/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs() // pastikan folder ada

                    part.provider().copyAndClose(file.writeChannel())
                    motorReq.pathGambar = filePath
                }

                else -> {}
            }

            part.dispose()
        }

        return motorReq
    }

    // Validasi request data dari pengguna
    private fun validateMotorRequest(motorReq: MotorRequest){
        val validatorHelper = ValidatorHelper(motorReq.toMap())
        validatorHelper.required("nama", "Nama tidak boleh kosong")
        validatorHelper.required("deskripsi", "Deskripsi tidak boleh kosong")
        validatorHelper.required("manfaat", "Manfaat tidak boleh kosong")
        validatorHelper.required("efekSamping", "Efek Samping tidak boleh kosong")
        validatorHelper.required("pathGambar", "Gambar tidak boleh kosong")
        validatorHelper.validate()

        val file = File(motorReq.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar Motor gagal diupload!")
        }

    }

    // Menambahkan data Motor
    suspend fun createMotor(call: ApplicationCall) {
        // Ambil data request
        val motorReq = getMotorRequest(call)

        // Validasi request
        validateMotorRequest(motorReq)

        // periksa motor dengan nama yang sama
        val existMotor = motorRepository.getMotorByName(motorReq.nama)
        if(existMotor != null){
            val tmpFile = File(motorReq.pathGambar)
            if(tmpFile.exists()){
                tmpFile.delete()
            }
            throw AppException(409, "Motor dengan nama ini sudah terdaftar!")
        }

        val motorId = motorRepository.addMotor(
            motorReq.toEntity()
        )

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data Motor",
            mapOf(Pair("motorId", motorId))
        )
        call.respond(response)
    }

    // Mengubah data Motor
    suspend fun updateMotor(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID Motor tidak boleh kosong!")

        val oldMotor = motorRepository.getMotorById(id) ?: throw AppException(404, "Data Motor tidak tersedia!")

        // Ambil data request
        val motorReq = getMotorRequest(call)

        if(motorReq.pathGambar.isEmpty()){
            motorReq.pathGambar = oldMotor.pathGambar
        }

        // Validasi request
        validateMotorRequest(motorReq)

        // periksa motor dengan nama yang sama jika nama diubah
        if(motorReq.nama != oldMotor.nama){
            val existMotor = motorRepository.getMotorByName(motorReq.nama)
            if(existMotor != null){
                val tmpFile = File(motorReq.pathGambar)
                if(tmpFile.exists()){
                    tmpFile.delete()
                }
                throw AppException(409, "Motor dengan nama ini sudah terdaftar!")
            }
        }

        // Hapus gambar lama jika mengupload file baru
        if(motorReq.pathGambar != oldMotor.pathGambar){
            val oldFile = File(oldMotor.pathGambar)
            if(oldFile.exists()){
                oldFile.delete()
            }
        }

        val isUpdated = motorRepository.updateMotor(
            id, motorReq.toEntity()
        )
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data Motor!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah data Motor",
            null
        )
        call.respond(response)
    }

    // Menghapus data Motor
    suspend fun deleteMotor(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID Motor tidak boleh kosong!")

        val oldMotor = motorRepository.getMotorById(id) ?: throw AppException(404, "Data Motor tidak tersedia!")

        val oldFile = File(oldMotor.pathGambar)

        val isDeleted = motorRepository.removeMotor(id)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data Motor!")
        }

        // Hapus data gambar jika data Motor sudah dihapus
        if (oldFile.exists()) {
            oldFile.delete()
        }

        val response = DataResponse(
            "success",
            "Berhasil menghapus data Motor",
            null
        )
        call.respond(response)
    }

    // Mengambil gambar Motor
    suspend fun getMotorImage(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val motor = motorRepository.getMotorById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(motor.pathGambar)

        if (!file.exists()) {
            return call.respond(HttpStatusCode.NotFound)
        }

        call.respondFile(file)
    }
}