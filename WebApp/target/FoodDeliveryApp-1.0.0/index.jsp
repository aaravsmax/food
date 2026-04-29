<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Food Delivery Order Management - Home</title>
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
        <!-- Success/Error Messages -->
        <%
            String success = request.getParameter("success");
            String error = request.getParameter("error");
            
            if (success != null && !success.isEmpty()) {
        %>
            <div class="alert alert-success">
                ✅ <%= success %>
            </div>
        <%
            }
            if (error != null && !error.isEmpty()) {
        %>
            <div class="alert alert-danger">
                ❌ <%= error %>
            </div>
        <%
            }
        %>

        <!-- Add Order Form Card -->
        <div class="card">
            <h2 class="card-title">📝 Enter New Order Details</h2>
            
            <form method="post" action="addOrder" id="orderForm">
                <div class="form-row">
                    <div class="form-group">
                        <label for="orderId">📌 Order ID:</label>
                        <input type="text" id="orderId" name="orderId" placeholder="e.g., ORD001" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="customerName">👤 Customer Name:</label>
                        <input type="text" id="customerName" name="customerName" placeholder="e.g., John Doe" required>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="restaurant">🏪 Restaurant:</label>
                        <input type="text" id="restaurant" name="restaurant" placeholder="e.g., Dominoes" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="amount">💰 Order Amount (₹):</label>
                        <input type="number" id="amount" name="amount" placeholder="e.g., 450.50" step="0.01" min="0" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="status">🚚 Current Status:</label>
                    <select id="status" name="status" required>
                        <option value="">-- Select Status --</option>
                        <option value="Pending">Pending</option>
                        <option value="Preparing">Preparing</option>
                        <option value="On the Way">On the Way</option>
                        <option value="Delivered">Delivered</option>
                        <option value="Cancelled">Cancelled</option>
                    </select>
                </div>

                <div>
                    <button type="submit" class="btn btn-primary">➕ Add Order</button>
                    <button type="reset" class="btn btn-warning">🔄 Clear Form</button>
                </div>
            </form>
        </div>

        <!-- Quick Links Card -->
        <div class="card">
            <h2 class="card-title">🎯 Quick Actions</h2>
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
                <a href="viewOrders" class="btn btn-primary">📋 View All Orders</a>
                <a href="premiumOrders" class="btn btn-premium">💎 Premium Orders (>₹1000)</a>
                <a href="sortOrders" class="btn btn-info" style="background-color: #3498db; color: white;">📉 Sort by Price</a>
                <a href="dashboard" class="btn btn-success">📊 View Dashboard</a>
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
