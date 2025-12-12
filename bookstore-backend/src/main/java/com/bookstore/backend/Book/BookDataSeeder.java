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
                    stockQty,
                    info.isbn()
            );

            booksToSeed.add(book);
        }

        bookRepository.saveAll(booksToSeed);
        System.out.println("Successfully seeded " + bookRepository.count() + " books.");
    }


    private record BookInfo(String title, String author, String description, String isbn) {}


    private List<BookInfo> getBookList() {
        return List.of(
                new BookInfo("Clean Architecture", "Robert C. Martin", "A timeless guide on designing maintainable and scalable software systems using layered architecture.", "9780134494166"),
                new BookInfo("Domain-Driven Design", "Eric Evans", "Defines a methodology for tackling complex software projects by aligning code with real-world business concepts.", "9780321125217"),
                new BookInfo("Patterns of Enterprise Application Architecture", "Martin Fowler", "Covers proven patterns for building robust, large-scale enterprise applications.", "9780321127420"),
                new BookInfo("Designing Data-Intensive Applications", "Martin Kleppmann", "Explains principles for reliable, scalable, and maintainable data systems in modern applications.", "9781449373320"),
                new BookInfo("Software Architecture in Practice", "Len Bass", "A practical guide that teaches how to think about and design software architectures effectively.", "9780321815736"),
                new BookInfo("Building Microservices", "Sam Newman", "Teaches how to design, build, and deploy microservices architectures that scale.", "9781491950357"),
                new BookInfo("Fundamentals of Software Architecture", "Mark Richards and Neal Ford", "A detailed look at architectural patterns, trade-offs, and decision-making strategies.", "9781492043454"),
                new BookInfo("The Pragmatic Programmer", "Andrew Hunt and David Thomas", "A handbook for practical programming, teamwork, and craftsmanship.", "9780135957059"),
                new BookInfo("Clean Code", "Robert C. Martin", "A must-read that shows how to write clean, maintainable, and efficient code.", "9780132350884"),
                new BookInfo("Refactoring", "Martin Fowler", "Explains how to improve code design without changing its behavior.", "9780134757599"),
                new BookInfo("Continuous Delivery", "Jez Humble and David Farley", "Shows how to automate and deliver high-quality software at speed.", "9780321601919"),
                new BookInfo("Site Reliability Engineering", "Niall Richard Murphy", "Google’s principles for building and maintaining reliable systems at scale.", "9781491929124"),
                new BookInfo("Accelerate", "Nicole Forsgren, Jez Humble, and Gene Kim", "Data-backed insights into what makes elite software delivery teams succeed.", "9781942788331"),
                new BookInfo("Enterprise Integration Patterns", "Gregor Hohpe and Bobby Woolf", "Classic guide to integrating enterprise applications and systems using messaging.", "9780321200686"),
                new BookInfo("The Art of Computer Programming, Vol. 1", "Donald Knuth", "The definitive reference on algorithms and computer science theory.", "9780201896831"),
                new BookInfo("Structure and Interpretation of Computer Programs", "Harold Abelson", "An influential introduction to computer science fundamentals using Scheme.", "9780262510875"),
                new BookInfo("Compilers: Principles, Techniques, and Tools", "Alfred V. Aho", "The classic ‘Dragon Book’ on compiler theory and implementation.", "9780321486813"),
                new BookInfo("Design Patterns", "Erich Gamma et al.", "Introduces 23 reusable design patterns for object-oriented software development.", "9780201633610"),
                new BookInfo("Introduction to Algorithms", "Thomas H. Cormen", "Comprehensive guide to data structures and algorithms used in modern computing.", "9780262033848"),
                new BookInfo("The Mythical Man-Month", "Frederick Brooks Jr.", "Insights into software engineering management and project scheduling challenges.", "9780201835953"),
                new BookInfo("Code Complete", "Steve McConnell", "A practical guide to writing robust, efficient, and maintainable software.", "9780735619678"),
                new BookInfo("Working Effectively with Legacy Code", "Michael Feathers", "Teaches strategies to safely refactor and modernize old, untested codebases.", "9780131177055"),
                new BookInfo("A Philosophy of Software Design", "John Ousterhout", "Explains design principles to manage complexity and improve system quality.", "9781732102200"),
                new BookInfo("Continuous Integration", "Paul Duvall", "Outlines CI techniques to maintain code quality during rapid development cycles.", "9780321336385"),
                new BookInfo("The Art of Scalability", "Martin L. Abbott and Michael T. Fisher", "Explores how to scale technology, processes, and organizations effectively.", "9780134032801"),
                new BookInfo("Docker Deep Dive", "Nigel Poulton", "Comprehensive guide to mastering Docker and containerization concepts.", "9781521822807"),
                new BookInfo("Kubernetes: Up and Running", "Brendan Burns", "Explains how to deploy, scale, and manage containerized applications using Kubernetes.", "9781492046530"),
                new BookInfo("Hands-On Machine Learning with Scikit-Learn", "Aurélien Géron", "Practical hands-on introduction to modern machine learning techniques in Python.", "9781492032649"),
                new BookInfo("Deep Learning with Python", "François Chollet", "A clear and approachable guide to deep learning concepts using Keras.", "9781617294433"),
                new BookInfo("Programming Rust", "Jim Blandy and Jason Orendorff", "A deep dive into Rust for building fast, safe, and concurrent systems.", "9781492032656"),
                new BookInfo("Go Programming Blueprints", "Mat Ryer", "Practical projects and design examples written in the Go programming language.", "9781783988020"),
                new BookInfo("Learning SQL", "Alan Beaulieu", "Teaches SQL fundamentals and how to effectively query relational databases.", "9781492057611"),
                new BookInfo("The Manga Guide to Databases", "Kenichi Kadowaki", "An illustrated introduction to databases and how they work.", "9781593271909"),
                new BookInfo("Data Structures and Algorithms Made Easy", "Narasimha Karumanchi", "Simplifies core data structure and algorithm concepts for interviews and exams.", "9788193245279"),
                new BookInfo("Cracking the Coding Interview", "Gayle Laakmann McDowell", "The go-to guide for coding interview prep with over 180 solved problems.", "9780984782857")
        );
    }
}
