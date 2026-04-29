package com.fooddelivery.servlet;

import com.fooddelivery.dao.OrderDAO;
import com.fooddelivery.model.FoodOrder;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * SortOrdersServlet - Displays orders sorted by price
 * Maps to: /sortOrders
 */
@WebServlet("/sortOrders")
public class SortOrdersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get orders from database sorted by price
            List<FoodOrder> orders = OrderDAO.getOrdersSortedByPrice();

            // Store in request attribute
            request.setAttribute("orders", orders);
            request.setAttribute("title", "📉 Orders Sorted by Price (High to Low)");

            // Forward to JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("viewOrders.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp?message=Error retrieving sorted orders");
        }
    }
}
