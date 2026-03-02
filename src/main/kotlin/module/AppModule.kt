package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.PlantRepository
import org.delcom.services.PlantService
import org.delcom.repositories.IMotorRepository
import org.delcom.repositories.MotorRepository
import org.delcom.services.MotorService
import org.delcom.services.ProfileService
import org.koin.dsl.module


val appModule = module {
    // Plant Repository
    single<IPlantRepository> {
        PlantRepository()
    }

    // Plant Service
    single {
        PlantService(get())
    }

    single<IMotorRepository> {
        MotorRepository()
    }

    // Plant Service
    single {
        MotorService(get())
    }

    // Profile Service
    single {
        ProfileService()
    }
}