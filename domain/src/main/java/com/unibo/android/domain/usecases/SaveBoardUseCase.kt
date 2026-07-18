package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.BoardModel
import com.unibo.android.domain.repositories.BoardRepository

interface SaveBoardUseCase {
    suspend operator fun invoke(board: BoardModel): Result<Long>
}

class SaveBoardUseCaseImpl(
    private val boardRepository: BoardRepository
) : SaveBoardUseCase {
    override suspend operator fun invoke(board: BoardModel): Result<Long> {
        if (board.title.isBlank()) return Result.failure(Exception("Title cannot be empty"))
        return try {
            // save() fa un insert con onConflict=REPLACE: su una board esistente cancella e
            // ricrea la riga, e il CASCADE delle colonne la svuoterebbe. Su una modifica va
            // usato update(), che fa un UPDATE SQL vero senza toccare le righe collegate.
            if (board.id == 0L) {
                Result.success(boardRepository.save(board))
            } else {
                boardRepository.update(board)
                Result.success(board.id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}