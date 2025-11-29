import "./HomePage.css"
import {Link} from "react-router-dom";

export default function HomePage() {
    return (
        <header>
            <h1 className="welcome-sentence">Welcome to the Bookstore</h1>
            <p>Your favorite shop for books online.</p>
            <Link to="/books">
                <button className="browse-button">Browse Books</button>
            </Link>
        </header>
    );
}
