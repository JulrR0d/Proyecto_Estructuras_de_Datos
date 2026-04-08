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

## Descripción del proyecto:
El Triage es un sistema de selección y clasificación de pacientes basado en sus necesidades terapéuticas y los recursos disponibles. A diferencia del orden de llegada, este sistema prioriza la gravedad clínica para asegurar que las emergencias vitales sean atendidas de inmediato.

### Objetivo:
Diseñar un sistema que gestione el flujo de pacientes en una sala de emergencias, asegurando que la atención se asigne por niveles de urgencia.

#### **Registro único de pacientes:**
- **ID único** ---> long 
- **Nombre**  del paciente ---> String
- Nivel de **Triage** ---> Byte
- **Hora** de **ingreso***  ---> LocalTime
- **Fecha** de **ingreso*** ---> LocalDate

**Variables importadas del paquete java.time.*

#### **El sistema debe:**
- Priorización automática: Mantener una fila de espera donde los pacientes con menor nivel de Triage (más graves) siempre estén al principio.
- Acceso directo: Consultar el estado y datos de un paciente mediante su ID.
- Atención: Extraer al paciente de mayor prioridad del sistema cuando un médico quede disponible.
- Estadísticas *(Ad disputandum)*: Listar cuántos pacientes han sido atendidos por cada nivel de severidad.

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
proyecto_ed/
│
├── src/
│   └── main/
│       └── java/
│           └── grupo1/
│               ├── Main.java
│               ├── modelo/
│               ├── estructuras/    
│               └── servicios/
│
├── target/
├── pom.xml
└── README.md
```

