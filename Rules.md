# 🧠 Reglas de Trabajo en Equipo — GitFlow

Este documento define la estrategia de colaboración para el equipo de desarrollo usando Git y el flujo GitFlow. Aplica al desarrollo del sistema de reservas de vuelos.

## 📌 Objetivos

* Ordenar el desarrollo colaborativo
* Evitar conflictos innecesarios
* Facilitar integración y testing
* Mantener ramas limpias y funcionales

## 🌱 Estructura de Ramas

Usaremos el modelo GitFlow simplificado:

* **main** → rama principal (producción)
* **develop** → integración y pruebas (estable)
* **feature/xxx** → nuevas funcionalidades
* **fix/xxx** → corrección de errores
* **release/xxx** → versiones candidatas para producción
* **hotfix/xxx** → parches urgentes sobre main


## 🔧 Reglas por tipo de rama

### main
* Contiene el código en producción (estable)
* Solo se actualiza desde release o hotfix
* Nunca se hace commit directo

### develop
* Rama de integración continua (último código aprobado)
* Se actualiza por merge de features y fixes
* De aquí se parte para release

### feature/mi-funcionalidad
* Crear a partir de develop
* Nombrar en minúsculas y con guiones: `feature/busqueda-vuelos`
* Debe contener solo UNA funcionalidad
* Merge a develop solo después de pruebas

**Ejemplo:**
```bash
git checkout develop 
git checkout -b feature/crear-reserva
```

### fix/nombre-del-fix
* Para bugs encontrados en develop
* Merge a develop

### hotfix/nombre-del-hotfix
* Correcciones urgentes en producción
* Se crean desde main
* Se hace merge tanto a main como a develop

### release/v1.0.0
* Versión candidata para producción
* Se crean desde develop
* Se prueba, documenta y luego merge a main y develop

## 💡 Convenciones de Commits

Usar mensajes de commits claros y estructurados. Ejemplo:

* `feat: agregar búsqueda de vuelos por destino`
* `fix: corregir error en validación de correo`
* `refactor: extraer lógica de reserva a clase nueva`
* `docs: agregar README de arquitectura`
* `test: agregar tests de ReservaService`

**Estructura:** `tipo: mensaje claro`


## 🚦 Flujo de Trabajo Recomendado

1. `git pull origin develop`
2. Crear rama feature: `git checkout -b feature/nombre`
3. Codificar y testear localmente
4. Commit con mensajes claros
5. `git push origin feature/nombre`
6. Crear Pull Request (PR) hacia develop
7. Revisar y aprobar en equipo
8. Merge vía PR

---

## ✅ Checklist para Merge Requests

Antes de hacer merge:

* ¿El código compila?
* ¿Se probó la funcionalidad completa?
* ¿Los nombres son claros y coherentes?
* ¿El código está documentado?
* ¿Se evitó modificar cosas no relacionadas?

---

## 🧪 Testing

* Toda nueva funcionalidad debe probarse al menos de forma manual o agregar pruebas unitarias usando JUNIT (en este caso se permite el uso de IA para hacer las pruebas unitarias) 
* Probar casos límite: sin datos, duplicados, campos vacíos, etc.

---

## 📁 Organización del Repositorio

* **src/**: código fuente
* **docs/**: documentación funcional y técnica
* **README.md**: documentación base
* **GITFLOW.md** o **RULES.md**: este documento
