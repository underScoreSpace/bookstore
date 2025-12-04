import { Link, Routes, Route, Navigate } from "react-router-dom";
import { useState, createContext, useContext, useEffect } from "react";
import "./App.css";
import HomePage from "./HomePage.jsx";
import AboutPage from "./AboutPage.jsx";
import ContactPage from "./ContactPage.jsx";
import BookPage from "./BooksPage.jsx";
import CartPage from "./CartPage.jsx";
import CheckoutPage from "./CheckoutPage.jsx";
import BookDetailPage from "./BookDetailPage.jsx";
import OrderConfirmation from "./OrderConfirmation";
import OrdersPage from "./OrdersPage.jsx";


// Create Cart Context
const CartContext = createContext();

// Export useCart hook for other components to use
export function useCart() {
    const context = useContext(CartContext);
    if (!context) {
        throw new Error("useCart must be used within CartProvider");
    }
    return context;
}

export default function App() {
    const [activeModal, setActiveModal] = useState(null);

    // Cart state
    const [cart, setCart] = useState([]);

    // Logged-in user
    const [currentUser, setCurrentUser] = useState(() => {
        const saved = localStorage.getItem("currentUser");
        return saved ? JSON.parse(saved) : null;
    });

    const [loginData, setLoginData] = useState({
        email: "",
        password: ""
    });

    const [loginMessage, setLoginMessage] = useState("");

    const [signupData, setSignupData] = useState({
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        confirmPassword: ""
    });
    const [signupMessage, setSignupMessage] = useState("");

    const mapBackendCart = (items) =>
        items.map((item) => ({
            id: item.bookId,
            title: item.title,
            author: item.author,
            price: typeof item.price === "number" ? item.price : Number(item.price),
            quantity: item.quantity,
        }));

    const loadCartFromBackend = async (userId) => {
        try {
            const res = await fetch(`http://localhost:8080/api/cart/${userId}`);
            if (!res.ok) {
                console.error("Failed to load cart for user", userId);
                setCart([]);
                return;
            }
            const backendItems = await res.json();
            setCart(mapBackendCart(backendItems));
        } catch (err) {
            console.error("Error loading cart:", err);
            setCart([]);
        }
    };

    // When currentUser changes: load their cart, or clear if logged out
    useEffect(() => {
        if (currentUser && currentUser.id != null) {
            loadCartFromBackend(currentUser.id);
        } else {
            setCart([]);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [currentUser]);

    // ---- Cart functions ----

    // Add to cart function
    const addToCart = async (book) => {
        if (!currentUser) {
            setActiveModal("signin");
            return;
        }

        try {
            const res = await fetch("http://localhost:8080/api/cart/add", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    userId: currentUser.id,
                    bookId: book.id,
                    quantity: 1,
                }),
            });

            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg || "Failed to update cart");
            }

            const backendItems = await res.json();
            setCart(mapBackendCart(backendItems));

            alert(`${book.title} added to cart!`);
        } catch (err) {
            console.error("Error adding to cart:", err);
            alert("Could not add to cart. Please try again.");
        }
    };

    // Remove from cart
    const removeFromCart = async (bookId) => {
        if (!currentUser) return;

        try {
            const res = await fetch("http://localhost:8080/api/cart/update", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    userId: currentUser.id,
                    bookId: bookId,
                    quantity: 0,
                }),
            });

            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg || "Failed to update cart");
            }

            const backendItems = await res.json();
            setCart(mapBackendCart(backendItems));
        } catch (err) {
            console.error("Error removing from cart:", err);
        }
    };

    // Update quantity
    const updateQuantity = async (bookId, newQuantity) => {
        if (!currentUser) return;

        try {
            const res = await fetch("http://localhost:8080/api/cart/update", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    userId: currentUser.id,
                    bookId: bookId,
                    quantity: newQuantity,
                }),
            });

            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg || "Failed to update cart");
            }

            const backendItems = await res.json();
            setCart(mapBackendCart(backendItems));
        } catch (err) {
            console.error("Error updating quantity:", err);
        }
    };

    // Clear cart in UI when logging out
    const clearCart = () => {
        setCart([]);
    };

    const cartValue = {
        cart,
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
        cartCount: cart.reduce((total, item) => total + item.quantity, 0),
        cartTotal: cart.reduce(
            (total, item) => total + item.price * item.quantity,
            0
        ),
    };

    // ---- Signup / Login handlers ----

    const handleSignupChange = (e) => {
        const { name, value } = e.target;
        setSignupData({ ...signupData, [name]: value });
    };

    const handleSignupSubmit = async () => {
        const { firstName, lastName, email, password, confirmPassword } =
            signupData;

        if (!email || !password || !confirmPassword) {
            setSignupMessage("Please fill out all required fields.");
            return;
        }
        if (password !== confirmPassword) {
            setSignupMessage("Passwords do not match.");
            return;
        }

        try {
            const res = await fetch("http://localhost:8080/api/users/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    firstName,
                    lastName,
                    email,
                    passwordHash: password,
                }),
            });

            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg || "Failed to create account");
            }

            const msg = await res.text();
            setSignupMessage(`${msg}`);
            setTimeout(() => {
                setActiveModal(null);
                setSignupMessage("");
                setSignupData({
                    firstName: "",
                    lastName: "",
                    email: "",
                    password: "",
                    confirmPassword: "",
                });
            }, 1500);
        } catch (err) {
            setSignupMessage(`${err.message}`);
        }
    };

    const handleLoginChange = (e) => {
        const { name, value } = e.target;
        setLoginData({ ...loginData, [name]: value });
    };

    const handleLoginSubmit = async () => {
        const { email, password } = loginData;

        if (!email || !password) {
            setLoginMessage("Please enter your email and password.");
            return;
        }

        try {
            const res = await fetch("http://localhost:8080/api/users/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password }),
            });

            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg || "Login failed");
            }

            const user = await res.json(); // LoginResponse from backend

            setCurrentUser(user);
            localStorage.setItem("currentUser", JSON.stringify(user));
            setLoginMessage("Logged in successfully!");

            if (user.id != null) {
                await loadCartFromBackend(user.id);
            }

            setTimeout(() => {
                setActiveModal(null);
                setLoginMessage("");
                setLoginData({ email: "", password: "" });
            }, 1000);
        } catch (err) {
            setLoginMessage(err.message);
        }
    };

    const handleSignOut = () => {
        setCurrentUser(null);
        localStorage.removeItem("currentUser");
        setCart([]);
    };

    return (
        <CartContext.Provider value={cartValue}>
            <div>
                <nav>
                    <div className="nav-inner">
                        <Link className="navbar-brand" to="/">
                            Bookstore
                        </Link>

                        <ul>
                            <li>
                                <Link to="/about">About Us</Link>
                            </li>
                            <li>
                                <Link to="/contact">Contact</Link>
                            </li>
                            <li>
                                <Link to="/books">Books</Link>
                            </li>
                        </ul>

                        <div className="nav-right">
                            <Link to="/cart" className="cart-link-wrapper">
                                <div className="cart-link">
                                    🛒 Cart ({cartValue.cartCount})
                                </div>
                            </Link>

                            {currentUser && (
                                <Link to="/orders" className="orders-link">
                                    Orders
                                </Link>
                            )}

                            {currentUser ? (
                                <div className="auth-area">
            <span className="user-greeting">
                Hi, {currentUser.firstName || currentUser.email}
            </span>
                                    <button
                                        className="auth-button"
                                        onClick={handleSignOut}
                                    >
                                        Sign Out
                                    </button>
                                </div>
                            ) : (
                                <div className="auth-area">
                                    <button
                                        className="auth-button"
                                        onClick={() => {
                                            setLoginMessage("");
                                            setActiveModal("signin");
                                        }}
                                    >
                                        Sign In
                                    </button>
                                    <button
                                        className="auth-button"
                                        onClick={() => {
                                            setSignupMessage("");
                                            setActiveModal("signup");
                                        }}
                                    >
                                        Sign Up
                                    </button>
                                </div>
                            )}
                        </div>
                    </div>
                </nav>

                <main>
                    <Routes>
                        <Route path="/" element={<HomePage />} />
                        <Route path="/about" element={<AboutPage />} />
                        <Route path="/contact" element={<ContactPage />} />
                        <Route path="/books" element={<BookPage />} />
                        <Route path="/orders" element={<OrdersPage />} />
                        <Route path="/order-confirmation" element={<OrderConfirmation />} />
                        <Route
                            path="/books/:id"
                            element={<BookDetailPage openSignIn={() => setActiveModal("signin")} />}
                        />
                        <Route
                            path="/cart"
                            element={<CartPage openSignIn={() => setActiveModal("signin")} />}
                        />
                        <Route
                            path="/checkout"
                            element={<CheckoutPage openSignIn={() => setActiveModal("signin")} />}
                        />
                        <Route path="*" element={<Navigate to="/" replace />} />
                    </Routes>
                </main>

                {/* Sign In Modal */}
                {activeModal === "signin" && (
                    <div
                        className="modal-overlay"
                        onClick={() => setActiveModal(null)}
                    >
                        <div
                            className="modal-content"
                            onClick={(e) => e.stopPropagation()}
                        >
                            <h2>Sign In</h2>
                            <input
                                type="email"
                                name="email"
                                placeholder="Email"
                                value={loginData.email}
                                onChange={handleLoginChange}
                            />
                            <input
                                type="password"
                                name="password"
                                placeholder="Password"
                                value={loginData.password}
                                onChange={handleLoginChange}
                            />
                            {loginMessage && (
                                <p className="modal-message">
                                    {loginMessage}
                                </p>
                            )}
                            <button onClick={handleLoginSubmit}>
                                Sign In
                            </button>

                            <p className="switch-text">
                                Don&apos;t have an account?{" "}
                                <button
                                    type="button"
                                    className="link-button"
                                    onClick={() => {
                                        setLoginMessage("");
                                        setActiveModal("signup");
                                    }}
                                >
                                    Create one
                                </button>
                            </p>
                        </div>
                    </div>
                )}

                {/* Sign Up Modal */}
                {activeModal === "signup" && (
                    <div
                        className="modal-overlay"
                        onClick={() => setActiveModal(null)}
                    >
                        <div
                            className="modal-content"
                            onClick={(e) => e.stopPropagation()}
                        >
                            <h2>Create an Account</h2>
                            <input
                                type="text"
                                name="firstName"
                                placeholder="First Name"
                                value={signupData.firstName}
                                onChange={handleSignupChange}
                            />
                            <input
                                type="text"
                                name="lastName"
                                placeholder="Last Name"
                                value={signupData.lastName}
                                onChange={handleSignupChange}
                            />
                            <input
                                type="email"
                                name="email"
                                placeholder="Email"
                                value={signupData.email}
                                onChange={handleSignupChange}
                            />
                            <input
                                type="password"
                                name="password"
                                placeholder="Password"
                                value={signupData.password}
                                onChange={handleSignupChange}
                            />
                            <input
                                type="password"
                                name="confirmPassword"
                                placeholder="Re-enter Password"
                                value={signupData.confirmPassword}
                                onChange={handleSignupChange}
                            />

                            {signupMessage && (
                                <p className="modal-message">
                                    {signupMessage}
                                </p>
                            )}

                            <button onClick={handleSignupSubmit}>
                                Create Account
                            </button>

                            <p className="switch-text">
                                Already have an account?{" "}
                                <button
                                    type="button"
                                    className="link-button"
                                    onClick={() => {
                                        setSignupMessage("");
                                        setActiveModal("signin");
                                    }}
                                >
                                    Sign in
                                </button>
                            </p>
                        </div>
                    </div>
                )}
            </div>
        </CartContext.Provider>
    );
}
