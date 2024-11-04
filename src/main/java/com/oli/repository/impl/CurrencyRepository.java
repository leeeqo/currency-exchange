package com.oli.repository.impl;

import com.oli.entity.Currency;
import com.oli.exception.impl.AlreadyExistsException;
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

public class CurrencyRepository extends DataSourceRepository implements CrRepository<Currency> {

    private static final String UNIQUE_CONSTRAINT_VIOLATION =
            "violates unique constraint \"currency_code_key\"";

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
            throw new RuntimeException(e);
        }

        return res;
    }

    @Override
    public Optional<Currency> findById(Long id) {
        String query =
                "SELECT * FROM currency " +
                "WHERE id = ?";

        Optional<Currency> optional = Optional.empty();

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

    public Optional<Currency> findByCode(String code) {
        String query =
                "SELECT * FROM currency " +
                "WHERE code = ?";

        Optional<Currency> optional = Optional.empty();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                optional = Optional.of(fromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
                throw new SQLException();
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                Optional<Currency> optional = findById(generatedKeys.getLong(1));

                saved = optional.orElseThrow(() ->
                        new SQLException("Saving currency failed, no currency with retrieved ID found."));
            }
        } catch (SQLException e) {
            if (e.getMessage().contains(UNIQUE_CONSTRAINT_VIOLATION)) {
                throw new AlreadyExistsException("Currency with code \"" + obj.getCode() + "\" already exists.");
            }

            throw new RuntimeException(e);
        }

        return saved;
    }
}
