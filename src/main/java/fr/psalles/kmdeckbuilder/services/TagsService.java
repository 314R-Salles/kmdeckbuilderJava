package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.models.SimpleTagDto;
import fr.psalles.kmdeckbuilder.models.TagDto;
import fr.psalles.kmdeckbuilder.models.entities.embedded.Identity;
import fr.psalles.kmdeckbuilder.models.entities.TagEntity;
import fr.psalles.kmdeckbuilder.models.entities.projections.TagCount;
import fr.psalles.kmdeckbuilder.models.enums.Language;
import fr.psalles.kmdeckbuilder.repositories.TagsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TagsService {

    @Autowired
    TagsRepository tagsRepository;

    public void createTagSet(List<TagDto> tags) {
        for (TagDto tag : tags) {

            Integer availableId = tag.getId();

            if (tag.getId() == null) {
                availableId = tagsRepository.findMaxId() + 1;
            }

            TagEntity tagEntity = new TagEntity();
            tagEntity.setIconId(tag.getIconId());
            tagEntity.setDisabled(tag.isDisabled());

            for (Language language : Language.values()) {
                Identity identity = new Identity(availableId, language);
                tagEntity.setTagIdentity(identity);

                switch (language) {
                    case FR -> tagEntity.setName(tag.getFrTitle());
                    case EN -> tagEntity.setName(tag.getEnTitle());
                    case ES -> tagEntity.setName(tag.getEsTitle());
                    case RU -> tagEntity.setName(tag.getRuTitle());
                    case BR -> tagEntity.setName(tag.getBrTitle());
                }

                tagsRepository.save(tagEntity);

            }
        }
    }

    public List<TagDto> getTags() {
        List<TagEntity> flatTags = tagsRepository.findAll();

        Map<Integer, List<TagEntity>> map = flatTags.stream().collect(Collectors.groupingBy(a -> a.getTagIdentity().getId()));
        return map.values().stream().map(tagList -> {
                    TagDto.TagDtoBuilder builder = TagDto.builder().id(tagList.getFirst().getTagIdentity().getId())
                            .iconId(tagList.getFirst().getIconId()).disabled(tagList.getFirst().isDisabled());

                    tagList.forEach(tagEntity -> {
                        switch (tagEntity.getTagIdentity().getLanguage()) {
                            case FR -> builder.frTitle(tagEntity.getName());
                            case EN -> builder.enTitle(tagEntity.getName());
                            case ES -> builder.esTitle(tagEntity.getName());
                            case RU -> builder.ruTitle(tagEntity.getName());
                            case BR -> builder.brTitle(tagEntity.getName());
                        }
                    });
                    return builder.build();
                }
        ).collect(Collectors.toList());
    }

    public List<SimpleTagDto> getTagsByLanguage(Language language) {
        List<TagEntity> flatTags = tagsRepository.findByTagIdentityLanguageAndDisabled(language, false);
        List<TagCount> counts = loadTags();
        Map<Integer, Integer> mapCount = counts.stream().collect(Collectors.toMap(TagCount::getTagId, TagCount::getCount, (a, b) -> a));
        return flatTags.stream().map(tag -> {
                    SimpleTagDto.SimpleTagDtoBuilder builder = SimpleTagDto.builder().id(tag.getTagIdentity().getId())
                            .count(Objects.requireNonNullElse(mapCount.get(tag.getTagIdentity().getId()),0))
                            .iconId(tag.getIconId()).disabled(tag.isDisabled());
                    switch (language) {
                        case FR -> builder.title(tag.getName());
                        case EN -> builder.title(tag.getName());
                        case ES -> builder.title(tag.getName());
                        case RU -> builder.title(tag.getName());
                        case BR -> builder.title(tag.getName());
                    }
                    return builder.build();
                }
        ).collect(Collectors.toList());
    }

    public List<TagCount> loadTags() {
        return tagsRepository.countTags();
    }

}
