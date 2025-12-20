--Bookstore with GenAI-Assisted Search & Recommendations--
A full-stack web application built with React and Spring Boot that allows users to browse, search, and purchase books. The application integrates a GenAI-assisted search and recommendation feature that interprets natural-language queries to suggest relevant books.

--Project Overview--
This project demonstrates a modern full-stack architecture combining a React frontend with a Java Spring Boot backend. Users can explore books, manage a shopping cart, place orders, and receive AI-assisted recommendations based on their search intent.
The system emphasizes clean API design, scalable data modeling, and user-friendly interaction.

--Features--
Browse & search books by title, author, or description
GenAI-assisted search & recommendations using natural-language input
Shopping cart & checkout flow, including tax and shipping calculations
Order lifecycle management (cart → order → confirmation)
Inventory updates after purchase
RESTful backend APIs with persistent storage

--Technology Stack--
-Frontend
React
JavaScript
HTML, CSS
-Backend
Java
Spring Boot
RESTful APIs
JPA / Hibernate
Database
MySQL
AI Integration
Ollama (local GenAI model for search & recommendations)
Tools
Git
JSON
REST APIs

--Installation & Setup--
1. Clone the repository
git clone https://github.com/underScoreSpace/bookstore.git
cd bookstore
2. Backend Setup (Spring Boot)
cd backend
./mvnw spring-boot:run
Backend will run at:
http://localhost:8080
Ensure MySQL is running and database credentials are configured in application.properties.
3. Frontend Setup (React)
cd frontend
npm install
npm start
Frontend will run at:
http://localhost:3000
4. GenAI Setup (Optional)
Ensure Ollama is installed and running locally with a supported model:
ollama serve
The backend communicates with Ollama to interpret user queries and generate book recommendations.

--How It Works--
User enters a natural-language query (e.g., “books about software architecture”)
Query is sent to the backend
GenAI interprets intent and returns relevant book suggestions
Backend responds with ranked results
Frontend displays recommended books in the UI

--What This Project Demonstrates--
Full-stack development with React + Spring Boot
REST API design and data modeling
AI integration into a real user workflow
Cart, checkout, and inventory management logic
Clean separation of frontend and backend concerns

--Future Improvements--
User authentication & roles
Advanced filtering and sorting
Deployment to cloud infrastructure
Caching and performance optimization
Enhanced AI personalization per user
