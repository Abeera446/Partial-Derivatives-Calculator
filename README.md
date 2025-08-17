# Partial Derivatives Calculator

Interactive CLI program to compute partial derivatives, implicit differentiation, higher-order derivatives, and verify Clairaut's Theorem/ Laplace's Equation.  
Built with Java and Maven. Uses **Symja (Matheclipse)** for symbolic math operations.

---

## Features
- First partial derivatives
- First partial derivative with numeric evaluation
- Implicit differentiation
- Second and higher-order partial derivatives
- Verify **Clairaut's Theorem** (equality of mixed partials)
- Verify **Laplace's Equation**
- Menu-driven CLI

---

## Tech stack
- Java (8+; Java 17 recommended)
- Maven
- Symja / Matheclipse (symbolic math library)

---

## Add Symja dependency (pom.xml)
Add this dependency into your `<dependencies>` in `pom.xml`:

```xml
<dependency>
  <groupId>org.matheclipse</groupId>
  <artifactId>matheclipse-core</artifactId>
  <version>3.0.0</version>
</dependency>
```
## Build & Run
```bash
mvn clean package
mvn compile exec:java -Dexec.mainClass="org.example.PartialDerivatives"
```
