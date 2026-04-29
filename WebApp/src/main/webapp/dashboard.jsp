<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fooddelivery.dao.OrderDAO" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Food Delivery Management</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <%
        // Check if user is logged in
        String username = null;
        
        if (session != null) {
            username = (String) session.getAttribute("user");
        }
        
        if (username == null) {
            response.sendRedirect("login.jsp");
            return;
        }
    %>

    <!-- Navbar -->
    <div class="navbar">
        <h1>🍔 Food Delivery Order Management System</h1>
        <div class="nav-links">
            <a href="index.jsp">➕ Add Order</a>
            <a href="viewOrders">📋 View All Orders</a>
            <a href="premiumOrders">💎 Premium Orders</a>
            <a href="sortOrders">📉 Sort by Price</a>
            <a href="dashboard">📊 Dashboard</a>
            <span style="color: white; margin: 0 1rem;">👤 <%= username %></span>
            <a href="logout" style="background-color: #e74c3c; padding: 0.5rem 1rem; border-radius: 5px;">🚪 Logout</a>
        </div>
    </div>

    <!-- Main Content -->
    <div class="container">
        <!-- Dashboard Title -->
        <div class="card">
            <h2 class="card-title">📊 Business Dashboard</h2>
            <p style="color: #7f8c8d;">Real-time statistics and insights about your food delivery orders</p>
        </div>

        <!-- Statistics Cards -->
        <div class="stats-grid">
            <div class="stat-card">
                <h3>📦 Total Orders</h3>
                <div class="stat-value">
                    <%= request.getAttribute("totalOrders") != null ? request.getAttribute("totalOrders") : "0" %>
                </div>
            </div>

            <div class="stat-card" style="background: linear-gradient(135deg, #27ae60 0%, #229954 100%);">
                <h3>💰 Total Revenue</h3>
                <div class="stat-value">
                    ₹<%= request.getAttribute("totalRevenue") != null ? 
                        String.format("%.2f", request.getAttribute("totalRevenue")) : "0.00" %>
                </div>
            </div>

            <div class="stat-card" style="background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%);">
                <h3>📈 Average Order Value</h3>
                <div class="stat-value">
                    <%
                        int totalOrders = (Integer) request.getAttribute("totalOrders");
                        double totalRevenue = (Double) request.getAttribute("totalRevenue");
                        double avgOrder = totalOrders > 0 ? totalRevenue / totalOrders : 0;
                    %>
                    ₹<%= String.format("%.2f", avgOrder) %>
                </div>
            </div>

            <div class="stat-card" style="background: linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%);">
                <h3>💎 Premium Orders (>₹1000)</h3>
                <div class="stat-value">
                    <%= OrderDAO.getPremiumOrders().size() %>
                </div>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="card">
            <h2 class="card-title">🎯 Quick Actions</h2>
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
                <a href="index.jsp" class="btn btn-primary" style="font-size: 1.1rem; padding: 1rem;">
                    ➕ Add New Order
                </a>
                <a href="viewOrders" class="btn btn-primary" style="font-size: 1.1rem; padding: 1rem;">
                    📋 View All Orders
                </a>
                <a href="premiumOrders" class="btn btn-premium" style="font-size: 1.1rem; padding: 1rem;">
                    💎 Premium Orders
                </a>
                <a href="sortOrders" class="btn btn-info" style="font-size: 1.1rem; padding: 1rem; background-color: #3498db; color: white; text-decoration: none; border-radius: 5px; text-align: center; font-weight: bold;">
                    📉 Sort by Price
                </a>
                <a href="viewOrders" class="btn btn-success" style="font-size: 1.1rem; padding: 1rem;">
                    🔄 Refresh Stats
                </a>
            </div>
        </div>

        <!-- Summary Card -->
        <div class="card">
            <h2 class="card-title">📋 System Summary</h2>
            <div style="background: #f8f9fa; padding: 2rem; border-radius: 8px; line-height: 2;">
                <p>
                    <strong>Total Orders in System:</strong> 
                    <span style="color: var(--primary-color); font-size: 1.2rem;">
                        <%= request.getAttribute("totalOrders") %>
                    </span>
                </p>
                <p>
                    <strong>Total Revenue Generated:</strong> 
                    <span style="color: var(--success-color); font-size: 1.2rem;">
                        ₹<%= String.format("%.2f", request.getAttribute("totalRevenue")) %>
                    </span>
                </p>
                <p>
                    <strong>Premium Orders (Amount > ₹1000):</strong> 
                    <span style="color: var(--premium-color); font-size: 1.2rem;">
                        <%= OrderDAO.getPremiumOrders().size() %>
                    </span>
                </p>
                <hr style="margin: 1rem 0; border: none; border-top: 2px solid #ecf0f1;">
                <p style="color: #7f8c8d;">
                    Last updated: <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()) %>
                </p>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer>
        <p>&copy; 2024 Food Delivery Order Management System | All Rights Reserved</p>
        <p>Built with Java Servlets, JSP, MySQL & Bootstrap</p>
    </footer>

    <script src="js/main.js"></script>
</body>
</html>
