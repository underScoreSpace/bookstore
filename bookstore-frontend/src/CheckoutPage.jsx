import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useCart } from "./App.jsx";
import "./CheckoutPage.css";

export default function CheckoutPage({ openSignIn }) {
    const { cart, cartTotal, clearCart } = useCart();
    const navigate = useNavigate();

    const currentUser = (() => {
        try {
            const saved = localStorage.getItem("currentUser");
            return saved ? JSON.parse(saved) : null;
        } catch {
            return null;
        }
    })();

    const [form, setForm] = useState({
        shipName: "",
        shipAddress1: "",
        shipAddress2: "",
        shipCity: "",
        shipRegion: "",
        shipPostal: "",
        shipCountry: "US",
    });

    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    if (!currentUser) {
        if (openSignIn) {
            openSignIn();
        }
        return (
            <div className="checkout-page">
                <h1>Checkout</h1>
                <p>You need to sign in to complete your order.</p>
            </div>
        );
    }

    if (cart.length === 0) {
        return (
            <div className="checkout-page">
                <h1>Checkout</h1>
                <p>Your cart is empty.</p>
            </div>
        );
    }

    const subtotal = cart.reduce((total, item) => total + (item.price * item.quantity), 0);
    const tax = subtotal * 0.08;
    const shipping = subtotal > 50 ? 0 : 5.99;
    const total = subtotal + tax + shipping;

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setError("");

        if (!form.shipName || !form.shipAddress1 || !form.shipCity || !form.shipRegion || !form.shipPostal) {
            setError("Please fill out all required fields.");
            return;
        }

        setLoading(true);
        try {
            const res = await fetch("http://localhost:8080/api/orders/checkout", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    userId: currentUser.id,
                    ...form,
                }),
            });

            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg || "Checkout failed");
            }

            const data = await res.json(); // CheckoutResponse
            clearCart();
            setMessage(
                `${data.message} Order #${data.orderNumber} • Total $${Number(
                    data.total
                ).toFixed(2)}`
            );

        } catch (err) {
            console.error(err);
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (

        <div className="checkout-page">

            <h1>Checkout</h1>
            <button className="back-button" onClick={() => navigate("/cart")}>
                Back to Cart
            </button>


            <div className="checkout-layout">
                <form className="checkout-form" onSubmit={handleSubmit}>
                    <h2>Shipping Information</h2>

                    <label>
                        Full Name*
                        <input
                            type="text"
                            name="shipName"
                            value={form.shipName}
                            onChange={handleChange}
                        />
                    </label>

                    <label>
                        Address Line 1*
                        <input
                            type="text"
                            name="shipAddress1"
                            value={form.shipAddress1}
                            onChange={handleChange}
                        />
                    </label>

                    <label>
                        Address Line 2
                        <input
                            type="text"
                            name="shipAddress2"
                            value={form.shipAddress2}
                            onChange={handleChange}
                        />
                    </label>

                    <label>
                        City*
                        <input
                            type="text"
                            name="shipCity"
                            value={form.shipCity}
                            onChange={handleChange}
                        />
                    </label>

                    <label>
                        State / Region*
                        <input
                            type="text"
                            name="shipRegion"
                            value={form.shipRegion}
                            onChange={handleChange}
                        />
                    </label>

                    <label>
                        Postal Code*
                        <input
                            type="text"
                            name="shipPostal"
                            value={form.shipPostal}
                            onChange={handleChange}
                        />
                    </label>

                    <label>
                        Country
                        <input
                            type="text"
                            name="shipCountry"
                            value={form.shipCountry}
                            onChange={handleChange}
                        />
                    </label>

                    {error && <p className="checkout-error">{error}</p>}
                    {message && <p className="checkout-success">{message}</p>}

                    <button type="submit" disabled={loading}>
                        {loading ? "Placing order..." : "Place Order"}
                    </button>
                </form>

                <div className="checkout-summary">
                    <h2>Order Summary</h2>
                    {cart.map((item) => (
                        <div key={item.id} className="checkout-item">
                            <div>
                                <strong>{item.title}</strong>
                                <div className="checkout-item-author">by {item.author}</div>
                                <div className="checkout-item-qty">
                                    Qty: {item.quantity}
                                </div>
                            </div>
                            <div>
                                ${(item.price * item.quantity).toFixed(2)}
                            </div>
                        </div>
                    ))}

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
                        <span>{shipping === 0 ? "FREE" : `$${shipping.toFixed(2)}`}</span>
                    </div>
                    <div className="summary-row total">
                        <span>Total:</span>
                        <span>${total.toFixed(2)}</span>
                    </div>
                </div>
            </div>
        </div>
    );
}
