package app.dtos;

import java.util.List;

public record CreateGameRequest(
        Long gameBoardId,
        List<CreatePlayerRequest> players
) {

    public record CreatePlayerRequest(
            String name,
            int age
    ) {
    }
}