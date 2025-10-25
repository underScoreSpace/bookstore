package com.bookstore.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

// CommandLineRunner ensures this code runs immediately after the application starts
@Component
public class BookDataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final Random random = new Random();
    
    // Spring automatically injects the repository for us to save data
    public BookDataSeeder(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if the database already has books. If so, do nothing.
        if (bookRepository.count() > 0) {
            System.out.println("Database already populated. Skipping data seeding.");
            return;
        }

        System.out.println("Database is empty. Starting data seeding...");
        
        // This is where we call the external tool to get book ideas
        List<Book> booksToSeed = new ArrayList<>();
        
        // Call the Google Search tool to get a list of popular book titles
        List<String> rawTitles = getBookTitlesFromApi("list of 75 popular software architecture books with authors");

        for (String rawTitle : rawTitles) {
            // Simple parsing: assuming the format might be "Title by Author"
            String title;
            String author;
            
            int byIndex = rawTitle.toLowerCase().indexOf(" by ");
            if (byIndex != -1) {
                title = rawTitle.substring(0, byIndex).trim();
                author = rawTitle.substring(byIndex + 4).trim();
            } else {
                title = rawTitle.trim();
                author = "Various Authors";
            }

            // Generate realistic random data
            double price = 15.00 + (35.00 * random.nextDouble()); // Price between $15.00 and $50.00
            
            // Initial listing status: 80% listed, 20% unlisted (for testing the admin feature)
            boolean isListed = random.nextDouble() < 0.8; 

            // Note: Book is assumed to be defined in com.bookstore.backend as well
            Book book = new Book(title, author, Math.round(price * 100.0) / 100.0, isListed);
            booksToSeed.add(book);
        }

        // Save all books in a single batch
        bookRepository.saveAll(booksToSeed);
        System.out.println("Successfully seeded " + bookRepository.count() + " books into the database.");
    }
    
    // Helper function to call the Google Search API (simulated)
    private List<String> getBookTitlesFromApi(String query) {
        // In a real Spring Boot application, this logic would involve a RestTemplate 
        // or WebClient call to a proxy service or the actual external book API.
        // Since we are simulating that search here, we will return a mock list
        // of 75 high-quality, relevant books.
        
        // Simulating the API response:
        return List.of(
            "Clean Architecture by Robert C. Martin",
            "Domain-Driven Design by Eric Evans",
            "Patterns of Enterprise Application Architecture by Martin Fowler",
            "Release It! by Michael Nygard",
            "The Phoenix Project by Gene Kim",
            "Designing Data-Intensive Applications by Martin Kleppmann",
            "Software Architecture in Practice by Len Bass",
            "Building Microservices by Sam Newman",
            "Fundamentals of Software Architecture by Mark Richards and Neal Ford",
            "The Mythical Man-Month by Frederick Brooks Jr.",
            "Code Complete by Steve McConnell",
            "Working Effectively with Legacy Code by Michael Feathers",
            "A Philosophy of Software Design by John Ousterhout",
            "The Pragmatic Programmer by Andrew Hunt and David Thomas",
            "Refactoring: Improving the Design of Existing Code by Martin Fowler",
            "Continuous Delivery by Jez Humble and David Farley",
            "Site Reliability Engineering by Niall Richard Murphy",
            "Accelerate by Nicole Forsgren, Jez Humble, and Gene Kim",
            "Enterprise Integration Patterns by Gregor Hohpe and Bobby Woolf",
            "The Art of Computer Programming, Vol. 1 by Donald Knuth",
            "Structure and Interpretation of Computer Programs by Harold Abelson",
            "Compilers: Principles, Techniques, and Tools by Alfred V. Aho",
            "The Design of Everyday Things by Donald A. Norman",
            "Design Patterns: Elements of Reusable Object-Oriented Software by Gamma et al.",
            "Grokking Algorithms by Aditya Bhargava",
            "Introduction to Algorithms by Thomas H. Cormen",
            "The C Programming Language by Brian Kernighan and Dennis Ritchie",
            "Expert Python Programming by Tarek Ziade",
            "Effective Java by Joshua Bloch",
            "Concurrency in Practice by Brian Goetz",
            "Modern Operating Systems by Andrew S. Tanenbaum",
            "Computer Networks by Andrew S. Tanenbaum",
            "Database System Concepts by Abraham Silberschatz",
            "Operating System Concepts by Abraham Silberschatz",
            "Distributed Systems: Concepts and Design by George Coulouris",
            "High Performance Web Sites by Steve Souders",
            "Web Scalability for Engineers by Artur Ejsmont",
            "Head First Design Patterns by Eric Freeman",
            "Clean Code by Robert C. Martin",
            "The Google SRE Workbook by Betsy Beyer et al.",
            "RESTful Web Services by Leonard Richardson and Sam Ruby",
            "The Unicorn Project by Gene Kim",
            "Monolith to Microservices by Sam Newman",
            "Microservices Patterns by Chris Richardson",
            "Continuous Integration by Paul M. Duvall",
            "The Art of Scalability by Martin L. Abbott and Michael T. Fisher",
            "Docker Deep Dive by Nigel Poulton",
            "Kubernetes: Up and Running by Brendan Burns",
            "Hands-On Machine Learning with Scikit-Learn by Aurélien Géron",
            "Deep Learning with Python by François Chollet",
            "Practical C++ Programming by Steve Oualline",
            "Thinking in Java by Bruce Eckel",
            "The Well-Grounded Rubyist by David Black",
            "Eloquent JavaScript by Marijn Haverbeke",
            "You Don't Know JS Yet by Kyle Simpson",
            "Pro Git by Scott Chacon and Ben Straub",
            "Code: The Hidden Language of Computer Hardware and Software by Charles Petzold",
            "Data Science for Business by Foster Provost",
            "Hands-On Cloud Native Applications by Laurent Broudoux",
            "The DevOps Handbook by Gene Kim et al.",
            "Agile Software Development, Principles, Patterns, and Practices by Robert C. Martin",
            "Extreme Programming Explained by Kent Beck",
            "Specification by Example by Gojko Adzic",
            "Impact Mapping by Gojko Adzic",
            "User Story Mapping by Jeff Patton",
            "Scrum: The Art of Doing Twice the Work in Half the Time by Jeff Sutherland",
            "Lean Software Development by Mary Poppendieck",
            "Programming Rust by Jim Blandy and Jason Orendorff",
            "Go Programming Blueprints by Mat Ryer",
            "Full Stack Development with JHipster by Deepu K Sasidharan",
            "Learning SQL by Alan Beaulieu",
            "The Manga Guide to Databases by Kenichi Kadowaki",
            "Data Structures and Algorithms Made Easy by Narasimha Karumanchi",
            "Cracking the Coding Interview by Gayle Laakmann McDowell"
        );
    }
}
