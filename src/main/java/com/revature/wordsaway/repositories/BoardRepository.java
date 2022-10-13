package com.revature.wordsaway.repositories;

import com.revature.wordsaway.models.entities.Board;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Repository
public interface BoardRepository extends CrudRepository<Board, UUID> {
    @Query(value = "SELECT * FROM boards WHERE id = ?1", nativeQuery = true)
    Board findBoardByID(UUID id);

    @Query(value = "SELECT * FROM boards WHERE game_id = ?1", nativeQuery = true)
    List<Board> findBoardByGameID(UUID gameID);

    @Query(value = "SELECT * FROM boards WHERE game_id = ?2 AND id != ?1", nativeQuery = true)
    Board findOpposingBoardByIDAndGameID(UUID id, UUID gameID);

    @Query(value = "SELECT * FROM boards WHERE username = ?1", nativeQuery = true)
    List<Board> findAllBoardsByUsername(String username);

    @Query(value = "SELECT * FROM boards B1, boards B2 WHERE B1.game_id = B2.game_id AND B1.username = ?1 AND B2.username = ?2 AND B1.completed IS NULL AND B2.completed IS NULL", nativeQuery = true)
    List<Board> findUnfinishedBoardsByTwoUsernames(String username1, String username2);

    @Transactional
    @Modifying
    @Query(value = "UPDATE boards SET fireballs = ?2 , game_state = ?3, letters = ?4, tray = ?5, worms = ?6 WHERE id = ?1", nativeQuery = true)
    void updateBoard(UUID gameID, int fireballs, int gameState, char[] letters, char[] tray, char[] worms);

    //Delg v2
    @Query(value = "SELECT * FROM boards WHERE username = ?1 AND completed = null", nativeQuery = true)
    List<Board> findAllOngoingBoardsByUsername(String username);
}