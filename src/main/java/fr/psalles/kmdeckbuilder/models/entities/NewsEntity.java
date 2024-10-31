package fr.psalles.kmdeckbuilder.models.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "km_news")
public class NewsEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "illustration_id", referencedColumnName = "id")
    private NewsIllustrationEntity illustration;

    @Column
    private LocalDateTime publicationDate;
    @Column
    private LocalDateTime updateDate;

    @Column
    private boolean disabled;

}
