import { useState } from "react";
import { useCart } from "./App.jsx";
import "./AiHelper.css";

export default function AiHelper() {
    const [query, setQuery] = useState("");
    const [message, setMessage] = useState("");
    const [displayedMessage, setDisplayedMessage] = useState("");
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(false);
    const { addToCart } = useCart();

    const askAI = async () => {
        if (!query.trim()) return;

        setLoading(true);
        setMessage("");
        setDisplayedMessage("");
        setBooks([]);

        try {
            const res = await fetch("http://localhost:8080/api/ai/recommend", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ query })
            });

            const data = await res.json();

            // Start typing effect
            typeOutMessage(data.message || "");
            setBooks(data.books || []);

        } catch {
            typeOutMessage("Could not reach AI service.");
        } finally {
            setLoading(false);
        }
    };

    //---- Typing effect ----
    const typeOutMessage = (fullText) => {
        setMessage(fullText);
        setDisplayedMessage("");

        let index = 0;
        const interval = setInterval(() => {
            index++;
            setDisplayedMessage(fullText.slice(0, index));

            if (index >= fullText.length) {
                clearInterval(interval);
            }
        }, 15);
    };

    return (
        <div className="ai-helper">
            <h3>AI Book Helper</h3>
            <p className="ai-subtitle">
                Tell me what you're looking for — I’ll recommend 3 books.
            </p>

            <div className="ai-input">
                <input
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    placeholder="e.g. databases, system design, testing..."
                />
                <button onClick={askAI} disabled={loading}>
                    {loading ? "Thinking..." : "Ask"}
                </button>
            </div>

            {/* AI text response */}
            {displayedMessage && (
                <div className="ai-message">
                    {formatMessage(displayedMessage)}
                </div>
            )}

            {/* Recommended books */}
            {books.length > 0 && (
                <div className="ai-results">
                    {books.map((book) => (
                        <div key={book.id} className="ai-book">
                            <div className="ai-book-info">
                                <strong>{book.title}</strong>
                                <div className="ai-author">by {book.author}</div>
                                <div className="ai-price">
                                    ${Number(book.price).toFixed(2)}
                                </div>
                            </div>

                            <button
                                className="ai-add-btn"
                                onClick={() => addToCart(book)}
                            >
                                Add to Cart
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}


function formatMessage(text) {
    return text
        .split("\n")
        .map((line, i) => {
            if (!line.trim()) return null;

            if (/^\d+\./.test(line)) {
                return (
                    <div key={i} className="ai-line ai-numbered">
                        {line}
                    </div>
                );
            }

            return <p key={i}>{line}</p>;
        });
}
