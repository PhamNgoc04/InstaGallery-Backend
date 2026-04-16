package com.instagallery.plugins

import com.instagallery.repositories.BookingRepository
import com.instagallery.repositories.InteractionRepository
import com.instagallery.repositories.PostRepository
import com.instagallery.repositories.SessionRepository
import com.instagallery.repositories.UserRepository
import com.instagallery.services.AuthService
import com.instagallery.services.BookingService
import com.instagallery.services.InteractionService
import com.instagallery.services.PostService
import com.instagallery.services.UserService
import com.instagallery.utils.JwtManager
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

val appModule = module {
    single { UserRepository() }
    single { SessionRepository() }
    single { com.instagallery.repositories.PasswordResetRepository() }
    single { JwtManager(get()) }
    single { AuthService() }
    single { UserService() }
    single { PostRepository() }
    single { PostService() }
    single { InteractionRepository() }
    single { InteractionService() }
    single { BookingRepository() }
    single { BookingService() }
    single { com.instagallery.repositories.ChatRepository() }
    single { com.instagallery.services.ChatService() }
    single { com.instagallery.repositories.NotificationRepository() }
    single { com.instagallery.services.NotificationService() }
    single { com.instagallery.repositories.SearchRepository() }
    single { com.instagallery.services.SearchService() }
    single { com.instagallery.repositories.PortfolioRepository() }
    single { com.instagallery.services.PortfolioService() }
    single { com.instagallery.repositories.RatingRepository() }
    single { com.instagallery.services.RatingService() }
    single { com.instagallery.repositories.MediaRepository() }
    single { com.instagallery.services.MediaService() }
    single { com.instagallery.repositories.ReportRepository() }
    single { com.instagallery.services.ReportService() }
    single { com.instagallery.repositories.AdminRepository() }
    single { com.instagallery.services.AdminService() }
}

fun Application.configureDependencyInjection() {
    install(Koin) {
        modules(appModule)
        
        // Pass application config to Koin so JwtManager can read secrets
        environment.config.let { config ->
            modules(module {
                single { config }
            })
        }
    }
}
