package app.dtos;

public record GameBoardDTO(
        Long id,
        String boardName,
        String subject,
        String boardLength,
        Long gameNameId,
        String gameName
) {
}