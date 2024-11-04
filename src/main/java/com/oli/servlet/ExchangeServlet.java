package com.oli.servlet;

import com.oli.dto.ConvertedAmount;
import com.oli.exception.ApplicationException;
import com.oli.service.ExchangeRateService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static com.oli.exception.ExceptionHandler.handleException;
import static com.oli.utils.JsonUtils.writeJsonToResponse;
import static com.oli.utils.RequestUtils.getRequestExchangeParameters;

@WebServlet(name = "ExchangeServlet", value = "/exchange")
public class ExchangeServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        exchangeRateService = (ExchangeRateService) servletConfig.getServletContext()
                .getAttribute("exchangeRateService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ConvertedAmount convertedAmount = null;
        try {
            List<Object> parameters = getRequestExchangeParameters(request);

            convertedAmount = exchangeRateService.calculateExchangeRateResponse(parameters);
        } catch (ApplicationException e) {
            handleException(response, e);
        }

        writeJsonToResponse(response, convertedAmount);
    }
}
