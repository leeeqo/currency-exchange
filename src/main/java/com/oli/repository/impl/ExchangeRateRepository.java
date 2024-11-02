package com.oli.repository.impl;

import com.oli.entity.Currency;
import com.oli.entity.ExchangeRate;
import com.oli.repository.CruRepository;
import com.oli.repository.DataSourceRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateRepository extends DataSourceRepository implements CruRepository<ExchangeRate> {

    private static final String BASE_CURRENCY_FIELDS = "c_base.id AS base_id, c_base.code AS base_code, " +
            "c_base.fullname AS base_fullname, c_base.sign AS base_sign";
    private static final String TARGET_CURRENCY_FIELDS = "c_target.id AS target_id, c_target.code AS target_code, " +
            "c_target.fullname AS target_fullname, c_target.sign AS target_sign";

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
            e.printStackTrace();
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
                "WHERE er.id = " + id;

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
        String query =
                "SELECT er.id, er.rate, " + BASE_CURRENCY_FIELDS + ", " + TARGET_CURRENCY_FIELDS + " " +
                "FROM exchange_rate er " +
                "INNER JOIN currency c_base " +
                    "ON er.base_currency_id = c_base.id " +
                "INNER JOIN currency c_target " +
                    "ON er.target_currency_id = c_target.id  " +
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
        String query =
                "INSERT INTO exchange_rate (base_currency_id, target_currency_id, rate) " +
                "VALUES (?, ?, ?)";

        ExchangeRate saved = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setLong(1, obj.getBaseCurrency().getId());
            preparedStatement.setLong(2, obj.getTargetCurrency().getId());
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
        String query =
                "UPDATE exchange_rate " +
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
