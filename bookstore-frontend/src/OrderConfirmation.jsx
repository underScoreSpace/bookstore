import { useLocation, useNavigate } from "react-router-dom";
import "./OrderConfirmation.css";

export default function OrderConfirmation() {
    const location = useLocation();
    const navigate = useNavigate();

    const {
        orderNumber,
        message,
        userEmail,
        subtotal,
        tax,
        shipping,
        total,
        items,
        shippingInfo,
    } = location.state || {};

    if (!location.state) {
        return (
            <div className="order-confirmation-page">
                <h1>Order Confirmation</h1>
                <p>No order details found.</p>
                <button
                    className="continue-shopping-btn"
                    onClick={() => navigate("/books")}
                >
                    Continue Shopping
                </button>
            </div>
        );
    }

    return (
        <div className="order-confirmation-page">
            <h1>Thank you for your order!</h1>
            {message && <p className="order-message">{message}</p>}

            <div className="order-summary-box">

                <h2>Order Summary</h2>

                <div className="order-meta">
                    <p><strong>Order ID:</strong> {orderNumber}</p>
                    <p><strong>Email:</strong> {userEmail}</p>
                </div>

                <h3>Shipping Address</h3>
                <div className="shipping-info">
                    <p>{shippingInfo.shipName}</p>
                    <p>{shippingInfo.shipAddress1}</p>
                    {shippingInfo.shipAddress2 && <p>{shippingInfo.shipAddress2}</p>}
                    <p>
                        {shippingInfo.shipCity}, {shippingInfo.shipRegion}{" "}
                        {shippingInfo.shipPostal}
                    </p>
                    <p>{shippingInfo.shipCountry}</p>
                </div>

                <hr />

                {items && items.map((item) => (
                    <div key={item.id} className="order-item-row">
                        <div>
                            <strong>{item.title}</strong>
                            <div className="order-item-author">by {item.author}</div>
                            <div className="order-item-qty">Qty: {item.quantity}</div>
                        </div>
                        <div className="order-item-price">
                            ${(item.price * item.quantity).toFixed(2)}
                        </div>
                    </div>
                ))}

                <hr />

                <div className="summary-row">
                    <span>Subtotal:</span>
                    <span>${subtotal.toFixed(2)}</span>
                </div>

                <div className="summary-row">
                    <span>Tax (8%):</span>
                    <span>${tax.toFixed(2)}</span>
                </div>

                <div className="summary-row">
                    <span>Shipping Cost:</span>
                    <span>{shipping === 0 ? "FREE" : `$${shipping.toFixed(2)}`}</span>
                </div>

                <div className="summary-row total">
                    <span>Total:</span>
                    <span>${total.toFixed(2)}</span>
                </div>

                <hr />

            </div>

            <button
                className="continue-shopping-btn"
                onClick={() => navigate("/books")}
            >
                Continue Shopping
            </button>
        </div>
    );
}
