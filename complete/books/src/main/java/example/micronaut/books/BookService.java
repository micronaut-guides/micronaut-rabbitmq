package example.micronaut.books;

import io.micronaut.context.annotation.Context;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Context
public class BookService {

    private static final List<Book> bookStore = new ArrayList<>();

    @PostConstruct
    void init() {
        bookStore.add(new Book("1491950358", "Building Microservices"));
        bookStore.add(new Book("1680502395", "Release It!"));
        bookStore.add(new Book("0321601912", "Continuous Delivery"));
    }

    public List<Book> listAll() {
        return bookStore;
    }

    public Optional<Book> findByIsbn(String isbn) {
        return bookStore.stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findFirst();
    }
}
