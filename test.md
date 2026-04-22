# 1. Основной процесс рассылки

```mermaid
sequenceDiagram
    participant System as Другие модули/подсистемы
    participant EventLog as EventLog (БД)
    participant Job as Служебный Job
    participant EventType as EventType (БД)
    participant Template as Template Engine
    participant Channel as Каналы связи

    System->>EventLog: Регистрирует событие<br/>(event_type_id, context)
    Note right of EventLog: Новая запись с status='new'
    
    loop Каждые N минут
        Job->>EventLog: SELECT new events
        Job->>EventType: Получить настройки<br/>WHERE event_type_id = ?
        
        alt Настройки найдены
            EventType-->>Job: context_settings, message_template, recipients
            
            loop По каждому событию
                Job->>Job: Извлечь параметры из context<br/>используя context_settings
                Job->>Template: Подставить в message_template
                Template-->>Job: Сформированное сообщение
                
                Job->>Job: Определить список получателей
                
                alt Email канал
                    Job->>Channel: Отправить email
                end
                
                alt SMS канал
                    Job->>Channel: Отправить SMS
                end
                
                alt Push канал
                    Job->>Channel: Отправить push
                end
                
                Job->>EventLog: UPDATE status='sent'<br/>+ timestamp отправки
            end
            
        else Настройки не найдены
            Job->>EventLog: UPDATE status='error'<br/>+ сообщение об ошибке
        end
    end
```

---

***

# 2. Регистрация события

```mermaid
sequenceDiagram
    participant User as Пользователь
    participant BizSystem as Бизнес-модуль
    participant EventLog as EventLog (БД)

    User->>BizSystem: Выполняет действие<br/>(заказ, регистрация и т.д.)
    BizSystem->>BizSystem: Валидация бизнес-правил
    
    alt Событие должно быть зафиксировано
        BizSystem->>EventLog: INSERT<br/>event_type_id<br/>context (JSON)<br/>status='new'
        Note right of EventLog: Асинхронная регистрация
        
        BizSystem-->>User: Продолжить обработку
    else Без регистрации
        BizSystem-->>User: Продолжить обработку
    end
```

---

***


# 3. Настройка типа события

```mermaid
flowchart TD
    A[Администратор добавляет тип события] --> B[Заполняет метаданные]
    B --> C[event_type_id<br/>name<br/>description]
    
    C --> D[Настраивает ContextSettings]
    D --> D1[Определяет параметры<br/>контекста]
    D1 --> D2[Описывает путь извлечения<br/>JSON путь/выражение]
    
    D2 --> E[Настраивает MessageTemplate]
    E --> E1[Шаблон сообщения<br/>с переменными]
    E1 --> E2[Пример: 'Здравствуйте, $#123;name#125;!']
    
    E2 --> F[Указывает каналы отправки]
    F --> F1[Email<br/>SMS<br/>Push<br/>Иные]
    
    F1 --> G[Сохранение в EventType]
```

---

***

## 4. Обработка ошибок

```mermaid
flowchart TD
    A[Событие в EventLog] --> B{Статус}
    
    B -->|new| C[Попытка обработки]
    B -->|sent| Z[Завершено успешно]
    B -->|error| Y[Ожидает ручного вмешательства]
    
    C --> D{Успешно?}
    D -->|Да| E[update status='sent'<br/>sent_at=now]
    D -->|Нет| F[update status='error'<br/>error_message=...<br/>retry_count+1]
    
    F --> G{retry_count < 3?}
    G -->|Да| H[Планирование повтора]
    G -->|Нет| I[Перевод в статус 'failed'<br/>требует анализ]
```




---

## 5. Контекст и шаблоны - пример

```mermaid
flowchart LR
    A[EventLog.context<br/>JSON] --> B[EventType.context_settings<br/>JSON]
    B --> C[Извлеченные параметры]
    C --> D[EventType.message_template]
    D --> E[Финальное сообщение]
    
    subgraph Пример данных
        A1["context:<br/>{<br/>  'user_id': 123,<br/>  'order_id': 'ORD-456',<br/>  'amount': 5000<br/>}"]
        B1["context_settings:<br/>{<br/>  'user_name': '$.user.name',<br/>  'order_number': '$.order_id',<br/>  'total': '$.amount'<br/>}"]
        D1["message_template:<br/>'Здравствуйте, {user_name}!<br/>Ваш заказ {order_number}<br/>на сумму {total} руб.обработан.'"]
    end
```

---

## Основные сущности

| Сущность | Ключевые поля |
|-----------|---------------|
| **EventType** | event_type_id, name, description, context_settings (JSON), message_template, channels |
| **EventLog** | event_id, event_type_id, context (JSON), status (new/sent/error/failed), created_at, sent_at, error_message |

## Статусы событий

- `new` - зарегистрировано, ожидает обработки
- `sent` - успешно отправлено
- `error` - ошибка при обработке (автоматический повтор)
- `failed` - окончательная ошибка (требует вмешательства)
