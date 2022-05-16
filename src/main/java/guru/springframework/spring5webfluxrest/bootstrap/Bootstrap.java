package guru.springframework.spring5webfluxrest.bootstrap;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements CommandLineRunner {
    CategoryRepository categoryRepository;

    VendorRepository vendorRepository;

    public Bootstrap(CategoryRepository categoryRepository,
                     VendorRepository vendorRepository) {
        this.categoryRepository = categoryRepository;
        this.vendorRepository=vendorRepository;
    }

    private void initCategory(){
        if (categoryRepository.count().block()==0) {

            Category fruits = new Category();
            fruits.setDescription("Fruits");

            Category dried = new Category();
            dried.setDescription("Dried");

            Category fresh = new Category();
            fresh.setDescription("Fresh");

            Category exotic = new Category();
            exotic.setDescription("Exotic");

            Category nuts = new Category();
            nuts.setDescription("Nuts");

            categoryRepository.save(fruits).block();
            categoryRepository.save(dried).block();
            categoryRepository.save(fresh).block();
            categoryRepository.save(exotic).block();
            categoryRepository.save(nuts).block();
        }
        System.out.println("Category Loaded = " + categoryRepository.count().block());

    }

    private void initVendor(){
        if (vendorRepository.count().block()==0) {

            Vendor vendor1 = new Vendor();
            vendor1.setFirstname("Vendor");
            vendor1.setLastname("One");

            Vendor vendor2 = new Vendor();
            vendor2.setFirstname("Vendor");
            vendor2.setLastname("Two");

            Vendor vendor3 = new Vendor();
            vendor3.setFirstname("Vendor");
            vendor3.setLastname("Three");


            vendorRepository.save(vendor1).block();
            vendorRepository.save(vendor2).block();
            vendorRepository.save(vendor3).block();
        }
        System.out.println("Vendor Loaded = " + vendorRepository.count().block());
    }


    //private void initCustomer(){
    //    Customer jamesBond = new Customer();
    //    jamesBond.setFirstname("James");
    //    jamesBond.setLastname("Bond");
    //
    //    Customer jackRyan = new Customer();
    //    jackRyan.setFirstname("Jack");
    //    jackRyan.setLastname("Ryan");
    //
    //    Customer jasonBourne = new Customer();
    //    jasonBourne.setFirstname("Jason");
    //    jasonBourne.setLastname("Bourne");
    //
    //    Customer jackBauer = new Customer();
    //    jackBauer.setFirstname("Jack");
    //    jackBauer.setLastname("Bauer");
    //
    //
    //    customerRepository.save(jamesBond);
    //    customerRepository.save(jackRyan);
    //    customerRepository.save(jasonBourne);
    //    customerRepository.save(jackBauer);
    //
    //    System.out.println("Customer Loaded = " + customerRepository.count() );
    //
    //}


    @Override
    public void run(String... args) throws Exception {
        initCategory();
        initVendor();
    }
}
