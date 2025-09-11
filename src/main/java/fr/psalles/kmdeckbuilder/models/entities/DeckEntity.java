package fr.psalles.kmdeckbuilder.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.psalles.kmdeckbuilder.models.entities.embedded.DeckIdentity;
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

    @EmbeddedId
    private DeckIdentity id;

    @Column
    private int minorVersion;

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
            orphanRemoval = true
//            mappedBy = "id.id.deckId" // a.b pour embeddedId
    )
    @JsonIgnore
    @JoinColumns({
            @JoinColumn(name = "deckId", referencedColumnName = "deckId"),
            @JoinColumn(name = "version", referencedColumnName = "version")
    })
    private List<CardAssociation> cards = new ArrayList<>();

    @Column
    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
//            mappedBy = "id.id.deckId" // a.b pour embeddedId
    )
    @JsonIgnore
    @JoinColumns({
            @JoinColumn(name = "deckId", referencedColumnName = "deckId"),
            @JoinColumn(name = "version", referencedColumnName = "version")
    })
    private List<TagAssociation> tags = new ArrayList<>();

    @Column
    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
//            mappedBy = "id.id.deckId" // a.b pour embeddedId
    )
    @JoinColumns({
            @JoinColumn(name = "deckId", referencedColumnName = "deckId"),
            @JoinColumn(name = "version", referencedColumnName = "version")
    })
    @JsonIgnore
    private List<DeckHighlight> highlights = new ArrayList<>();

    @Column
    private God god;

    @Column
    private LocalDateTime creationDate;

    @Column
    private int costAP;

    @Column
    private int costDust;

    @Column(columnDefinition = "integer default 0")
    private int favoriteCount;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column
    private String videoLink;


    @Override
    public String toString() {
        return "DeckEntity{" +
                "deckId=" + id.getDeckId() +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", god=" + god +
                '}';
    }
}
