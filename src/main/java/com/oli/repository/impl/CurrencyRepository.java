package com.oli.repository.impl;

import com.oli.entity.Currency;
import com.oli.repository.CrudRepository;
import com.oli.repository.DataSourceRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyRepository extends DataSourceRepository implements CrudRepository<Currency> {

    public CurrencyRepository(DataSource dataSource) {
        super(dataSource);
    }

    private Currency fromResultSet(ResultSet resultSet) throws SQLException {
        return Currency.builder()
                .id(resultSet.getLong("id"))
                .code(resultSet.getString("code"))
                .fullName(resultSet.getString("fullname"))
                .sign(resultSet.getString("sign"))
                .build();
    }

    @Override
    public List<Currency> findAll() {
        String query = "SELECT * FROM currency";
        List<Currency> res = new ArrayList<>();

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
    public Optional<Currency> findById(Long id) {
        String query =
                "SELECT * FROM currency " +
                "WHERE id = " + id;

        Optional<Currency> optional = Optional.empty();

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

    public Optional<Currency> findByCode(String code) {
        String query =
                "SELECT * FROM currency " +
                "WHERE code = '" + code + "'";

        Optional<Currency> optional = Optional.empty();

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
    public Currency save(Currency obj) {
        String query =
                "INSERT INTO currency (code, fullname, sign) " +
                "VALUES (?, ?, ?)";

        Currency saved = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, obj.getCode());
            preparedStatement.setString(2, obj.getFullName());
            preparedStatement.setString(3, obj.getSign());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Saving currency failed, no rows affected.");
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                Optional<Currency> optional = findById(generatedKeys.getLong(1));

                if (optional.isPresent()) {
                    saved = optional.get();
                } else {
                    throw new SQLException("Saving currency failed, no currency with retrieved ID found.");
                }
            } else {
                throw new SQLException("Saving currency failed, no ID generated.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return saved;
    }
}
