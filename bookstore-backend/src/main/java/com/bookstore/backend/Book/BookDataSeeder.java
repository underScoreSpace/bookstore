package com.bookstore.backend.Book;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class BookDataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final Random random = new Random();

    public BookDataSeeder(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (bookRepository.count() > 0) {
            System.out.println("Database already populated. Skipping data seeding.");
            return;
        }

        System.out.println("Database empty. Seeding initial data...");

        List<Book> booksToSeed = new ArrayList<>();
        List<BookInfo> books = getBookList();

        for (BookInfo info : books) {
            double priceDouble = 15.00 + (35.00 * random.nextDouble());
            BigDecimal price = BigDecimal.valueOf(priceDouble).setScale(2, RoundingMode.HALF_UP);
            int stockQty = random.nextInt(50) + 1;

            Book book = new Book(
                    info.title(),
                    info.author(),
                    info.description(),
                    price,
                    stockQty
            );

            booksToSeed.add(book);
        }

        bookRepository.saveAll(booksToSeed);
        System.out.println("Successfully seeded " + bookRepository.count() + " books.");
    }


    private record BookInfo(String title, String author, String description) {}


    private List<BookInfo> getBookList() {
        return List.of(
                new BookInfo("Clean Architecture", "Robert C. Martin", "A timeless guide on designing maintainable and scalable software systems using layered architecture."),
                new BookInfo("Domain-Driven Design", "Eric Evans", "Defines a methodology for tackling complex software projects by aligning code with real-world business concepts."),
                new BookInfo("Patterns of Enterprise Application Architecture", "Martin Fowler", "Covers proven patterns for building robust, large-scale enterprise applications."),
                new BookInfo("Designing Data-Intensive Applications", "Martin Kleppmann", "Explains principles for reliable, scalable, and maintainable data systems in modern applications."),
                new BookInfo("Software Architecture in Practice", "Len Bass", "A practical guide that teaches how to think about and design software architectures effectively."),
                new BookInfo("Building Microservices", "Sam Newman", "Teaches how to design, build, and deploy microservices architectures that scale."),
                new BookInfo("Fundamentals of Software Architecture", "Mark Richards and Neal Ford", "A detailed look at architectural patterns, trade-offs, and decision-making strategies."),
                new BookInfo("The Pragmatic Programmer", "Andrew Hunt and David Thomas", "A handbook for practical programming, teamwork, and craftsmanship."),
                new BookInfo("Clean Code", "Robert C. Martin", "A must-read that shows how to write clean, maintainable, and efficient code."),
                new BookInfo("Refactoring", "Martin Fowler", "Explains how to improve code design without changing its behavior."),
                new BookInfo("Continuous Delivery", "Jez Humble and David Farley", "Shows how to automate and deliver high-quality software at speed."),
                new BookInfo("Site Reliability Engineering", "Niall Richard Murphy", "Google’s principles for building and maintaining reliable systems at scale."),
                new BookInfo("Accelerate", "Nicole Forsgren, Jez Humble, and Gene Kim", "Data-backed insights into what makes elite software delivery teams succeed."),
                new BookInfo("Enterprise Integration Patterns", "Gregor Hohpe and Bobby Woolf", "Classic guide to integrating enterprise applications and systems using messaging."),
                new BookInfo("The Art of Computer Programming, Vol. 1", "Donald Knuth", "The definitive reference on algorithms and computer science theory."),
                new BookInfo("Structure and Interpretation of Computer Programs", "Harold Abelson", "An influential introduction to computer science fundamentals using Scheme."),
                new BookInfo("Compilers: Principles, Techniques, and Tools", "Alfred V. Aho", "The classic ‘Dragon Book’ on compiler theory and implementation."),
                new BookInfo("Design Patterns", "Erich Gamma et al.", "Introduces 23 reusable design patterns for object-oriented software development."),
                new BookInfo("Introduction to Algorithms", "Thomas H. Cormen", "Comprehensive guide to data structures and algorithms used in modern computing."),
                new BookInfo("The Mythical Man-Month", "Frederick Brooks Jr.", "Insights into software engineering management and project scheduling challenges."),
                new BookInfo("Code Complete", "Steve McConnell", "A practical guide to writing robust, efficient, and maintainable software."),
                new BookInfo("Working Effectively with Legacy Code", "Michael Feathers", "Teaches strategies to safely refactor and modernize old, untested codebases."),
                new BookInfo("A Philosophy of Software Design", "John Ousterhout", "Explains design principles to manage complexity and improve system quality."),
                new BookInfo("Continuous Integration", "Paul Duvall", "Outlines CI techniques to maintain code quality during rapid development cycles."),
                new BookInfo("The Art of Scalability", "Martin L. Abbott and Michael T. Fisher", "Explores how to scale technology, processes, and organizations effectively."),
                new BookInfo("Docker Deep Dive", "Nigel Poulton", "Comprehensive guide to mastering Docker and containerization concepts."),
                new BookInfo("Kubernetes: Up and Running", "Brendan Burns", "Explains how to deploy, scale, and manage containerized applications using Kubernetes."),
                new BookInfo("Hands-On Machine Learning with Scikit-Learn", "Aurélien Géron", "Practical hands-on introduction to modern machine learning techniques in Python."),
                new BookInfo("Deep Learning with Python", "François Chollet", "A clear and approachable guide to deep learning concepts using Keras."),
                new BookInfo("Programming Rust", "Jim Blandy and Jason Orendorff", "A deep dive into Rust for building fast, safe, and concurrent systems."),
                new BookInfo("Go Programming Blueprints", "Mat Ryer", "Practical projects and design examples written in the Go programming language."),
                new BookInfo("Learning SQL", "Alan Beaulieu", "Teaches SQL fundamentals and how to effectively query relational databases."),
                new BookInfo("The Manga Guide to Databases", "Kenichi Kadowaki", "An illustrated introduction to databases and how they work."),
                new BookInfo("Data Structures and Algorithms Made Easy", "Narasimha Karumanchi", "Simplifies core data structure and algorithm concepts for interviews and exams."),
                new BookInfo("Cracking the Coding Interview", "Gayle Laakmann McDowell", "The go-to guide for coding interview prep with over 180 solved problems.")
        );
    }
}
