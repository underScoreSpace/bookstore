import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useCart } from "./App.jsx";
import "./CartPage.css";

export default function CartPage({ openSignIn }) {
    const { cart, removeFromCart, updateQuantity } = useCart();
    const [checkoutMessage, setCheckoutMessage] = useState("");
    const navigate = useNavigate();

    const currentUser = (() => {
        try {
            const saved = localStorage.getItem("currentUser");
            return saved ? JSON.parse(saved) : null;
        } catch {
            return null;
        }
    })();

    if (cart.length === 0) {
        return (
            <div className="cart-page">
                <div className="empty-cart">
                    <h2>Your cart is empty</h2>
                    <p>Start adding some books!</p>
                    <Link to="/books">
                        <button>Browse Books</button>
                    </Link>
                </div>
            </div>
        );
    }

    const subtotal = cart.reduce((total, item) => total + (item.price * item.quantity), 0);
    const tax = subtotal * 0.08;
    const shipping = subtotal > 50 ? 0 : 5.99;
    const total = subtotal + tax + shipping;

    const handleProceed = () => {
        setCheckoutMessage("");
        if (!currentUser) {
            openSignIn ? openSignIn() : alert("Please sign in to proceed.");
            return;
        }
        navigate("/checkout");
    };
    console.log("Cart Items:", cart);
    return (
        <div className="cart-page">
            <h1>Shopping Cart</h1>

            <div className="cart-container">

                <div>
                    {cart.map((item) => (
                        <div key={item.id} className="cart-item">
                            <div className="cart-product-group">
                                <img
                                    src={`https://covers.openlibrary.org/b/isbn/${item.isbn}-M.jpg`}
                                    alt={item.title}
                                    className="cart-thumb"
                                    onError={(e) => { e.target.src = 'https://via.placeholder.com/60x90?text=No+Cover'; }}
                                />

                                <div className="item-info">
                                    <h3>{item.title}</h3>
                                    <p className="author">by {item.author}</p>
                                    <p className="price">${item.price.toFixed(2)} each</p>
                                </div>
                            </div>

                            <div className="item-controls">
                                <div className="quantity-controls">
                                    <button onClick={() => updateQuantity(item.id, item.quantity - 1)}>
                                        -
                                    </button>
                                    <span>{item.quantity}</span>
                                    <button onClick={() => updateQuantity(item.id, item.quantity + 1)}>
                                        +
                                    </button>
                                </div>

                                <div className="item-total">
                                    ${(item.price * item.quantity).toFixed(2)}
                                </div>

                                <button
                                    className="remove-btn"
                                    onClick={() => removeFromCart(item.id)}
                                >
                                    Remove
                                </button>
                            </div>
                        </div>
                    ))}
                </div>

                <div className="cart-summary">
                    <h2>Order Summary</h2>

                    <div className="summary-row">
                        <span>Subtotal:</span>
                        <span>${subtotal.toFixed(2)}</span>
                    </div>

                    <div className="summary-row">
                        <span>Tax (8%):</span>
                        <span>${tax.toFixed(2)}</span>
                    </div>

                    <div className="summary-row">
                        <span>Shipping:</span>
                        <span>{shipping === 0 ? "FREE" : shipping.toFixed(2)}</span>
                    </div>

                    {shipping > 0 && (
                        <p className="free-shipping-msg">
                            Add ${(50 - subtotal).toFixed(2)} more for free shipping!
                        </p>
                    )}

                    <div className="summary-row total">
                        <span>Total:</span>
                        <span>${total.toFixed(2)}</span>
                    </div>

                    <button className="checkout-btn" onClick={handleProceed}>
                        Proceed to Checkout
                    </button>

                    {checkoutMessage && (
                        <p className="checkout-message">{checkoutMessage}</p>
                    )}

                    <Link to="/books" className="continue-shopping">
                        Continue Shopping
                    </Link>
                </div>

            </div>
        </div>
    );
}
