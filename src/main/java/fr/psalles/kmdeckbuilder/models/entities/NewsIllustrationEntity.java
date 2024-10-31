package fr.psalles.kmdeckbuilder.models.entities;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "km_news_illustration")
public class NewsIllustrationEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String illustration;

}
