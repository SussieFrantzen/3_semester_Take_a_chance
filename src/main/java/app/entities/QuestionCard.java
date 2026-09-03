package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question_cards")
@Getter
@Setter
@NoArgsConstructor
public class QuestionCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    @Column(length = 1000)
    private String question;

    private String groupName;

    private String ageGroup;

    @Column(length = 1000)
    private String answer;
}
