import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useCart } from "./App.jsx";
import "./BookDetailPage.css";

export default function BookDetailPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const { addToCart } = useCart();

    const [book, setBook] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [userRating, setUserRating] = useState(0);
    const [hoverRating, setHoverRating] = useState(0);
    const [reviewText, setReviewText] = useState("");
    const [reviews, setReviews] = useState([]);

    // Read logged-in user from localStorage (same idea as FinalTake)
    const currentUser = (() => {
        try {
            const saved = localStorage.getItem("currentUser");
            return saved ? JSON.parse(saved) : null;
        } catch {
            return null;
        }
    })();

    useEffect(() => {
        fetchBookDetails();
        fetchReviews();
    }, [id]);

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
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const fetchReviews = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/books/${id}/reviews`);
            if (!response.ok) {
                setReviews([]);
                return;
            }

            const data = await response.json();
            setReviews(
                data.map((r) => ({
                    id: r.id,
                    user: r.userDisplayName,
                    rating: r.rating,
                    comment: r.comment,
                }))
            );
        } catch (err) {
            console.error("Failed to load reviews:", err);
            setReviews([]);
        }
    };

    const handleSubmitReview = async (e) => {
        e.preventDefault();

        if (!currentUser) {
            alert("You must be signed in to write a review.");
            return;
        }

        if (userRating === 0) {
            alert("Please select a rating!");
            return;
        }

        if (reviewText.trim() === "") {
            alert("Please write a review!");
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/api/books/${id}/reviews`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    userId: currentUser.id,
                    rating: userRating,
                    comment: reviewText.trim(),
                }),
            });

            if (!response.ok) {
                const msg = await response.text();
                throw new Error(msg || "Failed to submit review");
            }

            const updated = await response.json();
            setReviews(
                updated.map((r) => ({
                    id: r.id,
                    user: r.userDisplayName,
                    rating: r.rating,
                    comment: r.comment,
                }))
            );

            setUserRating(0);
            setReviewText("");
            alert("Review submitted!");
        } catch (err) {
            console.error("Error submitting review:", err);
            alert(err.message);
        }
    };

    const renderStars = (rating) => {
        return [...Array(5)].map((_, index) => (
            <span key={index} className="star">
                {index < rating ? "★" : "☆"}
            </span>
        ));
    };

    const averageRating =
        reviews.length > 0
            ? (reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length).toFixed(1)
            : "No ratings yet";

    if (loading) return <div className="book-detail-page"><p>Loading...</p></div>;
    if (error) return <div className="book-detail-page"><p className="error">Error: {error}</p></div>;
    if (!book) return <div className="book-detail-page"><p>Book not found</p></div>;

    return (
        <div className="book-detail-page">
            <button className="back-button" onClick={() => navigate(-1)}>
                ← Back to Books
            </button>

            <div className="book-detail-container">
                <div className="book-info">
                    <h1>{book.title}</h1>
                    <h2 className="author">by {book.author}</h2>

                    <div className="rating-summary">
                        <div className="stars-display">
                            {reviews.length === 0
                                ? renderStars(0)
                                : renderStars(Math.floor(parseFloat(averageRating)))}
                        </div>
                        <span className="rating-text">
                            {reviews.length === 0
                                ? "No ratings yet"
                                : `${averageRating} (${reviews.length} reviews)`}
                        </span>
                    </div>

                    <div className="price-section">
                        <span className="price">${book.price.toFixed(2)}</span>
                        <span className="stock">
                            {book.stockQty > 0 ? `${book.stockQty} in stock` : "Out of stock"}
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

            <div className="reviews-section">
                <h2>Customer Reviews</h2>

                <form className="review-form" onSubmit={handleSubmitReview}>
                    <h3>Write a Review</h3>

                    {!currentUser && (
                        <p style={{ color: "#a00", marginBottom: "0.5rem" }}>
                            You must be signed in to submit a review.
                        </p>
                    )}

                    <div className="rating-input">
                        <label>Your Rating:</label>
                        <div className="stars-input">
                            {[1, 2, 3, 4, 5].map((star) => (
                                <span
                                    key={star}
                                    className={`star-input ${
                                        star <= (hoverRating || userRating) ? "filled" : ""
                                    }`}
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
                        disabled={!currentUser}
                    />

                    <button type="submit" className="submit-review-btn" disabled={!currentUser}>
                        Submit Review
                    </button>
                </form>

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
