package com.esporteclubepelotas.api.modules.news.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.modules.news.dto.*;
import com.esporteclubepelotas.api.modules.news.entity.*;
import com.esporteclubepelotas.api.modules.news.helper.*;
import com.esporteclubepelotas.api.modules.news.repository.NewsArticleRepository;
import com.esporteclubepelotas.api.modules.news.repository.NewsCategoryRepository;
import com.esporteclubepelotas.api.modules.news.repository.NewsCommentRepository;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsAdminService {

    private final NewsArticleRepository articleRepository;
    private final NewsCategoryRepository categoryRepository;
    private final NewsCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    @Transactional(readOnly = true)
    public GetNewsResponseDto adminFindAll(QueryNewsDto query) {
        int page = query.getPage() != null && query.getPage() > 0 ? query.getPage() : 1;
        int limit = query.getLimit() != null && query.getLimit() > 0 ? Math.min(50, query.getLimit()) : 10;

        Specification<NewsArticle> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("isActive")));

            if (query.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), query.getStatus()));
            }

            if (query.getIsFeatured() != null) {
                predicates.add(cb.equal(root.get("isFeatured"), query.getIsFeatured()));
            }

            if (query.getFeaturedSlot() != null) {
                predicates.add(cb.equal(root.get("featuredSlot"), query.getFeaturedSlot()));
            }

            if (query.getIsPinned() != null) {
                predicates.add(cb.equal(root.get("isPinned"), query.getIsPinned()));
            }

            if (query.getShowOnHomepage() != null) {
                predicates.add(cb.equal(root.get("showOnHomepage"), query.getShowOnHomepage()));
            }

            if (query.getIsYouth() != null) {
                predicates.add(cb.equal(root.get("isYouth"), query.getIsYouth()));
            }

            if (query.getCategory() != null && !query.getCategory().isBlank()) {
                String catParam = query.getCategory().trim().toLowerCase();
                try {
                    UUID catUuid = UUID.fromString(catParam);
                    predicates.add(cb.equal(root.get("category").get("id"), catUuid));
                } catch (IllegalArgumentException ex) {
                    predicates.add(cb.equal(cb.lower(root.get("category").get("slug")), catParam));
                }
            }

            if (query.getSearch() != null && !query.getSearch().isBlank()) {
                String pattern = "%" + query.getSearch().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("subtitle")), pattern),
                        cb.like(cb.lower(root.get("summary")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(
                Sort.Order.desc("isPinned"),
                Sort.Order.desc("updatedAt"),
                Sort.Order.desc("createdAt")
        );

        Page<NewsArticle> paged = articleRepository.findAll(spec, PageRequest.of(page - 1, limit, sort));

        List<NewsArticleDto> dtos = paged.getContent().stream()
                .map(NewsMapperHelper::mapArticle)
                .toList();

        return GetNewsResponseDto.builder()
                .data(dtos)
                .total(paged.getTotalElements())
                .page(page)
                .limit(limit)
                .totalPages(paged.getTotalPages() > 0 ? paged.getTotalPages() : 1)
                .build();
    }

    @Transactional(readOnly = true)
    public NewsArticleDto adminFindById(UUID id) {
        NewsArticle article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Matéria não encontrada."));
        return NewsMapperHelper.mapArticle(article);
    }

    @Transactional
    public NewsArticleDto create(CreateNewsDto dto, String authorId) {
        if (authorId == null || authorId.isBlank()) {
            throw new BusinessException("Autor não identificado.");
        }
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new BusinessException("Autor não encontrado."));

        NewsCategory category = resolveCategory(dto.getCategory());

        String slug = NewsSlugHelper.generateUniqueArticleSlug(dto.getTitle(), articleRepository, null);

        Instant publishedAt = null;
        if (dto.getStatus() == NewsStatus.PUBLISHED) {
            publishedAt = dto.getPublishedAt() != null ? Instant.parse(dto.getPublishedAt()) : Instant.now();
        } else if (dto.getPublishedAt() != null) {
            publishedAt = Instant.parse(dto.getPublishedAt());
        }

        List<String> rawTags = dto.getTags() != null ? dto.getTags() : Collections.emptyList();
        List<String> cleanTags = rawTags.stream()
                .filter(t -> t != null && !t.isBlank())
                .map(t -> t.trim().startsWith("#") ? t.trim() : "#" + t.trim())
                .toList();

        Map<String, Object> content = NewsContentProcessorHelper.processContentBase64Images(dto.getContent(), storageService);

        NewsFeaturedSlot slot = dto.getFeaturedSlot() != null ? dto.getFeaturedSlot() : NewsFeaturedSlot.NONE;
        boolean isFeatured = Boolean.TRUE.equals(dto.getIsFeatured());

        if (slot == NewsFeaturedSlot.MAIN) {
            isFeatured = true;
            articleRepository.clearAllMainSlots();
        } else if (slot == NewsFeaturedSlot.SECONDARY) {
            isFeatured = true;
        } else if (isFeatured) {
            slot = NewsFeaturedSlot.MAIN;
            articleRepository.clearAllMainSlots();
        }

        NewsArticle article = NewsArticle.builder()
                .title(dto.getTitle().trim())
                .subtitle(dto.getSubtitle() != null ? dto.getSubtitle().trim() : null)
                .slug(slug)
                .previousSlugs(new ArrayList<>())
                .coverImage(dto.getCoverImage() != null ? dto.getCoverImage() : "")
                .coverCaption(dto.getCoverCaption() != null ? dto.getCoverCaption().trim() : null)
                .content(content)
                .summary(dto.getSummary() != null ? dto.getSummary().trim() : null)
                .category(category)
                .tags(cleanTags)
                .status(dto.getStatus() != null ? dto.getStatus() : NewsStatus.DRAFT)
                .isFeatured(isFeatured)
                .featuredSlot(slot)
                .isPinned(Boolean.TRUE.equals(dto.getIsPinned()))
                .showOnHomepage(!Boolean.FALSE.equals(dto.getShowOnHomepage()))
                .isYouth(Boolean.TRUE.equals(dto.getIsYouth()))
                .reporterName(dto.getReporterName() != null ? dto.getReporterName().trim() : null)
                .photographerName(dto.getPhotographerName() != null ? dto.getPhotographerName().trim() : null)
                .author(author)
                .publishedAt(publishedAt)
                .gallery(dto.getGallery() != null ? dto.getGallery() : new ArrayList<>())
                .youtubeVideoUrl(dto.getYoutubeVideoUrl() != null ? dto.getYoutubeVideoUrl().trim() : null)
                .viewsCount(0L)
                .allowReactions(!Boolean.FALSE.equals(dto.getAllowReactions()))
                .allowComments(!Boolean.FALSE.equals(dto.getAllowComments()))
                .reactions(new NewsReactionsCounter())
                .isActive(true)
                .build();

        NewsArticle saved = articleRepository.save(article);
        return NewsMapperHelper.mapArticle(saved);
    }

    @Transactional
    public NewsArticleDto update(UUID id, UpdateNewsDto dto, String userId) {
        NewsArticle article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Matéria não encontrada."));

        User user = (userId != null && !userId.isBlank()) ? userRepository.findById(userId).orElse(null) : null;
        if (user != null) {
            article.setLastEditedBy(user);
        }

        if (dto.getCategory() != null) {
            article.setCategory(resolveCategory(dto.getCategory()));
        }

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            article.setTitle(dto.getTitle().trim());
        }

        if (dto.getSubtitle() != null) {
            article.setSubtitle(dto.getSubtitle().trim());
        }

        if (dto.getCoverImage() != null) {
            article.setCoverImage(dto.getCoverImage());
        }

        if (dto.getCoverCaption() != null) {
            article.setCoverCaption(dto.getCoverCaption().trim());
        }

        if (dto.getSummary() != null) {
            article.setSummary(dto.getSummary().trim());
        }

        if (dto.getContent() != null) {
            article.setContent(NewsContentProcessorHelper.processContentBase64Images(dto.getContent(), storageService));
        }

        if (dto.getTags() != null) {
            List<String> cleanTags = dto.getTags().stream()
                    .filter(t -> t != null && !t.isBlank())
                    .map(t -> t.trim().startsWith("#") ? t.trim() : "#" + t.trim())
                    .toList();
            article.setTags(cleanTags);
        }

        if (dto.getStatus() != null) {
            article.setStatus(dto.getStatus());
        }

        if (dto.getPublishedAt() != null) {
            article.setPublishedAt(Instant.parse(dto.getPublishedAt()));
        } else if (dto.getStatus() == NewsStatus.PUBLISHED && article.getPublishedAt() == null) {
            article.setPublishedAt(Instant.now());
        }

        if (dto.getIsPinned() != null) {
            article.setPinned(dto.getIsPinned());
        }

        if (dto.getShowOnHomepage() != null) {
            article.setShowOnHomepage(dto.getShowOnHomepage());
        }

        if (dto.getIsYouth() != null) {
            article.setYouth(dto.getIsYouth());
        }

        if (dto.getReporterName() != null) {
            article.setReporterName(dto.getReporterName().trim());
        }

        if (dto.getPhotographerName() != null) {
            article.setPhotographerName(dto.getPhotographerName().trim());
        }

        if (dto.getGallery() != null) {
            article.setGallery(dto.getGallery());
        }

        if (dto.getYoutubeVideoUrl() != null) {
            article.setYoutubeVideoUrl(dto.getYoutubeVideoUrl().trim());
        }

        if (dto.getAllowReactions() != null) {
            article.setAllowReactions(dto.getAllowReactions());
        }

        if (dto.getAllowComments() != null) {
            article.setAllowComments(dto.getAllowComments());
        }

        // Slug management
        if (dto.getSlug() != null && !dto.getSlug().trim().equalsIgnoreCase(article.getSlug())) {
            String newSlug = NewsSlugHelper.generateUniqueArticleSlug(dto.getSlug(), articleRepository, article.getId());
            if (!article.getPreviousSlugs().contains(article.getSlug())) {
                article.getPreviousSlugs().add(article.getSlug());
            }
            article.setSlug(newSlug);
        } else if (article.getStatus() == NewsStatus.DRAFT && dto.getTitle() != null && dto.getSlug() == null) {
            String regenerated = NewsSlugHelper.generateUniqueArticleSlug(dto.getTitle(), articleRepository, article.getId());
            article.setSlug(regenerated);
        }

        // Slot management
        if (dto.getFeaturedSlot() != null) {
            if (dto.getFeaturedSlot() == NewsFeaturedSlot.MAIN) {
                article.setFeatured(true);
                article.setFeaturedSlot(NewsFeaturedSlot.MAIN);
                articleRepository.clearOtherMainSlots(article.getId());
            } else if (dto.getFeaturedSlot() == NewsFeaturedSlot.SECONDARY) {
                article.setFeatured(true);
                article.setFeaturedSlot(NewsFeaturedSlot.SECONDARY);
            } else {
                article.setFeatured(false);
                article.setFeaturedSlot(NewsFeaturedSlot.NONE);
            }
        } else if (dto.getIsFeatured() != null) {
            article.setFeatured(dto.getIsFeatured());
            if (dto.getIsFeatured() && article.getFeaturedSlot() == NewsFeaturedSlot.NONE) {
                article.setFeaturedSlot(NewsFeaturedSlot.MAIN);
                articleRepository.clearOtherMainSlots(article.getId());
            } else if (!dto.getIsFeatured()) {
                article.setFeaturedSlot(NewsFeaturedSlot.NONE);
            }
        }

        NewsArticle saved = articleRepository.save(article);
        return NewsMapperHelper.mapArticle(saved);
    }

    @Transactional
    public void softDelete(UUID id) {
        NewsArticle article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Matéria não encontrada."));
        article.setActive(false);
        articleRepository.save(article);
    }

    @Transactional
    public Map<String, String> uploadCover(UUID id, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo enviado.");
        }
        NewsArticle article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Matéria não encontrada."));

        String ext = getFileExtension(file.getOriginalFilename());
        String fileName = "cover-" + System.currentTimeMillis() + "." + ext;
        String folder = "news/" + id;

        String url = storageService.uploadFile(file, folder, fileName);
        article.setCoverImage(url);
        articleRepository.save(article);

        return Map.of("url", url);
    }

    @Transactional
    public Map<String, List<String>> uploadGallery(UUID id, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException("Nenhum arquivo enviado.");
        }
        NewsArticle article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Matéria não encontrada."));

        List<String> urls = new ArrayList<>();
        String folder = "news/" + id + "/gallery";

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            String ext = getFileExtension(file.getOriginalFilename());
            String fileName = "photo-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 100000) + "." + ext;
            String url = storageService.uploadFile(file, folder, fileName);
            urls.add(url);
        }

        if (article.getGallery() == null) {
            article.setGallery(new ArrayList<>());
        }
        article.getGallery().addAll(urls);
        articleRepository.save(article);

        return Map.of("urls", urls);
    }

    public Map<String, String> uploadContentImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo enviado.");
        }
        String ext = getFileExtension(file.getOriginalFilename());
        String fileName = "content-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 100000) + "." + ext;
        String folder = "news/content";

        String url = storageService.uploadFile(file, folder, fileName);
        return Map.of("url", url);
    }

    @Transactional
    public NewsCategoryDto createCategory(CreateCategoryDto dto) {
        String baseSlug = NewsSlugHelper.slugify(dto.getName());
        String slug = baseSlug;
        int counter = 1;
        while (categoryRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }

        NewsCategory category = NewsCategory.builder()
                .name(dto.getName().trim())
                .slug(slug)
                .description(dto.getDescription() != null ? dto.getDescription().trim() : null)
                .isActive(true)
                .displayOrder(0)
                .build();

        NewsCategory saved = categoryRepository.save(category);
        return NewsMapperHelper.mapCategory(saved);
    }

    @Transactional(readOnly = true)
    public List<NewsCategoryDto> adminFindAllCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAscNameAsc().stream()
                .map(NewsMapperHelper::mapCategory)
                .toList();
    }

    @Transactional
    public NewsArticleDto duplicate(UUID id, String userId) {
        NewsArticle original = articleRepository.findById(id)
                .filter(NewsArticle::isActive)
                .orElseThrow(() -> new BusinessException("Matéria original não encontrada."));

        if (userId == null || userId.isBlank()) {
            throw new BusinessException("Usuário não identificado.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));

        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        String newSlug = original.getSlug() + "-copia-" + timestamp;
        String newTitle = original.getTitle() + " (Cópia)";

        NewsArticle duplicated = NewsArticle.builder()
                .title(newTitle)
                .subtitle(original.getSubtitle())
                .slug(newSlug)
                .previousSlugs(new ArrayList<>())
                .coverImage(original.getCoverImage())
                .coverCaption(original.getCoverCaption())
                .content(original.getContent() != null ? new HashMap<>(original.getContent()) : new HashMap<>())
                .summary(original.getSummary())
                .category(original.getCategory())
                .tags(original.getTags() != null ? new ArrayList<>(original.getTags()) : new ArrayList<>())
                .status(NewsStatus.DRAFT)
                .isFeatured(false)
                .featuredSlot(NewsFeaturedSlot.NONE)
                .isPinned(false)
                .reporterName(original.getReporterName())
                .photographerName(original.getPhotographerName())
                .author(user)
                .lastEditedBy(user)
                .publishedAt(null)
                .gallery(original.getGallery() != null ? new ArrayList<>(original.getGallery()) : new ArrayList<>())
                .youtubeVideoUrl(original.getYoutubeVideoUrl())
                .viewsCount(0L)
                .allowReactions(original.isAllowReactions())
                .allowComments(original.isAllowComments())
                .reactions(new NewsReactionsCounter())
                .isActive(true)
                .build();

        NewsArticle saved = articleRepository.save(duplicated);
        return NewsMapperHelper.mapArticle(saved);
    }

    // Comentários Admin
    @Transactional(readOnly = true)
    public GetNewsCommentsResponseDto adminFindComments(Integer page, Integer limit, NewsCommentStatus status, String search, String articleId) {
        return NewsCommentsAdminHelper.adminFindComments(commentRepository, page, limit, status, search, articleId);
    }

    @Transactional
    public NewsCommentDto adminUpdateCommentStatus(UUID commentId, NewsCommentStatus status) {
        return NewsCommentsAdminHelper.adminUpdateCommentStatus(commentRepository, commentId, status);
    }

    @Transactional
    public void adminDeleteComment(UUID commentId) {
        NewsCommentsAdminHelper.adminDeleteComment(commentRepository, commentId);
    }

    // Vitrine (Showcase)
    @Transactional(readOnly = true)
    public ShowcaseResponseDto getShowcase() {
        return NewsShowcaseHelper.getShowcase(articleRepository);
    }

    @Transactional
    public void updateShowcase(UpdateShowcaseDto dto) {
        NewsShowcaseHelper.updateShowcase(articleRepository, dto);
    }

    @Transactional(readOnly = true)
    public NewsArticleDto getCurrentMain() {
        return NewsShowcaseHelper.getCurrentMain(articleRepository);
    }

    // Matérias Agendadas
    @Transactional(readOnly = true)
    public Map<String, Object> getScheduledArticles() {
        return NewsScheduledHelper.getScheduledArticles(articleRepository);
    }

    @Transactional
    public NewsArticleDto publishNow(UUID id) {
        return NewsScheduledHelper.publishNow(articleRepository, id);
    }

    private NewsCategory resolveCategory(String categoryParam) {
        if (categoryParam == null || categoryParam.isBlank()) {
            throw new BusinessException("A categoria da matéria é obrigatória.");
        }
        try {
            UUID catUuid = UUID.fromString(categoryParam.trim());
            return categoryRepository.findById(catUuid)
                    .orElseThrow(() -> new BusinessException("Categoria não encontrada pelo ID: " + categoryParam));
        } catch (IllegalArgumentException ex) {
            return categoryRepository.findBySlug(categoryParam.trim().toLowerCase())
                    .orElseThrow(() -> new BusinessException("Categoria não encontrada pelo slug: " + categoryParam));
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
