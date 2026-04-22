## 5. Контекст и шаблоны - пример

```mermaid
flowchart LR
    A[EventLog.context<br/>JSON] --> B[EventType.context_settings<br/>JSON]
    B --> C[Извлеченные параметры]
    C --> D[EventType.message_template]
    D --> E[Финальное сообщение]
    
    subgraph Пример данных
        A1["context:<br/>{<br/>  'user': {'name': 'Иван'},<br/>  'order_id': 'ORD-456',<br/>  'amount': 5000<br/>}"]
        B1["context_settings:<br/>{<br/>  'user_name': '$.user.name',<br/>  'order_number': '$.order_id',<br/>  'total': '$.amount'<br/>}"]
        C1["Извлеченные параметры:<br/>{<br/>  'user_name': 'Иван',<br/>  'order_number': 'ORD-456',<br/>  'total': 5000<br/>}"]
        D1["message_template:<br/>'Здравствуйте, {user_name}!<br/>Ваш заказ {order_number}<br/>на сумму {total} руб.обработан.'"]
        E1["Финальное сообщение:<br/>'Здравствуйте, Иван!<br/>Ваш заказ ORD-456<br/>на сумму 5000 руб.обработан.'"]
    end
