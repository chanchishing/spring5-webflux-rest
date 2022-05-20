package guru.springframework.spring5webfluxrest.controller;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

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

    @Test
    void list() {
        BDDMockito.given(mockCategoryRepository.findAll())
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
        BDDMockito.given(mockCategoryRepository.findById("someid"))
                .willReturn(Mono.just(Category.builder().description("Cat").build()));

        webTestClient.get()
                .uri("/api/v1/categories/someid")
                .exchange()
                .expectBody(Category.class);

    }

    @Test
    public void testCreateCateogry() {
        BDDMockito.given(mockCategoryRepository.saveAll(any(Publisher.class)))
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
    public void TestUpdate() {
        BDDMockito.given(mockCategoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> catToUpdateMono = Mono.just(Category.builder().description("Some Cat").build());

        webTestClient.put()
                .uri("/api/v1/categories/asdfasdf")
                .body(catToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();
    }
}