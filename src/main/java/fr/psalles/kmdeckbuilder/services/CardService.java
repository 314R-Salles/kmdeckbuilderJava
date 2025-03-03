package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.models.CardDto;
import fr.psalles.kmdeckbuilder.models.entities.CardEntity;
import fr.psalles.kmdeckbuilder.models.requests.CardSearchForm;
import fr.psalles.kmdeckbuilder.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.psalles.kmdeckbuilder.models.entities.specification.CardSpecification.*;


@Service
public class CardService {

    @Autowired
    CardRepository cardRepository;

    public Page<CardDto> fetchWithSpecs(CardSearchForm cardFilter) {
        Page<CardEntity> page = cardRepository.findAll(
                filterByCardType(cardFilter.getType())
                        .and(filterByHpGreaterThan(cardFilter.getHpGreaterThan()))
                        .and(filterByHpLessThan(cardFilter.getHpLessThan()))
                        .and(filterByApGreaterThan(cardFilter.getApGreaterThan()))
                        .and(filterByApLessThan(cardFilter.getApLessThan()))
                        .and(filterByMpGreaterThan(cardFilter.getMpGreaterThan()))
                        .and(filterByMpLessThan(cardFilter.getMpLessThan()))
                        .and(filterByAtGreaterThan(cardFilter.getAtGreaterThan()))
                        .and(filterByAtLessThan(cardFilter.getAtLessThan()))
                        .and(filterByGod(cardFilter.getGods()))
                        .and(filterByRarity(cardFilter.getRarity()))
                        .and(filterByLanguage(cardFilter.getLanguage()))
                        .and(filterByFamily(cardFilter.getFamily()))
                        .and(filterByNameLikeContent(cardFilter.getName())
                                .or(filterByDescriptionLikeContent(cardFilter.getDescription())))
                , PageRequest.of(cardFilter.getPageNumber(), cardFilter.getPageSize(), Sort.Direction.ASC, "costAP", "name", "cardFilePath"));
        return page.map(entity -> CardDto.builder()
                .id(entity.getCardIdentity().getId())
                .cardType(entity.getCardType())
                .rarity(entity.getRarity())
                .costAP(entity.getCostAP())
                .godType(entity.getGodType())
                .name(entity.getName())
                .cardFilePath(entity.getCardFilePath())
                .miniFilePath(entity.getMiniFilePath())
                .infiniteName(entity.getInfiniteName())
                .life(entity.getLife())
                .attack(entity.getAttack())
                .movementPoint(entity.getMovementPoint())
                .build());
    }

    public Page<CardDto> fetchByName(CardSearchForm cardFilter) {
        Page<CardEntity> page = cardRepository.findAll(
                filterByNameLikeContent(cardFilter.getName())
                        .and(filterByGod(cardFilter.getGods()))
                , PageRequest.of(cardFilter.getPageNumber(), cardFilter.getPageSize(), Sort.Direction.ASC, "godType", "name", "cardFilePath"));
        return page.map(entity -> CardDto.builder()
                .id(entity.getCardIdentity().getId())
                .cardType(entity.getCardType())
                .rarity(entity.getRarity())
                .costAP(entity.getCostAP())
                .godType(entity.getGodType())
                .name(entity.getName())
                .infiniteLevel(getLevelFromPath(entity.getCardFilePath()))
                .cardFilePath(entity.getCardFilePath())
                .miniFilePath(entity.getMiniFilePath())
                .infiniteName(entity.getInfiniteName())
                .life(entity.getLife())
                .attack(entity.getAttack())
                .movementPoint(entity.getMovementPoint())
                .build());
    }


    private Integer getLevelFromPath(String path) {
        Pattern p = Pattern.compile("niveau_(\\d+)");
        Matcher m = p.matcher(path);
        boolean b = m.find();
        if (b) {
            // pour récupérer le 1/2/3 de la rareté après niveau_
            return Integer.parseInt(m.group(1));
        } else return null;
    }

}
