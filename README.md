# COMPA Backend

> COMPA - Acompañamiento estudiantil: sistema de gestión para orientadores y estudiantes

**Spring Boot 3.5 | Java 17 | PostgreSQL (Neon) | Render**

---

## Descripción del Proyecto

COMPA es una aplicación web diseñada para orientadores que necesitan gestionar la información clínica de sus estudiantes, hacer seguimiento de su estado de salud y asignarles planes de hábitos diarios. Los estudiantes pueden realizar un registro diario (check-in) de su estado emocional y el cumplimiento de sus tareas en menos de 30 segundos.

El sistema garantiza que el historial clínico nunca se elimina físicamente, mantiene trazabilidad de todos los cambios de estado con justificación, y genera rachas de cumplimiento para motivar al estudiante.

---
## Equipo de Desarrollo

| Nombre | Rol |
|--------|-----|
| Andrea Marin Diaz | Desarrolladora FullStack | 
| Mariana  Carvajal Rueda | Desarrolladora FullStack |

---
## Arquitectura del Proyecto

El proyecto sigue una arquitectura en capas  con separación clara de responsabilidades:
```
com.compa/
├── controller/     → Expone los endpoints REST, recibe peticiones HTTP
├── service/        → Contiene la lógica de negocio y validaciones
├── repository/     → Interfaces JPA para acceso a la base de datos
├── model/          → Clases que mapean las tablas de la base de datos
├── dto/
│   ├── request/    → Objetos de entrada validados con Jakarta Validation
│   └── response/   → Objetos de salida formateados para el cliente
├── enums/          → Tipos enumerados: estados, motivos, barreras, tipos de documento
└── exception/      → Manejo centralizado de errores con GlobalExceptionHandler
```
## Cómo Correr el Proyecto Localmente

### Requisitos Previos

- Java 17
- Maven 3.9+
- Conexión a internet (la base de datos está en Neon)

### Pasos

**1. Clonar el repositorio:**
```bash
git clone <https://github.com/LifeTrackWellness/LifeTracker-WellnessBackend>
cd LifeTrackerBackend/backend
```

**2. Verificar Java y Maven:**
```bash
java -version
mvn -version
```

**3. Configurar variables de entorno en `application.yml`:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://...neon.tech/neondb?sslmode=require
    username: neondb_owner
    password: <password>
```

**4. Correr la aplicación:**
```bash
mvn spring-boot:run
```

---

## Despliegue

---
