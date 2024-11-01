package com.oli.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oli.entity.Currency;
import com.oli.repository.impl.CurrencyRepository;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "CurrencyServlet", value = "/currency/*")
public class CurrencyServlet extends HttpServlet {

    private CurrencyRepository currencyRepository;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        currencyRepository = (CurrencyRepository) servletConfig.getServletContext()
                .getAttribute("currencyRepository");
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

        String code = pathInfo.replaceFirst("/", "").toUpperCase();
        Optional<Currency> currency = currencyRepository.findByCode(code);

        if (currency.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Currency with code " + code + " wasn't found.");
            return;
        }

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), currency.get());
    }
}
