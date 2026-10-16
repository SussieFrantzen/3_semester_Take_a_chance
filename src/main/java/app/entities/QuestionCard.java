package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "question_cards",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_question_card_fingerprint",
                        columnNames = "fingerprint"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class QuestionCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String subject;

    @Column(nullable = false, length = 1000)
    private String question;

    @Column(nullable = false, length = 100)
    private String groupName = "Questencard";

    @Column(nullable = false, length = 20)
    private String ageGroup;

    @Column(nullable = false, length = 1000)
    private String answer;

    @Column(nullable = false, length = 20)
    private String difficulty;

    @Column(nullable = false, length = 64)
    private String fingerprint;
}