package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.models.TagDto;
import fr.psalles.kmdeckbuilder.models.entities.Identity;
import fr.psalles.kmdeckbuilder.models.entities.TagEntity;
import fr.psalles.kmdeckbuilder.models.enums.Language;
import fr.psalles.kmdeckbuilder.repositories.TagsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TagsService {

    @Autowired
    TagsRepository tagsRepository;

    public int createTagSet(TagDto newTag) {
        int availableId = tagsRepository.findMaxId() + 1;

        TagEntity tagEntity = new TagEntity();
        tagEntity.setIconId(newTag.getIconId());
        tagEntity.setDisabled(newTag.isDisabled());
        tagEntity.setName(newTag.getName());

        for (Language language : Language.values()) {
            Identity identity = new Identity(availableId, language);
            tagEntity.setTagIdentity(identity);
            tagsRepository.save(tagEntity);
        }

        return availableId;
    }

    public boolean updateDisable(String id) {
        TagEntity entity = tagsRepository.findById(id).get();
        entity.setDisabled(!entity.isDisabled());
        return tagsRepository.save(entity).isDisabled();
    }


    public List<TagDto> getTags(Boolean withDisabled) {
        return tagsRepository.findByDisabled(withDisabled)
                .stream().map(tagEntity ->
                        TagDto.builder().id(tagEntity.getTagIdentity().getId())
                                .language(tagEntity.getTagIdentity().getLanguage())
                                .name(tagEntity.getName())
                                .iconId(tagEntity.getIconId())
                                .build()).collect(Collectors.toList());
    }

}
