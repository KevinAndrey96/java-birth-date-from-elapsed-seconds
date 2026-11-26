# Fecha de nacimiento a partir de los segundos vividos (Java 17+)

[![CI](https://github.com/KevinAndrey96/java-birth-date-from-elapsed-seconds/actions/workflows/ci.yml/badge.svg)](https://github.com/KevinAndrey96/java-birth-date-from-elapsed-seconds/actions/workflows/ci.yml)
![Java 17 | 21 | 25](https://img.shields.io/badge/Java-17%20%7C%2021%20%7C%2025-ED8B00?logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?logo=apachemaven&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5&logoColor=white)
[![Licencia MIT](https://img.shields.io/badge/licencia-MIT-green)](LICENSE)

Programa en **Java** que calcula la **fecha y hora de nacimiento** de una persona a partir de una **fecha de referencia** en formato `aaaammdd` y la **cantidad de segundos que ha vivido**, con validación estricta del calendario y aritmética de fechas con `java.time`.

## Enunciado

> Una persona ha vivido una cantidad x de segundos. Escriba un programa en lenguaje Java que a partir de una fecha `yyyymmdd` y la cantidad de segundos pueda calcular la fecha de nacimiento.

## Uso

```bash
mvn -B verify
java -jar target/birth-date.jar 20171016 1000000000
```

```text
Fecha de referencia : 16 de octubre de 2017, 00:00:00
Segundos vividos    : 1.000.000.000
Fecha de nacimiento : 6 de febrero de 1986, 22:13:20
Edad a esa fecha    : 31 años, 8 meses y 10 días
```

Sin argumentos, el programa pide ambos valores por consola. Los segundos aceptan separadores de miles (`1.000.000.000`). Ante una entrada inválida responde con un mensaje en español y código de salida `2`.

## Modelo de cálculo

- El momento de referencia es el **inicio del día** indicado (00:00:00).
- La fecha de nacimiento es ese momento menos los segundos vividos, con `LocalDateTime.minusSeconds`, que resuelve meses de distinta duración y **años bisiestos**.
- Se trabaja en hora civil sin cambios de horario de verano (Colombia no los aplica), así que todos los días tienen 86 400 segundos.
- La edad se expresa en años, meses y días completos con `Period.between`.

## Defectos de la versión de 2017 corregidos

| Defecto original | Corrección |
| --- | --- |
| Los segundos se guardaban en un `int`: cualquier persona con más de 68 años desbordaba el valor | `long`, con pruebas de 3 000 millones de segundos (95 años) |
| La validación `Segundos <= 0 && Segundos > 2147483647` nunca podía cumplirse | Validación explícita de entero no negativo |
| Aceptaba fechas inexistentes como el 31 de febrero o el día 0 | `DateTimeFormatter` con `ResolverStyle.STRICT` |
| Descartaba los segundos que no completaban un día | Se conserva la hora exacta de nacimiento |
| Cualquier texto no numérico terminaba en una excepción sin controlar | Mensajes claros y código de salida `2` |
| Interfaz con `JOptionPane`, imposible de probar o automatizar | CLI sin interfaz gráfica, con modo interactivo y por argumentos |

## Estructura

| Clase | Responsabilidad |
| --- | --- |
| `ReferenceDate` | Interpreta y valida la fecha `aaaammdd` |
| `BirthDateCalculator` | Resta los segundos y valida el rango soportado |
| `BirthDate` | Resultado inmutable (`record`) con fecha, hora y edad |
| `cli.BirthDateCli` | Entrada por argumentos o consola y salida en español |

## Calidad

```bash
mvn -B verify   # compila con -Xlint:all -Werror y ejecuta 39 pruebas JUnit 5
```

GitHub Actions compila y prueba en Java 17, 21 y 25, y ejecuta una prueba de humo del JAR.

## Historia del proyecto

Ejercicio de programación de septiembre de 2017 hecho en NetBeans con ventanas `JOptionPane`. El código original se conserva en la etiqueta [`v1.0-netbeans-swing-2017`](https://github.com/KevinAndrey96/java-birth-date-from-elapsed-seconds/tree/v1.0-netbeans-swing-2017). En 2026 se reescribió con `java.time`, pruebas y CI.

## Licencia

[MIT](LICENSE) © 2017-2026 Kevin Andrey Herrera Silva
