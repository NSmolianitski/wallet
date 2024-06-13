## Запуск
```
docker compose up
```

## POST api/v1/wallet
В ТЗ была опечатка (заменил valletId на walletId), поэтому валидный запрос выглядит так:
```json
{
  "walletId": "b3fa9b9c-6a59-4d7b-ae3e-8f8f3487f1b2",
  "operationType": "DEPOSIT",
  "amount": 10
}
```

## База данных
Изначально таблица wallets содержит следующие данные:

| id                                   | created_at      | updated_at      | amount |
|--------------------------------------|-----------------|-----------------|--------|
| 89a57e5f-206f-4c4a-bd0e-4c8e2264f2b8 | {creation_time} | {creation_time} | 0      |
| 33e0d8e8-6a95-4c59-8f8c-8d6e9e4c4c0a | {creation_time} | {creation_time} | 10     |
| b3fa9b9c-6a59-4d7b-ae3e-8f8f3487f1b2 | {creation_time} | {creation_time} | 50     |
