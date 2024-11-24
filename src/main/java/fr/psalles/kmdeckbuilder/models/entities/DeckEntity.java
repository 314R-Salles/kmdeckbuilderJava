package fr.psalles.kmdeckbuilder.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.psalles.kmdeckbuilder.models.enums.God;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "km_deck")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeckEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deckId;

    @Column
    private String name;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "userId")
    @JsonIgnore
    private UserEntity userId;

    @Column
    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "id.deckId" // a.b pour embeddedId
    )
    @JsonIgnore
    private List<CardAssociation> cards = new ArrayList<>();

    @Column
    private God god;

    @Column
    private LocalDateTime creationDate;

    @Column
    private int costAP;

    @Column
    private int costDust;

    @Override
    public String toString() {
        return "DeckEntity{" +
                "deckId=" + deckId +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", god=" + god +
                '}';
    }
}
