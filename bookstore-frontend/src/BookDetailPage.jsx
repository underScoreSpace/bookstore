import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {useCart} from "./App.jsx"
import "./BookDetailPage.css";

export default function BookDetailPage() {

    // Get the book ID from the URL
    const {id} = useParams();

    // Allows us to back to the previous page
    const navigate = useNavigate();

    // Allow user to add to cart
    const { addToCart } = useCart();

    // State to store the book details
    const [book, setBook] = useState(null);

    // State for loading and errors
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // State for user's rating (1-5 stars)
    const [userRating, setUserRating] = useState(0);

    // State for hovering over stars (for visual effect)
    const [hoverRating, setHoverRating] = useState(0);

    // State for user's review text
    const [reviewText, setReviewText] = useState("");

    // State to store all reviews for this book
    const [reviews, setReviews] = useState([]);

    // Fetch book details when component loads
    useEffect(() => {
        fetchBookDetails();
    }, [id]);

    // Function to fetch book from backend
    const fetchBookDetails = async () => {
        setLoading(true);
        setError(null);

        try {
            const response = await fetch(`http://localhost:8080/api/books/${id}`);

            if (!response.ok) {
                throw new Error("Book not found");
            }

            const data = await response.json();
            setBook(data);

            // For now, using mock reviews
            setReviews([
                { id: 1, user: "John Doe", rating: 5, comment: "Excellent book!" },
                { id: 2, user: "Jane Smith", rating: 4, comment: "Very informative." }
            ]);

        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };
    // Function to handle submitting a review
    const handleSubmitReview = (e) => {
        e.preventDefault();

        if (userRating === 0) {
            alert("Please select a rating!");
            return;
        }

        if (reviewText.trim() === "") {
            alert("Please write a review!");
            return;
        }

        // TODO: Later, send review to backend
        // For now, just add to local state
        const newReview = {
            id: reviews.length + 1,
            user: "You",
            rating: userRating,
            comment: reviewText
        };

        setReviews([newReview, ...reviews]);
        setUserRating(0);
        setReviewText("");
        alert("Review submitted!");
    };

    // Render star icons (★ or ☆)
    const renderStars = (rating) => {
        return [...Array(5)].map((_, index) => (
            <span key={index} className="star">
                {index < rating ? "★" : "☆"}
            </span>
        ));
    };

    // Calculate average rating
    const averageRating = reviews.length > 0
        ? (reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length).toFixed(1)
        : "No ratings yet";

    if (loading) return <div className="book-detail-page"><p>Loading...</p></div>;
    if (error) return <div className="book-detail-page"><p className="error">Error: {error}</p></div>;
    if (!book) return <div className="book-detail-page"><p>Book not found</p></div>;

    return (
        <div className="book-detail-page">
            {/* Back Button */}
            <button className="back-button" onClick={() => navigate(-1)}>
                ← Back to Books
            </button>

            {/* Book Details Section */}
            <div className="book-detail-container">
                <div className="book-info">
                    <h1>{book.title}</h1>
                    <h2 className="author">by {book.author}</h2>

                    <div className="rating-summary">
                        <div className="stars-display">
                            {renderStars(Math.round(averageRating))}
                        </div>
                        <span className="rating-text">
                            {averageRating} ({reviews.length} reviews)
                        </span>
                    </div>

                    <div className="price-section">
                        <span className="price">${book.price.toFixed(2)}</span>
                        <span className="stock">
                            {book.stockQty > 0
                                ? `${book.stockQty} in stock`
                                : "Out of stock"}
                        </span>
                    </div>

                    <p className="description">{book.description}</p>

                    <button
                        className="add-to-cart-btn"
                        disabled={book.stockQty === 0}
                        onClick={() => addToCart(book)}
                    >
                        {book.stockQty > 0 ? "Add to Cart" : "Unavailable"}
                    </button>
                </div>
            </div>

            {/* Reviews Section */}
            <div className="reviews-section">
                <h2>Customer Reviews</h2>

                {/* Submit Review Form */}
                <form className="review-form" onSubmit={handleSubmitReview}>
                    <h3>Write a Review</h3>

                    <div className="rating-input">
                        <label>Your Rating:</label>
                        <div className="stars-input">
                            {[1, 2, 3, 4, 5].map((star) => (
                                <span
                                    key={star}
                                    className={`star-input ${star <= (hoverRating || userRating) ? 'filled' : ''}`}
                                    onClick={() => setUserRating(star)}
                                    onMouseEnter={() => setHoverRating(star)}
                                    onMouseLeave={() => setHoverRating(0)}
                                >
                                    ★
                                </span>
                            ))}
                        </div>
                    </div>

                    <textarea
                        placeholder="Write your review here..."
                        value={reviewText}
                        onChange={(e) => setReviewText(e.target.value)}
                        rows="4"
                    />

                    <button type="submit" className="submit-review-btn">
                        Submit Review
                    </button>
                </form>

                {/* Display Reviews */}
                <div className="reviews-list">
                    {reviews.length === 0 ? (
                        <p>No reviews yet. Be the first to review!</p>
                    ) : (
                        reviews.map((review) => (
                            <div key={review.id} className="review-card">
                                <div className="review-header">
                                    <strong>{review.user}</strong>
                                    <div className="review-stars">
                                        {renderStars(review.rating)}
                                    </div>
                                </div>
                                <p className="review-comment">{review.comment}</p>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
}