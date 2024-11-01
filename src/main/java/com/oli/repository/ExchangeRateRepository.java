package com.oli.repository;

import com.oli.entity.ExchangeRate;
import com.oli.repository.abstr.CrudRepository;
import com.oli.repository.abstr.DataSourceRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ExchangeRateRepository extends DataSourceRepository implements CrudRepository<ExchangeRate> {

    public static final String INVALID_FOREIGN_KEY = "Invalid foreign key. " +
            "There is no currency with id that is specified for current exchange rate.";
    public static final String INVALID_CURRENCY_CODE = "Invalid currency code." +
            "There is no currency with code that is specified for current exchange rate.";

    private final CurrencyRepository currencyRepository;

    public ExchangeRateRepository(DataSource dataSource) {
        super(dataSource);
        currencyRepository = new CurrencyRepository(dataSource);
    }

    private ExchangeRate fromResultSet(ResultSet resultSet) throws SQLException {
        return ExchangeRate.builder()
                .id(resultSet.getLong("id"))
                .baseCurrencyId(
                        currencyRepository.findById(resultSet.getLong("base_currency_id"))
                                .orElseThrow(() -> new NoSuchElementException((INVALID_FOREIGN_KEY)))
                )
                .targetCurrencyId(
                        currencyRepository.findById(resultSet.getLong("target_currency_id"))
                                .orElseThrow(() -> new NoSuchElementException(INVALID_FOREIGN_KEY))
                )
                .rate(resultSet.getBigDecimal("rate"))
                .build();
    }

    @Override
    public List<ExchangeRate> findAll() {
        String query = "SELECT * FROM exchange_rate";
        List<ExchangeRate> res = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query))
        {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                res.add(fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    public Optional<ExchangeRate> findById(Long id) {
        String query = "SELECT * FROM exchange_rate WHERE id = " + id;
        Optional<ExchangeRate> optional = Optional.empty();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                optional = Optional.of(fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return optional;
    }

    public Optional<ExchangeRate> findByCodes(String baseCode, String targetCode) {
        String query = "SELECT er.* FROM exchange_rate er " +
                "INNER JOIN currency c_base " +
                    "ON er.base_currency_id = c_base.id " +
                "INNER JOIN currency c_target " +
                    "ON er.target_currency_id = c_target.id " +
                "WHERE c_base.code = '" + baseCode + "' " +
                "AND c_target.code = '" + targetCode + "'";
        Optional<ExchangeRate> optional = Optional.empty();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                optional = Optional.of(fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return optional;
    }

    @Override
    public ExchangeRate save(ExchangeRate obj) {
        String query = "INSERT INTO exchange_rate (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";
        ExchangeRate saved = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setLong(1, obj.getBaseCurrencyId().getId());
            preparedStatement.setLong(2, obj.getTargetCurrencyId().getId());
            preparedStatement.setBigDecimal(3, obj.getRate());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Saving exchange rate failed, no rows affected.");
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                Optional<ExchangeRate> optional = findById(generatedKeys.getLong(1));

                if (optional.isPresent()) {
                    saved = optional.get();
                } else {
                    throw new SQLException("Saving exchange rate failed, no exchange rate with retrieved ID found.");
                }
            } else {
                throw new SQLException("Saving exchange rate failed, no ID generated.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return saved;
    }

    public ExchangeRate update(ExchangeRate exchangeRate) {
        String query = "UPDATE exchange_rate " +
                "SET rate = " + exchangeRate.getRate() + " " +
                "WHERE id = " + exchangeRate.getId();
        ExchangeRate saved = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Saving exchange rate failed, no rows affected.");
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                Optional<ExchangeRate> optional = findById(generatedKeys.getLong(1));

                if (optional.isPresent()) {
                    saved = optional.get();
                } else {
                    throw new SQLException("Saving exchange rate failed, no exchange rate with retrieved ID found.");
                }
            } else {
                throw new SQLException("Saving exchange rate failed, no ID generated.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return saved;
    }
}
