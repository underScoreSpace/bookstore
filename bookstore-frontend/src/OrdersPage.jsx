import { useEffect, useState } from "react";
import "./OrdersPage.css";

export default function OrdersPage() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    const currentUser = JSON.parse(localStorage.getItem("currentUser") || "null");

    function formatDate(dateString) {
        const date = new Date(dateString);

        return date.toLocaleString("en-US", {
            year: "numeric",
            month: "long",
            day: "numeric",
            hour: "numeric",
            minute: "2-digit",
        });
    }


    useEffect(() => {
        if (!currentUser) return;

        fetch(`http://localhost:8080/api/orders/history/${currentUser.id}`)
            .then(res => res.json())
            .then(data => {
                setOrders(data);
                setLoading(false);
            })
            .catch(() => setLoading(false));
    }, []);

    if (!currentUser) {
        return <p style={{ textAlign: "center" }}>Please sign in to view your orders.</p>;
    }

    if (loading) return <p>Loading...</p>;

    return (
        <div className="orders-page">
            <h1>Your Orders</h1>

            {orders.length === 0 ? (
                <p>No orders yet.</p>
            ) : (
                orders.map(order => (
                    <div className="order-card" key={order.id}>
                        <h3>Order #{order.orderNumber}</h3>
                        <p>Total: ${order.total}</p>
                        <p>Placed: {formatDate(order.placedAt)}</p>

                        <h4>Items:</h4>
                        <ul>
                            {order.items.map(item => (
                                <li key={item.id}>
                                    {item.title} — {item.quantity} × ${item.unitPrice}
                                </li>
                            ))}
                        </ul>
                    </div>
                ))
            )}
        </div>
    );
}
