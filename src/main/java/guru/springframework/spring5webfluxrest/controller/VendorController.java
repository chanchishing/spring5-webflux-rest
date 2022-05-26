package guru.springframework.spring5webfluxrest.controller;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class VendorController {
    private final VendorRepository vendorRepository;

    public VendorController(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @GetMapping("/api/v1/vendors")
    Flux<Vendor> list(){
        return vendorRepository.findAll();
    }

    @GetMapping("/api/v1/vendors/{id}")
    Mono<Vendor> getById(@PathVariable String id){
        return vendorRepository.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/vendors")
    Mono<Void> create(@RequestBody Publisher<Vendor> vendorStream){
        return vendorRepository.saveAll(vendorStream).then();
    }

    @PutMapping("/api/v1/vendors/{id}")
    Mono<Vendor> update(@PathVariable String id, @RequestBody Vendor vendor) {
        vendor.setId(id);
        return vendorRepository.save(vendor);
    }

    @PatchMapping("/api/v1/vendors/{id}")
    Mono<Vendor> patch(@PathVariable String id, @RequestBody Vendor vendor, ServerHttpResponse response) {

        return vendorRepository.findById(id)
                .flatMap(vendorFound->{
                    boolean patched=false;
                    if (!vendorFound.getFirstname().equals(vendor.getFirstname())) {
                        vendorFound.setFirstname(vendor.getFirstname());
                        patched=true;
                    }
                    if ((vendor.getLastname()!==null) &&
                            ()
                            (!vendorFound.getLastname().equals(vendor.getLastname()))) {
                        vendorFound.setLastname(vendor.getLastname());
                        patched=true;
                    }

                    if (patched) {
                        return vendorRepository.save(vendorFound);
                    } else {
                        return Mono.just(vendorFound);
                    }
                })
                .switchIfEmpty(Mono.defer(() -> {
                    response.setStatusCode(HttpStatus.NOT_FOUND);
                    return Mono.empty();
                }));
    }



}
