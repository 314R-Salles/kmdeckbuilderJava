package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.models.DeckForm;
import fr.psalles.kmdeckbuilder.models.entities.AssociationIdentity;
import fr.psalles.kmdeckbuilder.models.entities.CardAssociation;
import fr.psalles.kmdeckbuilder.models.entities.DeckEntity;
import fr.psalles.kmdeckbuilder.models.entities.UserEntity;
import fr.psalles.kmdeckbuilder.models.enums.CardRarity;
import fr.psalles.kmdeckbuilder.repositories.DeckRepository;
import fr.psalles.kmdeckbuilder.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DeckService {

    @Autowired
    CardService cardService;
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    UserRepository userRepository;

    public Long saveDeck(DeckForm deck) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findById(userId).get(); // on est obligé d'avoir une valeur ici

        DeckEntity entity = new DeckEntity();

        entity.setName(deck.getName());
        entity.setGod(deck.getGod());
        entity.setUserId(user);
        entity.setCreationDate(LocalDateTime.now());

        DeckEntity savedDeck = deckRepository.save(entity);

        Map<CardRarity, Integer> dustMap= new HashMap<>();

        int costAP = deck.getCards().stream().map(DeckForm.Card::getCostAP).reduce(0, Integer::sum);
//        int costDust = deck.getCards().stream().map(DeckForm.Card::getCostAP).reduce(0,


        List<CardAssociation> associations = deck.getCards().stream().map(card -> {
            CardAssociation cardAssociation = new CardAssociation();
            cardAssociation.setId(new AssociationIdentity(savedDeck.getDeckId(), card.getId()));
            cardAssociation.setNbrExemplaires(card.getCount());
            cardAssociation.setHighlightOrder(card.getHightlight());
            return cardAssociation;
        }).toList();

        savedDeck.getCards().addAll(associations);

        return deckRepository.save(savedDeck).getDeckId();

    }
//
//    public void saveNews(News news) {
//        NewsEntity entity = new NewsEntity();
//        entity.setContent(news.getContent());
//        entity.setTitle(news.getTitle());
//        entity.setIllustration(illustrationRepository.findById(news.getIllustrationId()).get());
//        entity.setPublicationDate(LocalDateTime.now());
//        entity.setUpdateDate(LocalDateTime.now());
//        entity.setDisabled(false);
//        newsRepository.save(entity);
//    }
//
//    public boolean updateDisable(String id) {
//        NewsEntity entity = newsRepository.findById(id).get();
//        entity.setDisabled(!entity.isDisabled());
//        return newsRepository.save(entity).isDisabled();
//    }
//
//    // ne sert qu'à afficher les 3 dernières entetes sur la page d'accueil.
//    public List<News> getLatestNews(int number) {
//        return newsRepository.findByDisabledOrderByPublicationDateDesc(false, Limit.of(number)).stream().map(newsEntity ->
//                News.builder().id(newsEntity.getId())
//                        .illustration(newsEntity.getIllustration().getIllustration())
//                        .title(newsEntity.getTitle())
//                        .build()).collect(Collectors.toList());
//    }
//
//    // ne sert qu'à afficher les 3 dernières entetes sur la page d'accueil.
//    public List<News> getLatestNewsIds(int number) {
//        return newsRepository.findByDisabledOrderByPublicationDateDesc(false, Limit.of(number)).stream().map(newsEntity ->
//                News.builder().id(newsEntity.getId())
//                        .title(newsEntity.getTitle())
//                        .build()).collect(Collectors.toList());
//    }
//
//    public News getNews(String id) {
//        return newsRepository.findById(id).map(newsEntity ->
//                News.builder().id(newsEntity.getId())
////                        .illustration(newsEntity.getIllustration().getIllustration())
//                        .title(newsEntity.getTitle())
//                        .content(newsEntity.getContent())
//                        .build()).orElse(null);
//    }
//
//
//    public List<News> getAllNews() {
//        return newsRepository.findAll().stream().map(newsEntity ->
//                News.builder().id(newsEntity.getId())
//                        .title(newsEntity.getTitle())
//                        .disabled(newsEntity.isDisabled())
//                        .content(newsEntity.getContent())
//                        .build()).collect(Collectors.toList());
//    }
//
//
//    public List<IllustrationView> getAllTitles() {
//        return illustrationRepository.findAllProjectedBy();
//    }
//
//    public String getIllustration(String id) {
//        return illustrationRepository.getReferenceById(id).getIllustration();
//    }

}
