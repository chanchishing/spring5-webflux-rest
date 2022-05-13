package guru.springframework.spring5webfluxrest.repositories;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface Category extends ReactiveMongoRepository<Category,String> {
}
