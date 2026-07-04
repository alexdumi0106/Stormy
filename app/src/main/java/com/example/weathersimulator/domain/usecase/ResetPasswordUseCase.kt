package com.example.weathersimulator.domain.usecase

import com.example.weathersimulator.data.repository.UserRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> =
        repository.resetPassword(email)
}
