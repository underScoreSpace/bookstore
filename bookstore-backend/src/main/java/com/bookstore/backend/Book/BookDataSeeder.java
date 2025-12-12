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
                new BookInfo("Clean Architecture", "Robert C. Martin",
                        "A timeless guide on designing maintainable and scalable software systems using layered architecture.",
                        "9780134494166"),

                new BookInfo("Domain-Driven Design", "Eric Evans",
                        "Defines a methodology for tackling complex software projects by aligning code with real-world business concepts.",
                        "9780321125217"),

                new BookInfo("Patterns of Enterprise Application Architecture", "Martin Fowler",
                        "Covers proven patterns for building robust, large-scale enterprise applications.",
                        "9780321127426"),

                new BookInfo("Release It!", "Michael Nygard",
                        "Teaches how to design and operate resilient systems in production environments.",
                        "9781680502398"),

                new BookInfo("The Phoenix Project", "Gene Kim",
                        "A novel about DevOps, IT, and helping businesses win through better software delivery.",
                        "9781942788294"),

                new BookInfo("Designing Data-Intensive Applications", "Martin Kleppmann",
                        "Explains principles for reliable, scalable, and maintainable data systems in modern applications.",
                        "9781449373320"),

                new BookInfo("Software Architecture in Practice", "Len Bass",
                        "A practical guide that teaches how to think about and design software architectures effectively.",
                        "9780321815736"),

                new BookInfo("Building Microservices", "Sam Newman",
                        "Teaches how to design, build, and deploy microservices architectures that scale.",
                        "9781491950357"),

                new BookInfo("Fundamentals of Software Architecture", "Mark Richards and Neal Ford",
                        "A detailed look at architectural patterns, trade-offs, and decision-making strategies.",
                        "9781492043454"),

                new BookInfo("The Mythical Man-Month", "Frederick Brooks Jr.",
                        "Insights into software engineering management and project scheduling challenges.",
                        "9780201835953"),

                new BookInfo("Code Complete", "Steve McConnell",
                        "A practical guide to writing robust, efficient, and maintainable software.",
                        "9780735619678"),

                new BookInfo("Working Effectively with Legacy Code", "Michael Feathers",
                        "Teaches strategies to safely refactor and modernize old, untested codebases.",
                        "9780131177055"),

                new BookInfo("A Philosophy of Software Design", "John Ousterhout",
                        "Explains design principles to manage complexity and improve system quality.",
                        "9781732102200"),

                new BookInfo("The Pragmatic Programmer", "Andrew Hunt and David Thomas",
                        "A handbook for practical programming, teamwork, and craftsmanship.",
                        "9780135957059"),

                new BookInfo("Refactoring", "Martin Fowler",
                        "Explains how to improve code design without changing its behavior.",
                        "9780134757599"),

                new BookInfo("Continuous Delivery", "Jez Humble and David Farley",
                        "Shows how to automate and deliver high-quality software at speed.",
                        "9780321601919"),

                new BookInfo("Site Reliability Engineering", "Betsy Beyer et al.",
                        "Google’s principles for building and maintaining reliable systems at scale.",
                        "9781491929124"),

                new BookInfo("Accelerate", "Nicole Forsgren, Jez Humble, and Gene Kim",
                        "Data-backed insights into what makes elite software delivery teams succeed.",
                        "9781942788331"),

                new BookInfo("Enterprise Integration Patterns", "Gregor Hohpe and Bobby Woolf",
                        "Classic guide to integrating enterprise applications and systems using messaging.",
                        "9780321200686"),

                new BookInfo("The Art of Computer Programming, Vol. 1", "Donald Knuth",
                        "The definitive reference on algorithms and computer science theory.",
                        "9780201896831"),

                new BookInfo("Structure and Interpretation of Computer Programs", "Harold Abelson",
                        "An influential introduction to computer science fundamentals using Scheme.",
                        "9780262510875"),

                new BookInfo("Compilers: Principles, Techniques, and Tools", "Alfred V. Aho",
                        "The classic Dragon Book on compiler theory and implementation.",
                        "9780321486813"),

                new BookInfo("The Design of Everyday Things", "Donald A. Norman",
                        "Explains how design impacts usability and human behavior.",
                        "9780465050659"),

                new BookInfo("Design Patterns", "Erich Gamma et al.",
                        "Introduces 23 reusable design patterns for object-oriented software development.",
                        "9780201633610"),

                new BookInfo("Grokking Algorithms", "Aditya Bhargava",
                        "A visual and intuitive introduction to algorithms and problem-solving.",
                        "9781617292231"),

                new BookInfo("Introduction to Algorithms", "Thomas H. Cormen",
                        "Comprehensive guide to data structures and algorithms used in modern computing.",
                        "9780262033848"),

                new BookInfo("The C Programming Language", "Brian Kernighan and Dennis Ritchie",
                        "The authoritative book on the C programming language.",
                        "9780131103627"),

                new BookInfo("Expert Python Programming", "Tarek Ziade",
                        "Advanced techniques for writing clean and maintainable Python code.",
                        "9781785886850"),

                new BookInfo("Effective Java", "Joshua Bloch",
                        "Best practices for writing robust and maintainable Java applications.",
                        "9780134685991"),

                new BookInfo("Java Concurrency in Practice", "Brian Goetz",
                        "Definitive guide to writing concurrent and multithreaded Java programs.",
                        "9780321349606"),

                new BookInfo("Modern Operating Systems", "Andrew S. Tanenbaum",
                        "Comprehensive overview of operating system concepts and design.",
                        "9780133591620"),

                new BookInfo("Computer Networks", "Andrew S. Tanenbaum",
                        "Covers the principles and design of modern computer networks.",
                        "9780132126953"),

                new BookInfo("Database System Concepts", "Abraham Silberschatz",
                        "Core concepts behind database systems and relational models.",
                        "9780073523323"),

                new BookInfo("Operating System Concepts", "Abraham Silberschatz",
                        "Foundational operating system principles and implementations.",
                        "9781118063330"),

                new BookInfo("Distributed Systems: Concepts and Design", "George Coulouris",
                        "Explains distributed system architectures and design principles.",
                        "9780132143011"),

                new BookInfo("High Performance Web Sites", "Steve Souders",
                        "Techniques for speeding up web applications.",
                        "9780596529307"),

                new BookInfo("Web Scalability for Engineers", "Artur Ejsmont",
                        "Practical strategies for scaling web applications.",
                        "9780071843652"),

                new BookInfo("Head First Design Patterns", "Eric Freeman",
                        "A beginner-friendly and visual guide to design patterns.",
                        "9780596007126"),

                new BookInfo("The Google SRE Workbook", "Betsy Beyer et al.",
                        "Hands-on exercises for implementing SRE practices.",
                        "9781491949443"),

                new BookInfo("RESTful Web Services", "Leonard Richardson and Sam Ruby",
                        "Explains REST architecture and web service design.",
                        "9780596529260"),

                new BookInfo("The Unicorn Project", "Gene Kim",
                        "A novel focused on developers, DevOps, and modern IT culture.",
                        "9781942788768"),

                new BookInfo("Monolith to Microservices", "Sam Newman",
                        "Guidance for evolving monolithic systems into microservices.",
                        "9781492047841"),

                new BookInfo("Microservices Patterns", "Chris Richardson",
                        "Design patterns for building resilient microservices systems.",
                        "9781617294549"),

                new BookInfo("Continuous Integration", "Paul M. Duvall",
                        "Best practices for automating code integration and testing.",
                        "9780321336385"),

                new BookInfo("Docker Deep Dive", "Nigel Poulton",
                        "Comprehensive guide to mastering Docker and containerization concepts.",
                        "9781521822807"),

                new BookInfo("Kubernetes: Up and Running", "Brendan Burns",
                        "Explains how to deploy, scale, and manage containerized applications using Kubernetes.",
                        "9781492046530"),

                new BookInfo("Pro Git", "Scott Chacon and Ben Straub",
                        "The definitive guide to Git version control.",
                        "9781484200773"),

                new BookInfo("Code: The Hidden Language of Computer Hardware and Software", "Charles Petzold",
                        "Explains how computers work from the ground up.",
                        "9780735611313"),

                new BookInfo("The DevOps Handbook", "Gene Kim et al.",
                        "Practical guidance for implementing DevOps principles.",
                        "9781942788003"),

                new BookInfo("Programming Rust", "Jim Blandy and Jason Orendorff",
                        "A deep dive into Rust for building fast, safe, and concurrent systems.",
                        "9781491927281"),

                new BookInfo("Learning SQL", "Alan Beaulieu",
                        "Teaches SQL fundamentals and how to effectively query relational databases.",
                        "9781492057611"),

                new BookInfo("Cracking the Coding Interview", "Gayle Laakmann McDowell",
                        "The go-to guide for coding interview prep with over 180 solved problems.",
                        "9780984782857"),
                new BookInfo("SQL Performance Explained", "Marcus Winand",
                "A detailed, practical guide to SQL performance tuning across different databases.",
                "9783950307827"),

                new BookInfo("NoSQL Distilled", "Pramod J. Sadalage and Martin Fowler",
                        "An accessible introduction to the NoSQL movement and how to use these databases effectively.",
                        "9780321826626"),

                new BookInfo("Seven Databases in Seven Weeks", "Eric Redmond and Jim R. Wilson",
                        "Hands-on exploration of diverse database technologies from relational to NoSQL.",
                        "9781934356927"),
                new BookInfo("Pattern Recognition and Machine Learning", "Christopher M. Bishop",
                        "Comprehensive mathematical introduction to machine learning methods.",
                        "9780387310732"),

                new BookInfo("Deep Learning", "Ian Goodfellow, Yoshua Bengio, and Aaron Courville",
                        "One of the most authoritative books on deep learning fundamentals and models.",
                        "9780262035613"),

                new BookInfo("Machine Learning: A Probabilistic Perspective", "Kevin P. Murphy",
                        "A thorough, probabilistic approach to machine learning techniques.",
                        "9780262018029"),

                new BookInfo("Hands-On Kubernetes on Azure", "Matt Fenner and Josh Rosso",
                        "Bridges Kubernetes with cloud-native machine learning workflows (optional choice).",
                        "9781484246149"),
                new BookInfo("Effective Modern C++", "Scott Meyers",
                        "Practical guidance for writing high-performance, modern C++.",
                        "9781491903995"),

                new BookInfo("Programming Language Pragmatics", "Michael L. Scott",
                        "Broad overview of programming language design, semantics, and implementation.",
                        "9780124104099"),

                new BookInfo("Types and Programming Languages", "Benjamin C. Pierce",
                        "Foundational text on type systems and language theory.",
                        "9780262162098"),

                new BookInfo("Learn You a Haskell for Great Good!", "Miran Lipovaca",
                        "Fun and friendly guide to learning Haskell and functional programming.",
                        "9781593272838"),
                new BookInfo("The Kubernetes Book", "Nigel Poulton",
                        "Clear guide to Kubernetes fundamentals and practical usage.",
                        "9781397916454"),

                new BookInfo("Cloud Native DevOps with Kubernetes", "John Arundel and Justin Domingus",
                        "Shows how to build cloud native workflows and deploy with confidence.",
                        "9781492076958"),

                new BookInfo("Kubernetes Patterns", "Bilgin Ibryam and Roland Huß",
                        "Design patterns for building resilient, scalable Kubernetes applications.",
                        "9781492050283")
        );
    }
}
