package guru.springframework.spring5webfluxrest.controller;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
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

class VendorControllerTest {
    private AutoCloseable closeable;

    WebTestClient webTestClient;
    @Mock
    VendorRepository mockVendorRepository;
    VendorController vendorController;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        //mockVendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(mockVendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();

    }

    @Test
    void list() {
        BDDMockito.given(mockVendorRepository.findAll())
                .willReturn(Flux.just(Vendor.builder().firstname("first name 1").lastname("last name 1").build(),
                                      Vendor.builder().firstname("first name 2").lastname("last name 2").build()));

        webTestClient.get()
                .uri("/api/v1/vendors/")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    void getById() {
        BDDMockito.given(mockVendorRepository.findById("someid"))
                .willReturn(Mono.just(Vendor.builder().firstname("first name").lastname("last name").build()));

        webTestClient.get()
                .uri("/api/v1/vendors/someid")
                .exchange()
                .expectBody(Category.class);

    }


    @Test
    public void testCreateVendor() {
        BDDMockito.given(mockVendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().firstname("v firstname").lastname("v lastname").build()));

        Mono<Vendor> vendorToSaveMono = Mono.just(Vendor.builder().firstname("v firstname").lastname("v lastname").build());

        Flux<Vendor> vendorToSaveFlux = Flux.just(Vendor.builder().firstname("v1 firstname").lastname("v1 lastname").build(),
                                                  Vendor.builder().firstname("v2 firstname").lastname("v2 lastname").build()
        );

        webTestClient.post()
                .uri("/api/v1/vendors")
                .body(vendorToSaveMono, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();

        webTestClient.post()
                .uri("/api/v1/vendors")
                .body(vendorToSaveFlux, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();

    }

    @Test
    public void testUpdate() {
        BDDMockito.given(mockVendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToUpdateMono = Mono.just(Vendor.builder().firstname("v firstname").lastname("v lastname").build());

        webTestClient.put()
                .uri("/api/v1/vendors/asdfasdf")
                .body(vendorToUpdateMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

}