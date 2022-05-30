package guru.springframework.spring5webfluxrest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class CategoryControllerTest {
    private AutoCloseable closeable;

    WebTestClient webTestClient;
    @Mock
    CategoryRepository mockCategoryRepository;
    CategoryController categoryController;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        //mockCategoryRepository = Mockito.mock(CategoryRepository.class);
        categoryController = new CategoryController(mockCategoryRepository);
        webTestClient = WebTestClient.bindToController(categoryController).build();

    }

    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Test
    void list() {
        given(mockCategoryRepository.findAll())
                .willReturn(Flux.just(Category.builder().description("Cat1").build(),
                        Category.builder().description("Cat2").build()));

        webTestClient.get()
                .uri("/api/v1/categories/")
                .exchange()
                .expectBodyList(Category.class)
                .hasSize(2);
    }

    @Test
    void getById() {
        given(mockCategoryRepository.findById("someid"))
                .willReturn(Mono.just(Category.builder().description("Cat").build()));

        webTestClient.get()
                .uri("/api/v1/categories/someid")
                .exchange()
                .expectBody(Category.class);

    }

    @Test
    public void testCreateCateogry() {
        given(mockCategoryRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().description("descrp").build()));

        Mono<Category> catToSaveMono = Mono.just(Category.builder().description("Some Cat").build());

        Flux<Category> catToSaveFlux = Flux.just(Category.builder().description("Cat 1").build(),
                Category.builder().description("Cat 2").build()
                );

        webTestClient.post()
                .uri("/api/v1/categories")
                .body(catToSaveMono, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();

        webTestClient.post()
                .uri("/api/v1/categories")
                .body(catToSaveFlux, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();

    }

    @Test
    public void testUpdate() {
        given(mockCategoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> catToUpdateMono = Mono.just(Category.builder().description("Some Cat").build());

        webTestClient.put()
                .uri("/api/v1/categories/asdfasdf")
                .body(catToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testPatchWithChanges() throws JsonProcessingException {

        Category mockCategoryB4Save=Category.builder().description("old description").build();
        Category mockSavedCategory=Category.builder().description("new description").build();

        String mockCategoryB4SaveJsonString = ow.writeValueAsString(mockCategoryB4Save);
        String mockSavedCategoryJsonString = ow.writeValueAsString(mockSavedCategory);

        given(mockCategoryRepository.findById(any(String.class)))
                .willReturn(Mono.just(mockCategoryB4Save));

        given(mockCategoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(mockSavedCategory));

        Mono<Category> catToUpdateMono = Mono.just(Category.builder().description("some description").build());

        webTestClient.patch()
                .uri("/api/v1/categories/someID")
                .body(catToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody().json(mockSavedCategoryJsonString,true);
                //the B4 save Json String will fail
                //.expectBody().json(mockCategoryB4SaveJsonString,true);

        verify(mockCategoryRepository).save(any());
    }

    @Test
    public void testPatchWithNoChange() {

        given(mockCategoryRepository.findById(any(String.class)))
                .willReturn(Mono.just(Category.builder().id("someID").description("old description").build()));


        given(mockCategoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> catToUpdateMono = Mono.just(Category.builder().description("old description").build());

        webTestClient.patch()
                .uri("/api/v1/categories/someID")
                .body(catToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(mockCategoryRepository,never()).save(any());
    }


    @Test
    public void testPatchNotFound() throws JsonProcessingException {
        given(mockCategoryRepository.findById(anyString()))
                .willReturn(Mono.empty());

        Mono<Category> catToUpdateMono = Mono.just(Category.builder().build());

        webTestClient.patch()
                .uri("/api/v1/categories/asdfasdf")
                .body(catToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody().isEmpty();



        verify(mockCategoryRepository, never()).save(any());
    }

}