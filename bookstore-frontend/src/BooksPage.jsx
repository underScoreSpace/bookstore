  import { useState, useEffect } from "react";

import "./BooksPage.css"; 

import { useCart } from "./App.jsx"; 

export default function BooksPage() {
    // Use the Cart context to access the addToCart function
    const { addToCart } = useCart();
    
    // State to store the list of books
    const [books, setBooks] = useState([]);

    // State to store the search input
    const [searchQuery, setSearchQuery] = useState("");

    // State to show loading indicator
    const [loading, setLoading] = useState(false);

    // State to store any error messages
    const [error, setError] = useState(null);

    // useEffect: Runs when component first loads
    // The empty [] means "run once on mount"
    useEffect(() => {
        fetchBooks();
    }, []);

    // Function to fetch books from backend
    const fetchBooks = async (query = "") => {
        setLoading(true);
        setError(null);

        try {
            // Build the URL - with or without search query
            const url = query
                ? `http://localhost:8080/api/books/search?query=${encodeURIComponent(query)}`
                : "http://localhost:8080/api/books";

            // Make the HTTP request
            const response = await fetch(url);

            // Check if request was successful
            if (!response.ok) {
                const errorText = await response.text(); 
                throw new Error(errorText || "Failed to fetch books");
            }

            // Convert response to JSON
            const data = await response.json();
            setBooks(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    // Handle form submission (when user clicks Search)
    const handleSearch = (e) => {
        e.preventDefault(); // Prevent page reload
        fetchBooks(searchQuery);
    };

    // Clear search and show all books
    const handleClearSearch = () => {
        setSearchQuery("");
        fetchBooks();
    };

    return (
        <div className="books-page">
            <h1>Browse Our Books</h1>

            {/* Search Form */}
            <form className="search-form" onSubmit={handleSearch}>
                <input
                    type="text"
                    placeholder="Search by title or author..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                />
                <button type="submit">Search</button>
                {searchQuery && (
                    <button type="button" onClick={handleClearSearch}>
                        Clear
                    </button>
                )}
            </form>

            {/* Loading message */}
            {loading && <p className="message">Loading books...</p>}

            {/* Error message */}
            {error && <p className="error-message">Error: {error}</p>}

            {/* No results message */}
            {!loading && books.length === 0 && (
                <p className="message">No books found.</p>
            )}

            {/* Books Grid */}
            <div className="books-grid">
                {books.map((book) => (
                    <div key={book.id} className="book-card">
                        <h3>{book.title}</h3>
                        <p className="author">by {book.author}</p>
                        <p className="description">{book.description}</p>
                        <div className="book-footer">
                            {/* Price is formatted to two decimal places */}
                            <span className="price">${book.price.toFixed(2)}</span> 
                            <span className="stock">
                                {book.stockQty > 0
                                    ? `${book.stockQty} in stock`
                                    : "Out of stock"}
                            </span>
                        </div>
                        <button
                            className="add-to-cart"
                            disabled={book.stockQty === 0}
                            // Calls the global addToCart function when clicked
                            onClick={() => addToCart(book)} 
                        >
                            {book.stockQty > 0 ? "Add to Cart" : "Unavailable"}
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
}