package com.oli.repository.impl;

import com.oli.entity.Currency;
import com.oli.entity.ExchangeRate;
import com.oli.exception.impl.AlreadyExistsException;
import com.oli.exception.impl.NotFoundException;
import com.oli.repository.CrRepository;
import com.oli.repository.DataSourceRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateRepository extends DataSourceRepository implements CrRepository<ExchangeRate> {

    private static final String BASE_CURRENCY_FIELDS = "c_base.id AS base_id, c_base.code AS base_code, " +
            "c_base.fullname AS base_fullname, c_base.sign AS base_sign";
    private static final String TARGET_CURRENCY_FIELDS = "c_target.id AS target_id, c_target.code AS target_code, " +
            "c_target.fullname AS target_fullname, c_target.sign AS target_sign";

    private static final String UNIQUE_CONSTRAINT_VIOLATION =
            "violates unique constraint \"exchange_rate_base_currency_id_target_currency_id_key\"";
    private static final String NOT_NULL_CONSTRAINT_VIOLATION_BASE =
            "null value in column \"base_currency_id\" of relation \"exchange_rate\" violates not-null constraint";
    private static final String NOT_NULL_CONSTRAINT_VIOLATION_TARGET =
            "null value in column \"target_currency_id\" of relation \"exchange_rate\" violates not-null constraint";
    private static final String NO_ROWS_AFFECTED = "No rows affected.";

    public ExchangeRateRepository(DataSource dataSource) {
        super(dataSource);
    }

    private ExchangeRate fromResultSet(ResultSet resultSet) throws SQLException {
        return ExchangeRate.builder()
                .id(resultSet.getLong("id"))
                .baseCurrency(Currency.builder()
                        .id(resultSet.getLong("base_id"))
                        .code(resultSet.getString("base_code"))
                        .fullName(resultSet.getString("base_fullname"))
                        .sign(resultSet.getString("base_sign"))
                        .build()
                )
                .targetCurrency(Currency.builder()
                        .id(resultSet.getLong("target_id"))
                        .code(resultSet.getString("target_code"))
                        .fullName(resultSet.getString("target_fullname"))
                        .sign(resultSet.getString("target_sign"))
                        .build()
                )
                .rate(resultSet.getBigDecimal("rate"))
                .build();
    }

    @Override
    public List<ExchangeRate> findAll() {
        String query =
                "SELECT er.id, er.rate, " + BASE_CURRENCY_FIELDS + ", " + TARGET_CURRENCY_FIELDS + " " +
                "FROM exchange_rate er " +
                "INNER JOIN currency c_base " +
                    "ON er.base_currency_id = c_base.id " +
                "INNER JOIN currency c_target " +
                    "ON er.target_currency_id = c_target.id";

        List<ExchangeRate> res = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query))
        {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                res.add(fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    @Override
    public Optional<ExchangeRate> findById(Long id) {
        String query =
                "SELECT er.id, er.rate, " + BASE_CURRENCY_FIELDS + ", " + TARGET_CURRENCY_FIELDS + " " +
                "FROM exchange_rate er " +
                "INNER JOIN currency c_base " +
                    "ON er.base_currency_id = c_base.id " +
                "INNER JOIN currency c_target " +
                    "ON er.target_currency_id = c_target.id " +
                "WHERE er.id = ?";

        Optional<ExchangeRate> optional = Optional.empty();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                optional = Optional.of(fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return optional;
    }

    public Optional<ExchangeRate> findByCodes(String baseCode, String targetCode) {
        String query =
                "SELECT er.id, er.rate, " + BASE_CURRENCY_FIELDS + ", " + TARGET_CURRENCY_FIELDS + " " +
                "FROM exchange_rate er " +
                "INNER JOIN currency c_base " +
                    "ON er.base_currency_id = c_base.id " +
                "INNER JOIN currency c_target " +
                    "ON er.target_currency_id = c_target.id  " +
                "WHERE c_base.code = ? " +
                "AND c_target.code = ?";

        Optional<ExchangeRate> optional = Optional.empty();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, baseCode);
            preparedStatement.setString(2, targetCode);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                optional = Optional.of(fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return optional;
    }

    public ExchangeRate save(ExchangeRate exchangeRate) {
        String query =
                "INSERT INTO exchange_rate (base_currency_id, target_currency_id, rate) " +
                "VALUES (" +
                    "(SELECT id FROM currency WHERE code = ?), " +
                    "(SELECT id FROM currency WHERE code = ?), " +
                    "?" +
                ")";

        ExchangeRate saved = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, exchangeRate.getBaseCurrency().getCode());
            preparedStatement.setString(2, exchangeRate.getTargetCurrency().getCode());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException();
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                Optional<ExchangeRate> optional = findById(generatedKeys.getLong(1));

                saved = optional.orElseThrow(() ->
                        new SQLException("Saving exchange rate failed, no exchange rate with retrieved ID found."));
            }
        } catch (SQLException e) {
            if (e.getMessage().contains(NOT_NULL_CONSTRAINT_VIOLATION_BASE)) {
                throw new NotFoundException(
                        "Currency with code " + exchangeRate.getBaseCurrency().getCode() + " was not found.");
            }

            if (e.getMessage().contains(NOT_NULL_CONSTRAINT_VIOLATION_TARGET)) {
                throw new NotFoundException(
                        "Currency with code " + exchangeRate.getTargetCurrency().getCode() + " was not found.");
            }

            if (e.getMessage().contains(UNIQUE_CONSTRAINT_VIOLATION)) {
                throw new AlreadyExistsException("Exchange rate from " +
                        exchangeRate.getBaseCurrency().getCode() + " to " +
                        exchangeRate.getTargetCurrency().getCode() + " already exists.");
            }

            throw new RuntimeException(e);
        }

        return saved;
    }

    public ExchangeRate update(ExchangeRate exchangeRate) {
        String query =
                "UPDATE exchange_rate " +
                "SET rate = ? " +
                "WHERE id = (" +
                        "SELECT er.id " +
                        "FROM exchange_rate er " +
                        "INNER JOIN currency c_base " +
                            "ON er.base_currency_id = c_base.id " +
                        "INNER JOIN currency c_target " +
                            "ON er.target_currency_id = c_target.id  " +
                        "WHERE c_base.code = ? " +
                        "AND c_target.code = ?" +
                ")";

        ExchangeRate saved = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setBigDecimal(1, exchangeRate.getRate());
            preparedStatement.setString(2, exchangeRate.getBaseCurrency().getCode());
            preparedStatement.setString(3, exchangeRate.getTargetCurrency().getCode());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(NO_ROWS_AFFECTED);
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                Optional<ExchangeRate> optional = findById(generatedKeys.getLong(1));

                saved = optional.orElseThrow(() ->
                        new SQLException("Saving exchange rate failed, no exchange rate with retrieved ID found."));
            }
        } catch (SQLException e) {
            if (e.getMessage().contains(NO_ROWS_AFFECTED)) {
                throw new NotFoundException("One of the currencies was not found. Exchange rate was not updated.");
            }

            throw new RuntimeException(e);
        }

        return saved;
    }
}
