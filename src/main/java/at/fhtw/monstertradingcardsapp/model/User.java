package at.fhtw.monstertradingcardsapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
    private Integer id;
    @JsonAlias({"Username"})
    private String userName;
    @JsonAlias({"Password"})
    private String password;
    @JsonAlias({"coins"})
    private Integer coins;
    @JsonAlias({"stats"})
    private Integer stats;
    @JsonAlias({"Bio"})
    private String bio;
    @JsonAlias({"Image"})
    private String image;
}
