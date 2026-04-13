# Proyecto Triage/Stage Emergency 2026-1S

<p align="left">
  <img src="https://img.shields.io/badge/Java-Estructuras de Datos-red?style=for-the-badge&logo=java">
  <img src="https://img.shields.io/badge/Estado-En Desarrollo-success?style=for-the-badge">
  <img src="https://img.shields.io/badge/UNAL-Proyecto%20Universitario-green?style=for-the-badge">
  <img src="https://img.shields.io/badge/Grupo-1-purple?style=for-the-badge">

</p>
 

> ## Estructuras de Datos (2016699) - Grupo 2
> Profesor: David Alberto Herrera Alvarez - dherreraal@unal.edu.co
>
> Monitor: Daniel Alfonso Cely Infante - dcelyi@unal.edu.co

---

## 📝 Descripción del Proyecto
El **Triage Stage Emergency** es un sistema inteligente de selección y clasificación de pacientes basado en necesidades terapéuticas y recursos disponibles. A diferencia del modelo tradicional por orden de llegada, este sistema implementa algoritmos de priorización clínica para asegurar que las emergencias vitales sean atendidas de inmediato.

### 🎯 Objetivos
* Gestionar el flujo masivo de pacientes en una sala de emergencias.
* Garantizar la asignación de atención mediante niveles de urgencia (1-5).
* Optimizar la búsqueda y recuperación de información de pacientes en tiempo real.

---

### Arquitectura y Eficiencia

Para cumplir con los requisitos de alto rendimiento, se implementaron estructuras de datos manuales optimizadas:

| Funcionalidad | Estructura Utilizada | Complejidad (Big O) | Justificación |
| :--- | :--- | :--- | :--- |
| **Gestión de Prioridad** | Cola de Prioridad con *Buckets* | $O(1)$ | Uso de un arreglo de 5 listas enlazadas (los triage) para evitar recorridos lineales. |
| **Búsqueda por ID** | Árbol Binario Balanceado (AVL) | $O(\log n)$ | Garantiza búsquedas rápidas incluso con grandes volúmenes de datos, no importa si hay 1 o 1000 pacientes. |
| **Desempate (FIFO)** | Lista Enlazada con puntero `tail` | $O(1)$ | Permite inserciones al final sin recorrer la lista completa, por la variable cola/tail . |

### Funcionamiento de la Cola de Prioridad
El sistema no utiliza una lista única, sino un arreglo de estructuras paralelas:

[Índice 0: Triage 1] ──► [Paciente Crítico A] ──► [Paciente Crítico B] ──► NULL

[Índice 1: Triage 2] ──► [Paciente Urgente A] ──► [Paciente Urgente B] ──► NULL

[Índice 2: Triage 3] ──► [Urgencia Menor A] ──► NULL

[Índice 3: Triage 4] ──► [No Urgente A] ──► NULL

[Índice 4: Triage 5] ──► NULL


#### **Nuestro sistema debe:**
- Gestión de Prioridad Multinivel: Clasificación y ordenamiento automático en 5 categorías de urgencia con complejidad $O(1)$.
- Priorización automática: Mantener una fila de espera donde los pacientes con menor nivel de Triage siempre estén al principio.
- Inmutabilidad temporal: Una vez registrado el ingreso, la fecha y hora no deben ser modificables por el usuario, asegurando la transparencia en las auditorías de tiempos de espera.
- Acceso directo: Consultar el estado y datos de un paciente mediante su ID.
- Atención: Extraer al paciente de mayor prioridad del sistema cuando un médico quede disponible.
- Estadísticas: Listar cuántos pacientes han sido atendidos por cada nivel de severidad.
- Resolución de Conflictos Temporales: Algoritmo FIFO integrado para el desempate de pacientes con la misma prioridad médica.
- Monitoreo del Estado: Capacidad de visualizar el estado actual de todas las colas de espera en tiempo real


### Modelo de Información
El sistema gestiona la entidad `Paciente` con los siguientes atributos:

| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| `id` | `long` | Identificador único. |
| `nombre` | `String` | Nombre completo del paciente. |
| `nivelTriage` | `byte` | Prioridad médica / triage (niveles de 1-5). |
| `fechaIngreso`| `LocalDate` | Capturada automáticamente al registro. |
| `horaIngreso` | `LocalTime` | estampa de tiempo para desempates (FIFO). |


## Desarrolado por:
- Diego Alejandro Prieto Badillo - diprietob@unal.edu.co
- Julian Ricardo Rodriguez Villamizar - julrodriguezvi@unal.edu.co
- Sara Mariana Sanabria Ortiz - sasanabriao@unal.edu.co
- Carlos Stiven Romero Sicacha - cromerosi@unal.edu.co
- Miguel Angel Suarez Montiel - migsuarezmo@unal.edu.co

--- 

## Herramientas:

-  Java
-  SQL
-  Git

## Estructura del proyecto:
```text
C:.
│   README.md
│   
└───proyecto_ed
    │   pom.xml
    │   
    ├───out
    │   └───grupo1
    │       │   Main.class
    │       │   
    │       ├───Clases
    │       │       Paciente.class
    │       │       
    │       └───Estructuras
    │               ColaTriage.class
    │               Lista.class
    │               Nodo.class
    │               
    ├───src
    │   └───main
    │       └───java
    │           └───grupo1
    │               │   Main.java
    │               │   
    │               ├───Clases
    │               │       Paciente.java
    │               │       
    │               └───Estructuras
    │                       ArbolAVL.java
    │                       ColaTriage.java
    │                       Lista.java
    │                       Nodo.java
    │                       
    └───target
        └───classes
            └───grupo1
                │   Main.class
                │   
                ├───Clases
                │       Paciente.class
                │       
                └───Estructuras
                        ArbolAVL$Nodo.class
                        ArbolAVL.class
                        ColaTriage.class
                        Lista.class
                        Nodo.class
```

