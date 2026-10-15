package app.dtos;

public class UserDTO {

    private Integer id;
    private String username;

    public UserDTO(Integer id, String username) {
        this.id = id;
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}

