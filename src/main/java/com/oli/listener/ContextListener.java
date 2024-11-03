package com.oli.listener;

import com.oli.repository.impl.CurrencyRepository;
import com.oli.repository.impl.ExchangeRateRepository;
import com.oli.service.CurrencyService;
import com.oli.service.ExchangeRateService;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@WebListener
@NoArgsConstructor
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();

        CurrencyRepository currencyRepository = new CurrencyRepository(makeHikariDataSource(context));
        ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(makeHikariDataSource(context));

        context.setAttribute("currencyRepository", currencyRepository);
        context.setAttribute("exchangeRateRepository", exchangeRateRepository);

        CurrencyService currencyService = new CurrencyService(currencyRepository);
        ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateRepository);

        context.setAttribute("currencyService", currencyService);
        context.setAttribute("exchangeRateService", exchangeRateService);
    }

    private DataSource makeHikariDataSource(ServletContext context) {
        try {
            Properties properties = new Properties();
            HikariDataSource hikariDataSource = new HikariDataSource();

            properties.load(context.getResourceAsStream("WEB-INF/properties/db.properties"));

            hikariDataSource.setDriverClassName(properties.getProperty("db.driver.name"));
            hikariDataSource.setJdbcUrl(properties.getProperty("db.url"));
            hikariDataSource.setUsername(properties.getProperty("db.username"));
            hikariDataSource.setPassword(properties.getProperty("db.password"));

            return hikariDataSource;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
