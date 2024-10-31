package fr.psalles.kmdeckbuilder.services;

import fr.psalles.kmdeckbuilder.models.Illustration;
import fr.psalles.kmdeckbuilder.models.News;
import fr.psalles.kmdeckbuilder.models.entities.NewsEntity;
import fr.psalles.kmdeckbuilder.models.entities.NewsIllustrationEntity;
import fr.psalles.kmdeckbuilder.models.entities.projections.IllustrationView;
import fr.psalles.kmdeckbuilder.repositories.IllustrationRepository;
import fr.psalles.kmdeckbuilder.repositories.NewsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NewsService {

    @Autowired
    IllustrationRepository illustrationRepository;
    @Autowired
    NewsRepository newsRepository;

    public long saveIllustration(Illustration illustration) {
        NewsIllustrationEntity entity = new NewsIllustrationEntity();
        entity.setIllustration(illustration.getB64());
        entity.setTitle(illustration.getTitle());

        return illustrationRepository.save(entity).getId();
    }

    public void saveNews(News news) {
        NewsEntity entity = new NewsEntity();
        entity.setContent(news.getContent());
        entity.setTitle(news.getTitle());
        entity.setIllustration(illustrationRepository.findById(news.getIllustrationId()).get());
        entity.setPublicationDate(LocalDateTime.now());
        entity.setUpdateDate(LocalDateTime.now());
        entity.setDisabled(false);
        newsRepository.save(entity);
    }

    public boolean updateDisable(String id) {
        NewsEntity entity = newsRepository.findById(id).get();
        entity.setDisabled(!entity.isDisabled());
        return newsRepository.save(entity).isDisabled();
    }

    // ne sert qu'à afficher les 3 dernières entetes sur la page d'accueil.
    public List<News> getLatestNews(int number) {
        return newsRepository.findByDisabledOrderByPublicationDateDesc(false, Limit.of(number)).stream().map(newsEntity ->
                News.builder().id(newsEntity.getId())
                        .illustration(newsEntity.getIllustration().getIllustration())
                        .title(newsEntity.getTitle())
                        .build()).collect(Collectors.toList());
    }

    // ne sert qu'à afficher les 3 dernières entetes sur la page d'accueil.
    public List<News> getLatestNewsIds(int number) {
        return newsRepository.findByDisabledOrderByPublicationDateDesc(false, Limit.of(number)).stream().map(newsEntity ->
                News.builder().id(newsEntity.getId())
                        .title(newsEntity.getTitle())
                        .build()).collect(Collectors.toList());
    }

    public News getNews(String id) {
        return newsRepository.findById(id).map(newsEntity ->
                News.builder().id(newsEntity.getId())
//                        .illustration(newsEntity.getIllustration().getIllustration())
                        .title(newsEntity.getTitle())
                        .content(newsEntity.getContent())
                        .build()).orElse(null);
    }


    public List<News> getAllNews() {
        return newsRepository.findAll().stream().map(newsEntity ->
                News.builder().id(newsEntity.getId())
                        .title(newsEntity.getTitle())
                        .disabled(newsEntity.isDisabled())
                        .content(newsEntity.getContent())
                        .build()).collect(Collectors.toList());
    }


    public List<IllustrationView> getAllTitles() {
        return illustrationRepository.findAllProjectedBy();
    }

    public String getIllustration(String id) {
        return illustrationRepository.getReferenceById(id).getIllustration();
    }

}
