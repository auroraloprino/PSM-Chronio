package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.BoardTagModel
import com.unibo.android.domain.repositories.BoardTagRepository

interface SaveBoardTagUseCase {
    suspend operator fun invoke(tag: BoardTagModel): Result<Long>
}

class SaveBoardTagUseCaseImpl(
    private val boardTagRepository: BoardTagRepository
) : SaveBoardTagUseCase {
    override suspend operator fun invoke(tag: BoardTagModel): Result<Long> {
        if (tag.name.isBlank()) return Result.failure(Exception("Il nome del tag non può essere vuoto"))
        return try {
            // save() farebbe un insert con onConflict=REPLACE: su un tag esistente cancella e
            // ricrea la riga, e il CASCADE sulle associazioni card-tag le svuoterebbe. Su una
            // modifica va usato update(), che fa un UPDATE SQL vero senza toccare le righe collegate.
            if (tag.id == 0L) {
                Result.success(boardTagRepository.save(tag))
            } else {
                boardTagRepository.update(tag)
                Result.success(tag.id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}