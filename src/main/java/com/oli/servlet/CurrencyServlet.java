package com.oli.servlet;

import com.oli.entity.Currency;
import com.oli.service.CurrencyService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

import static com.oli.utils.JsonUtils.writeJsonToResponse;

@WebServlet(name = "CurrencyServlet", value = "/currency/*")
public class CurrencyServlet extends HttpServlet {

    private CurrencyService currencyService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        currencyService = (CurrencyService) servletConfig.getServletContext()
                .getAttribute("currencyService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Enter currency code. Ex.: .../currency/USD");
            return;
        }

        String code = pathInfo.replaceFirst("/", "");

        Currency currency = null;
        try {
            currency = currencyService.getCurrencyByCode(code);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        writeJsonToResponse(response, currency);
    }
}
