import { Link } from "react-router-dom";
import { useCart } from "./App.jsx";
import "./CartPage.css";

export default function CartPage() {
    const { cart, removeFromCart, updateQuantity, cartTotal } = useCart();

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

    // Calculate order summary
    const subtotal = cart.reduce((total, item) => total + (item.price * item.quantity), 0);
    const tax = subtotal * 0.08; // 8% tax
    const shipping = subtotal > 50 ? 0 : 5.99; // Free shipping over $50
    const total = subtotal + tax + shipping;

    return (
        <div className="cart-page">
            <h1>Shopping Cart</h1>
            
            <div className="cart-container">
                {/* Left side - Cart Items */}
                <div>
                    {cart.map((item) => (
                        <div key={item.id} className="cart-item">
                            <div>
                                <h3>{item.title}</h3>
                                <p className="author">by {item.author}</p>
                                <p className="price">${item.price.toFixed(2)} each</p>
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
                                
                                <div style={{ fontWeight: 'bold', color: '#333' }}>
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

                {/* Right side - Order Summary */}
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
                        <span>{shipping === 0 ? "FREE" : `${shipping.toFixed(2)}`}</span>
                    </div>
                    
                    {shipping > 0 && (
                        <p style={{ fontSize: '0.85rem', color: '#666', marginTop: '0.5rem' }}>
                            Add ${(50 - subtotal).toFixed(2)} more for free shipping!
                        </p>
                    )}
                    
                    <div className="summary-row total">
                        <span>Total:</span>
                        <span>${total.toFixed(2)}</span>
                    </div>
                    
                    <button className="checkout-btn">
                        Proceed to Checkout
                    </button>
                    
                    <Link to="/books" className="continue-shopping">
                        Continue Shopping
                    </Link>
                </div>
            </div>
        </div>
    );
}