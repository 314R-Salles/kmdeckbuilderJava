package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.models.entities.IllustrationNameEntity;
import fr.psalles.kmdeckbuilder.repositories.CardIllustrationsRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CardIllustrationService {

    private final CardIllustrationsRepository cardIllustrationsRepository;

    public CardIllustrationService(CardIllustrationsRepository cardIllustrationsRepository) {
        this.cardIllustrationsRepository = cardIllustrationsRepository;
    }

    public List<IllustrationNameEntity> loadIllustrationsReferential() {
        return cardIllustrationsRepository.findAll();
    }


}
