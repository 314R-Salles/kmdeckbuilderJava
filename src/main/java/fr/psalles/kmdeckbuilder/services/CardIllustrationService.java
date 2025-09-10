package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.models.entities.IllustrationNameEntity;
import fr.psalles.kmdeckbuilder.repositories.CardIllustrationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CardIllustrationService {

    @Autowired
    CardIllustrationsRepository cardIllustrationsRepository;

    public List<IllustrationNameEntity> loadIllustrationsReferential() {
        return cardIllustrationsRepository.findAll();
    }


}
