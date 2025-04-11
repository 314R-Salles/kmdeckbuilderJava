package fr.psalles.kmdeckbuilder.controllers;

import fr.psalles.kmdeckbuilder.models.Illustration;
import fr.psalles.kmdeckbuilder.models.News;
import fr.psalles.kmdeckbuilder.models.TagDto;
import fr.psalles.kmdeckbuilder.models.entities.projections.IllustrationView;
import fr.psalles.kmdeckbuilder.services.NewsService;
import fr.psalles.kmdeckbuilder.services.TagsService;
import fr.psalles.kmdeckbuilder.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
// tous ces endpoints sont déjà vérouillés derriere .requestMatchers("/api/admin/**").authenticated())
// on ajoute le preAuthorize pour restreindre les actions admin
@PreAuthorize("@principalPropertiesAccessor.isAdmin()")
public class AdminApiController {

    private final UserService userService;
    private final NewsService newsService;
    private final TagsService tagsService;


    @Autowired
    public AdminApiController(UserService userService,
                              NewsService newsService,
                              TagsService tagsService) {
        this.userService = userService;
        this.newsService = newsService;
        this.tagsService = tagsService;
    }


    @PostMapping("/news/disable/{id}")
//    @PreAuthorize("@principalPropertiesAccessor.isAdmin()")
    public boolean saveNews(@PathVariable String id) {
        return this.newsService.updateDisable(id);
    }

    // retourne toutes les news sans illu pour edition
    @GetMapping("/news")
//    @PreAuthorize("@principalPropertiesAccessor.isAdmin()")
    public List<News> getNews() {
        return newsService.getAllNews();
    }

    @PostMapping("/news")
//    @PreAuthorize("@principalPropertiesAccessor.isAdmin()")
    public void saveNews(@RequestBody News news) {
        this.newsService.saveNews(news);
    }

    @PostMapping("/illustrations")
//    @PreAuthorize("@principalPropertiesAccessor.isAdmin()")
    public long saveIllustration(@RequestBody Illustration illustration) {
        return newsService.saveIllustration(illustration);
    }

    @GetMapping("/illustrations")
//    @PreAuthorize("@principalPropertiesAccessor.isAdmin()")
    public List<IllustrationView> getAll() {
        return newsService.getAllTitles();
    }

    @GetMapping("/illustrations/{id}")
//    @PreAuthorize("@principalPropertiesAccessor.isAdmin()")
    public String getAll(@PathVariable String id) {
        return newsService.getIllustration(id);
    }

    @GetMapping("/tags")
//    @PreAuthorize("@principalPropertiesAccessor.isAdmin()")
    public List<TagDto> getAllTags() {
        return tagsService.getTags(false);
    }

    @PostMapping("/tags/disable/{id}")
//    @PreAuthorize("@principalPropertiesAccessor.isAdmin()")
    public boolean updateTag(@PathVariable String id) {
        return this.tagsService.updateDisable(id);
    }

    @PostMapping("/tags")
//    @PreAuthorize("@principalPropertiesAccessor.isAdmin()")
    public int updateTag(@RequestBody TagDto tag) {
        return this.tagsService.createTagSet(tag);
    }

}
