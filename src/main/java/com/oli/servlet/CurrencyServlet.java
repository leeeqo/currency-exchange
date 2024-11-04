package com.oli.servlet;

import com.oli.entity.Currency;
import com.oli.exception.ApplicationException;
import com.oli.service.CurrencyService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.oli.exception.ExceptionHandler.handleException;
import static com.oli.utils.JsonUtils.writeJsonToResponse;
import static com.oli.utils.RequestUtils.getRequestCurrencyCode;

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
            throws ServletException, IOException {

        Currency currency = null;
        try {
            String code = getRequestCurrencyCode(request);

            currency = currencyService.getCurrencyByCode(code);
        } catch (ApplicationException e) {
            handleException(response, e);
        }

        writeJsonToResponse(response, currency);
    }
}
