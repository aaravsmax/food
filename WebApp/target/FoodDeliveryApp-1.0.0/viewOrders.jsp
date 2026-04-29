<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.fooddelivery.model.FoodOrder" %>
<%@ page import="com.fooddelivery.dao.OrderDAO" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Orders - Food Delivery Management</title>
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

        <!-- Orders Table Card -->
        <div class="card">
            <h2 class="card-title">
                <%= request.getAttribute("title") != null ? request.getAttribute("title") : "📋 All Orders" %>
            </h2>

            <%
                List<FoodOrder> orders = (List<FoodOrder>) request.getAttribute("orders");
                
                if (orders == null || orders.isEmpty()) {
            %>
                <div class="empty-state">
                    <h3>📭 No Orders Found</h3>
                    <p>Start by <a href="index.jsp" class="btn btn-primary">adding a new order</a></p>
                </div>
            <%
                } else {
            %>
                <div style="overflow-x: auto;">
                    <table>
                        <thead>
                            <tr>
                                <th>📌 Order ID</th>
                                <th>👤 Customer Name</th>
                                <th>🏪 Restaurant</th>
                                <th>💰 Amount (₹)</th>
                                <th>🚚 Status</th>
                                <th>⚙️ Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                for (FoodOrder order : orders) {
                                    // Determine badge color based on status
                                    String badgeClass = "badge-pending";
                                    if ("Delivered".equals(order.getStatus())) {
                                        badgeClass = "badge-delivered";
                                    } else if ("Preparing".equals(order.getStatus())) {
                                        badgeClass = "badge-pending";
                                    }
                            %>
                                <tr>
                                    <td><strong><%= order.getOrderId() %></strong></td>
                                    <td><%= order.getCustomerName() %></td>
                                    <td><%= order.getRestaurant() %></td>
                                    <td><strong>₹<%= String.format("%.2f", order.getAmount()) %></strong></td>
                                    <td>
                                        <span class="badge <%= badgeClass %>">
                                            <%= order.getStatus() %>
                                        </span>
                                    </td>
                                    <td>
                                        <form method="post" action="deleteOrder" style="display: inline;">
                                            <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
                                            <button type="submit" class="btn btn-danger btn-sm" 
                                                    onclick="return confirm('Are you sure you want to delete this order?');">
                                                🗑️ Delete
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </div>

                <!-- Statistics -->
                <div style="margin-top: 2rem; padding-top: 2rem; border-top: 1px solid #ecf0f1;">
                    <h3 style="color: var(--primary-color); margin-bottom: 1rem;">📊 Order Statistics</h3>
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
                        <div style="background: #f8f9fa; padding: 1rem; border-radius: 8px; text-align: center;">
                            <p style="color: #7f8c8d; margin-bottom: 0.5rem;">Total Orders</p>
                            <h3 style="color: var(--primary-color);"><%= orders.size() %></h3>
                        </div>
                        <div style="background: #f8f9fa; padding: 1rem; border-radius: 8px; text-align: center;">
                            <p style="color: #7f8c8d; margin-bottom: 0.5rem;">Total Revenue</p>
                            <h3 style="color: var(--success-color);">
                                ₹<%= String.format("%.2f", orders.stream().mapToDouble(FoodOrder::getAmount).sum()) %>
                            </h3>
                        </div>
                        <div style="background: #f8f9fa; padding: 1rem; border-radius: 8px; text-align: center;">
                            <p style="color: #7f8c8d; margin-bottom: 0.5rem;">Average Order Value</p>
                            <h3 style="color: var(--warning-color);">
                                ₹<%= String.format("%.2f", orders.stream().mapToDouble(FoodOrder::getAmount).average().orElse(0)) %>
                            </h3>
                        </div>
                    </div>
                </div>
            <%
                }
            %>
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
