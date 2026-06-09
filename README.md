# Proyecto Triage/Stage Emergency 2026-1S

<p align="left">
  <img src="https://img.shields.io/badge/Java-Estructuras de Datos-red?style=for-the-badge&logo=java">
  <img src="https://img.shields.io/badge/Estado-Finalizado-success?style=for-the-badge">
  <img src="https://img.shields.io/badge/UNAL-Proyecto%20Universitario-green?style=for-the-badge">
  <img src="https://img.shields.io/badge/Grupo-1-purple?style=for-the-badge">
</p>

> ## Estructuras de Datos (2016699) - Grupo 1
> Profesor: David Alberto Herrera Alvarez - dherreraal@unal.edu.co
>
> Monitor: Daniel Alfonso Cely Infante - dcelyi@unal.edu.co

---

## Descripción del Proyecto
El **Triage Stage Emergency** es un sistema inteligente de selección y clasificación de pacientes basado en necesidades terapéuticas y recursos disponibles. A diferencia del modelo tradicional por orden de llegada, este sistema implementa algoritmos de priorización clínica para asegurar que las emergencias vitales sean atendidas de inmediato.

### Objetivos
* Gestionar el flujo masivo de pacientes en una sala de emergencias.
* Garantizar la asignación de atención mediante niveles de urgencia (1-5).
* Optimizar la búsqueda y recuperación de información de pacientes en tiempo real.

---

### Arquitectura y Eficiencia

Para cumplir con los exigentes requisitos de alto rendimiento y volumen de datos, se implementaron estructuras avanzadas desde cero:

| Funcionalidad | Estructura Utilizada | Complejidad (Big O) | Justificación |
| :--- | :--- | :--- | :--- |
| **Gestión de Prioridad** | Montículo Binario (Max-Heap) | $O(\log n)$ | Rendimiento logarítmico inmune a la acumulación de pacientes en un mismo nivel. Aprovecha la localidad espacial del procesador al usar un arreglo contiguo. |
| **Búsqueda por ID** | Tabla Hash con Encadenamiento | Promedio $O(1)$ | Acceso asociativo directo por cédula O(1) mediante función de familia universal. El encadenamiento por listas asegura que la eliminación de pacientes dados de alta sea atómica y limpia. |
| **Historial de Atenciones** | Pila (LIFO) | $O(1)$ | Registra pacientes atendidos y permite deshacer la última atención en tiempo constante. |

*En entregas anteriores se utilizó un Árbol AVL y un Arreglo de Buckets, los cuales fueron cambiados para la entrega final por indicacion, se logra un rendimiento constante en consultas y mayor integridad en almacenamiento continuo)

### Funcionamiento de la Cola de Prioridad
El sistema utiliza un **Montículo Binario (Max-Heap)** implementado sobre un arreglo primitivo. La prioridad está encapsulada matemáticamente para asegurar que el paciente más grave siempre flote hacia la raíz (posición `[0]`).

```text
Estructura Lógica en Arreglo (Agnóstica a los 5 niveles):

[0: Triage 1 (Raíz)] 
   ├── [1: Triage 2 (Hijo Izq)] 
   │      ├── [3: Triage 3] 
   │      └── [4: Triage 4] 
   └── [2: Triage 2 (Hijo Der)] 
          ├── [5: Triage 5] 
          └── [6: Triage 3]
```
#### **Nuestro sistema realiza:**

- Gestión de Prioridad Multinivel: Clasificación y ordenamiento automático de acuerdo a la urgencia y tiempo de llegada

- Priorización automática: Mantener una fila de espera donde los pacientes con menor nivel de Triage siempre estén al principio.

- Inmutabilidad temporal: Una vez registrado el ingreso, la fecha y hora no deben ser modificables por el usuario, asegurando la transparencia en las auditorías de tiempos de espera.

- Acceso directo: Consultar los datos de un paciente mediante su ID.

- Atención: Extraer al paciente de mayor prioridad del sistema cuando un médico quede disponible.

- Estadísticas: Listar cuántos pacientes han sido atendidos por cada nivel de severidad.

- Resolución de Conflictos Temporales: Algoritmo FIFO integrado para el desempate de pacientes con la misma prioridad médica.

- Monitoreo del Estado: Capacidad de visualizar el estado actual de todas las colas de espera en tiempo real.

---
### Modelo de Información
El sistema gestiona la entidad `Paciente` con los siguientes atributos:

| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| `id` | `long` | Identificador único. |
| `nombre` | `String` | Nombre completo del paciente. |
| `edad` | `int` | Edad del paciente (rango 0-100). |
| `sexo` | `char` | Masculino / Femenino. |
| `EPS` | `String` | EPS a la que pertenece. |
| `sintomas` | `String` | Evaluación del paciente. |
| `nivelTriage` | `byte` | Prioridad médica (niveles 1-5). |
| `fechaIngreso` | `LocalDate` | Capturada automáticamente al registro. |
| `horaIngreso` | `LocalTime` | Estampa de tiempo para desempates (FIFO). |

---

## Desarrollado por:
- Diego Alejandro Prieto Badillo - diprietob@unal.edu.co
- Julian Ricardo Rodriguez Villamizar - julrodriguezvi@unal.edu.co
- Sara Mariana Sanabria Ortiz - sasanabriao@unal.edu.co
- Carlos Stiven Romero Sicacha - cromerosi@unal.edu.co
- Miguel Angel Suarez Montiel - migsuarezmo@unal.edu.co

---

## Instalación
1. Clonar el repositorio: `git clone <url-del-repo>`
- Si se desea acceder al Sistema de Triage. Se ejecuta el archivo 'proyecto_ed.exe' encontrado en la carpeta 'output'
- Si se desea realizar las pruebas de complejidad temporal. Se abre el archivo 'Main.java' y se descomenta la linea como se indica.

## Herramientas:
- Java
- Python
- Git

## Estructura del proyecto:
```text
C:.
└───proyecto_ed
    ├───src
    │   ├───main
    │   │   └───java
    │   │       └───grupo1
    │   │           ├───Benchmark
    │   │           │   ├───Benchmark.java
    │   │           │   ├───datosAVL.csv
    │   │           │   ├───datosAVL.png
    │   │           │   ├───datosCola.csv
    │   │           │   ├───datosCola.png
    │   │           │   ├───datosColaNuevo.csv
    │   │           │   ├───datosColaNuevo.png
    │   │           │   ├───datosHash.csv
    │   │           │   ├───datosHash.png
    │   │           │   └───graficador.py
    │   │           ├───Clases
    │   │           │   └───Paciente.java
    │   │           ├───Estructuras
    │   │           │   ├───ArbolAVL.java
    │   │           │   ├───ColaTriage.java
    │   │           │   ├───heap.java
    │   │           │   ├───Lista.java
    │   │           │   ├───Nodo.java
    │   │           │   ├───Pila.java
    │   │           │   └───TablaHash.java
    │   │           ├───Features
    │   │           │   ├───RegistroCSV.java
    │   │           │   └───ResumenTXT.java
    │   │           ├───GUI
    │   │           │   ├───AVLpanel.java
    │   │           │   ├───GUI.java
    │   │           │   └───SalaEsperaGUI.java
    │   │           └───Main.java
    │   └───test
    └───target
```

