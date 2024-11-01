## Exchange Rate Project

REST API for describing currencies and exchange rates.

Tech Task: https://zhukovsd.github.io/java-backend-learning-course/projects/currency-exchange/

Tech Task Author: https://github.com/zhukovsd

### Stack:

- Jakarta Servlets
- JDBC
- PostgreSQL

### Endpoints:

#### GET `/currencies`

Getting all the currencies.

#### GET `/currency/USD`

Getting currency specified by code.

#### POST `/currencies`

Adding new currency into DB. Request body:

```
{
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
}
```

#### GET `/exchangeRates`

Getting all the exchange rates.

#### GET `/exchangeRate/USDEUR`

Getting exchange rate specified by currency codes.

#### POST `/exchangeRates`

Adding new exchange rate to DB. Request body:

```
{
    "baseCurrencyCode": "USD"
    "targetCurrencyCode": "EUR"
    "rate": "0.99"
}
```

#### PUT `/exchangeRate/USDEUR`

Updating existing exchange rate. Request body:

```
{
    "rate": 1.34
}
```

#### GET `/exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT`

Calculating of the transfer of a certain amount of funds from one currency to another specified by codes.

