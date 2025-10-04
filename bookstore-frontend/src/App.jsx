import { Link, Routes, Route, Navigate } from "react-router-dom";
import { useState } from "react";
import "./App.css";
import HomePage from "./HomePage.jsx";
import AboutPage from "./AboutPage.jsx";
import ContactPage from "./ContactPage.jsx";

export default function App() {
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [activeModal, setActiveModal] = useState(null); // null, 'signin', or 'signup'

    return (
        <div>
            <nav>
                <div>
                    <Link className="navbar-brand" to="/">Bookstore</Link>
                    <ul>
                        <li><Link to="/about">About Us</Link></li>
                        <li><Link to="/contact">Contact</Link></li>
                    </ul>
                    <div
                        className="dropdown"
                        onMouseEnter={() => setIsDropdownOpen(true)}
                        onMouseLeave={() => setIsDropdownOpen(false)}
                    >
                        <span className="dropdown-trigger">
                            My Account
                        </span>
                        {isDropdownOpen && (
                            <div className="dropdown-menu">
                                <button onClick={() => setActiveModal('signin')}>
                                    Sign In
                                </button>
                                <button onClick={() => setActiveModal('signup')}>
                                    Create an Account
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
                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </main>

            {/* Sign In Modal */}
            {activeModal === 'signin' && (
                <div className="modal-overlay" onClick={() => setActiveModal(null)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <h2>Sign In</h2>
                        <input type="text" placeholder="Enter your email or username" />
                        <input type="text" placeholder="Enter your password" />
                        <button onClick={() => setActiveModal(null)}>Sign In</button>
                    </div>
                </div>
            )}

            {/* Sign Up Modal */}
            {activeModal === 'signup' && (
                <div className="modal-overlay" onClick={() => setActiveModal(null)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <h2>Create an Account</h2>
                        <input type="text" placeholder="Enter your email" />
                        <input type="text" placeholder="Enter your password" />
                        <input type="text" placeholder="Re-enter your password" />
                        <button onClick={() => setActiveModal(null)}>Create an Account</button>
                    </div>
                </div>
            )}
        </div>
    );
}