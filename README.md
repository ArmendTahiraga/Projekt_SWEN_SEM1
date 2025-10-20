# Media Ratings Platform (MRP)

The **Media Ratings Platform** is a standalone Java REST API that allows users to manage media (movies, series, and games), rate them, and receive personalized recommendations.  
It’s built with pure Java using `com.sun.net.httpserver.HttpServer`, PostgreSQL for persistence, and token-based authentication for security.

---

##  How It Works

1. The server runs locally on **port 8080**.
2. Users can **register**, **log in**, and **retrieve/update** their profile.
3. Each logged-in user receives a **token** that must be included in the `Authorization` header.
4. Endpoints (current stage):
- `POST /api/users/register` → Register new user
- `POST /api/users/login` → Log in and get token
- `GET /api/users/{id}/profile` → Get profile info
- `PUT /api/users/{id}/profile` → Update profile 
5. The server runs on `http://localhost:8080`

---

Author: Armend Tahiraga