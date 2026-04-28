# payment-service

Microservicio de procesamiento de pagos del sistema **Stayly**. Consume el evento `BookingConfirmed` de SQS y procesa el cobro de la reserva. Diseñado para integrarse con proveedores externos de pago como Stripe o Conekta — por ahora simula el cobro para mantener el foco en la arquitectura.

---

## ¿Cómo encaja en Stayly?

```
hotel-service → search-service → booking-service → payment-service → notification-service
```

`payment-service` es un consumidor puro de eventos — no expone endpoints para crear pagos manualmente. Los pagos se crean automáticamente al recibir el evento `BookingConfirmed` de SQS.

---

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/payments/{id}` | Obtener pago por ID |
| GET | `/api/v1/payments/booking/{bookingId}` | Obtener pago por reserva |
| GET | `/api/v1/payments?userId=UUID` | Listar pagos por usuario |

---

## Flujo de procesamiento

```
SQS: BookingConfirmed
    → payment-service consume evento
        → verificar idempotencia (existsByBookingId)
            → llamar proveedor de pago (simulado)
                → INSERT payment (status: COMPLETED / FAILED)
```

---

## Decisiones de diseño

**Sin endpoint de creación** — Los pagos no se crean desde el API sino desde el listener de SQS. El controller es solo de lectura. Esto refleja que `payment-service` es un consumidor de eventos, no un servicio transaccional directo.

**Idempotencia con `unique` constraint** — El campo `bookingId` tiene constraint `unique` en la DB. Si SQS entrega el mismo evento dos veces, el segundo intento se descarta antes de llegar a la DB gracias a `existsByBookingId`. Esto previene cobros duplicados.

**Cobro simulado** — El método `processPayment` simula siempre éxito. En producción aquí se integraría Stripe, Conekta u otro proveedor. La arquitectura ya está lista para ese cambio sin modificar el resto del servicio.

**`PaymentType` en el modelo** — Aunque por ahora todos los pagos son `CREDIT_CARD`, el enum está listo para soportar múltiples métodos de pago cuando se integre un proveedor real.

---

## Idempotencia

SQS garantiza at-least-once delivery — el mismo evento puede llegar más de una vez. `payment-service` maneja esto en dos capas:

```java
// Capa 1 — verificación antes de procesar
if (paymentRepository.existsByBookingId(bookingId)) {
    log.info("Payment already processed, skipping");
    return;
}

// Capa 2 — unique constraint en DB como red de seguridad
@Column(nullable = false, unique = true)
private UUID bookingId;
```

---

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.x |
| Base de datos | PostgreSQL 16 |
| Mensajería | Amazon SQS |
| ORM | Hibernate / Spring Data JPA |
| Contenedor | Docker |
| Registry | Amazon ECR Public |
| CI/CD | GitHub Actions |

---

## Correr localmente

**1. Levantar la base de datos:**
```bash
docker-compose up -d
```

**2. Configurar credenciales AWS en `.env`:**
```
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key
```

**3. Correr la aplicación:**
```bash
./mvnw spring-boot:run
```

La app corre en `http://localhost:8083`.

---

## CI/CD

Cada push a `main` ejecuta el pipeline en GitHub Actions que:
1. Compila el proyecto con Maven
2. Construye la imagen Docker
3. Hace push a Amazon ECR Public con dos tags: `latest` y el SHA del commit
