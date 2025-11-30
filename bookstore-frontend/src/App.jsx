import { Link, Routes, Route, Navigate } from "react-router-dom";
import { useState, createContext, useContext } from "react";
import "./App.css";
import HomePage from "./HomePage.jsx";
import AboutPage from "./AboutPage.jsx";
import ContactPage from "./ContactPage.jsx";
import BookPage from "./BooksPage.jsx";
import CartPage from "./CartPage.jsx";
import BookDetailPage from "./BookDetailPage.jsx";

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
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [activeModal, setActiveModal] = useState(null);
    
    // Cart state
    const [cart, setCart] = useState([]);

    const [signupData, setSignupData] = useState({
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        confirmPassword: ""
    });
    const [signupMessage, setSignupMessage] = useState("");

    // Add to cart function
    const addToCart = (book) => {
        setCart((prevCart) => {
            const existingItem = prevCart.find(item => item.id === book.id);
            
            if (existingItem) {
                // If book already in cart, increase quantity
                return prevCart.map(item =>
                    item.id === book.id
                        ? { ...item, quantity: item.quantity + 1 }
                        : item
                );
            } else {
                // Add new book to cart
                return [...prevCart, { ...book, quantity: 1 }];
            }
        });
        
        // Optional: Show a success message
        alert(`${book.title} added to cart!`);
    };

    // Remove from cart function
    const removeFromCart = (bookId) => {
        setCart((prevCart) => prevCart.filter(item => item.id !== bookId));
    };

    // Update quantity function
    const updateQuantity = (bookId, newQuantity) => {
        if (newQuantity <= 0) {
            removeFromCart(bookId);
        } else {
            setCart((prevCart) =>
                prevCart.map(item =>
                    item.id === bookId
                        ? { ...item, quantity: newQuantity }
                        : item
                )
            );
        }
    };

    // Clear cart function
    const clearCart = () => {
        setCart([]);
    };

    // Cart value to provide to children
    const cartValue = {
        cart,
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
        cartCount: cart.reduce((total, item) => total + item.quantity, 0),
        cartTotal: cart.reduce((total, item) => total + (item.price * item.quantity), 0)
    };

    const handleSignupChange = (e) => {
        const { name, value } = e.target;
        setSignupData({ ...signupData, [name]: value });
    };

    const handleSignupSubmit = async () => {
        const { firstName, lastName, email, password, confirmPassword } = signupData;

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
                    passwordHash: password
                })
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
                    confirmPassword: ""
                });
            }, 1500);
        } catch (err) {
            setSignupMessage(`${err.message}`);
        }
    };

    return (
        <CartContext.Provider value={cartValue}>
            <div>
                <nav>
                    <div>
                        <Link className="navbar-brand" to="/">Bookstore</Link>
                        <ul>
                            <li><Link to="/about">About Us</Link></li>
                            <li><Link to="/contact">Contact</Link></li>
                            <li><Link to="/books">Books</Link></li>
                        </ul>
                        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                            <Link to="/cart" style={{ textDecoration: 'none' }}>
                                <div style={{ fontWeight: 'bold', color: 'black', cursor: 'pointer' }}>
                                    🛒 Cart ({cartValue.cartCount})
                                </div>
                            </Link>
                            <div className="dropdown"
                                 onMouseEnter={() => setIsDropdownOpen(true)}
                                 onMouseLeave={() => setIsDropdownOpen(false)}
                            >
                                <span className="dropdown-trigger">My Account</span>
                                {isDropdownOpen && (
                                    <div className="dropdown-menu">
                                        <button onClick={() => setActiveModal('signin')}>Sign In</button>
                                        <button onClick={() => setActiveModal('signup')}>Create an Account</button>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </nav>

                <main>
                    <Routes>
                        <Route path="/" element={<HomePage />} />
                        <Route path="/about" element={<AboutPage />} />
                        <Route path="/contact" element={<ContactPage />} />
                        <Route path="/books" element={<BookPage />} />
                        <Route path="/books/:id" element={<BookDetailPage />} />
                        <Route path="/cart" element={<CartPage />} />
                        <Route path="*" element={<Navigate to="/" replace />} />
                    </Routes>
                </main>

                {/* Sign In Modal */}
                {activeModal === 'signin' && (
                    <div className="modal-overlay" onClick={() => setActiveModal(null)}>
                        <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                            <h2>Sign In</h2>
                            <input type="text" placeholder="Enter your email or username" />
                            <input type="password" placeholder="Enter your password" />
                            <button onClick={() => setActiveModal(null)}>Sign In</button>
                        </div>
                    </div>
                )}

                {/* Sign Up Modal */}
                {activeModal === "signup" && (
                    <div className="modal-overlay" onClick={() => setActiveModal(null)}>
                        <div className="modal-content" onClick={(e) => e.stopPropagation()}>
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
                                <p style={{marginTop: "10px"}}>{signupMessage}</p>
                            )}

                            <button onClick={handleSignupSubmit}>Create Account</button>
                        </div>
                    </div>
                )}
            </div>
        </CartContext.Provider>
    );
}