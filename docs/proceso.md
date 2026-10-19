# Algoritmo Factorial con Recursión de Cola

## Definición del Algoritmo

```Scala
def factorial(n: Int): BigInt = {
  @annotation.tailrec
  def loop(x: Int, acumulador: BigInt): BigInt = {
    if (x <= 1) acumulador
    else loop(x - 1, acumulador * x)
  }
  loop(n, 1)
}
```

* La función `factorial` calcula el factorial de un número `n` utilizando **recursión de cola**.
* La función interna `loop` es la que hace la recursión:

  * Recibe dos parámetros:

    * `x`: el valor actual decreciente hasta llegar a 1.
    * `acumulador`: donde se guarda el resultado parcial en cada paso.
* El decorador `@annotation.tailrec` obliga a que la función sea optimizada como recursión de cola, es decir, **no se acumulan llamados en la pila**.

## Explicación paso a paso

### Caso base

```Scala
if (x <= 1) acumulador
```

Cuando `x` llega a `1`, la función retorna directamente el valor acumulado, evitando más llamadas.

### Caso recursivo

```Scala
loop(x - 1, acumulador * x)
```

En cada llamada:

* Se reduce el valor de `x` en 1.
* Se multiplica el acumulador por `x` y se pasa a la siguiente iteración.
* Como es recursión de cola, la llamada recursiva es la **última instrucción** en ejecutarse, lo que permite a Scala optimizar la pila.

---

## Llamados de pila en recursión de cola

Ejemplo:

```Scala
factorial(5)
```

### Paso 1: Llamada inicial

```Scala
loop(5, 1)
```

### Paso 2: Primera iteración

```Scala
loop(4, 5)   // acumulador = 1 * 5
```

### Paso 3: Segunda iteración

```Scala
loop(3, 20)  // acumulador = 5 * 4
```

### Paso 4: Tercera iteración

```Scala
loop(2, 60)  // acumulador = 20 * 3
```

### Paso 5: Cuarta iteración

```Scala
loop(1, 120) // acumulador = 60 * 2
```

### Paso 6: Caso base

```Scala
return 120
```

---

## Diferencia con recursión normal

* En **recursión normal** cada llamada queda en la pila esperando a que termine la siguiente, lo que puede causar desbordamiento si `n` es muy grande.
* En **recursión de cola**, el compilador transforma el proceso en un **bucle optimizado**, por lo que no se guarda cada llamada en la pila y el algoritmo puede ejecutarse para valores muy grandes sin problema.

---

## Ejemplo de uso

```Scala
val resultado = factorial(5)
println(resultado)  // 120
```

El resultado de `factorial(5)` es `120`.


## Diagrama de llamados de pila con recursión de cola

```mermaid
sequenceDiagram
    participant Main as factorial(5)
    participant L1 as loop(5, 1)
    participant L2 as loop(4, 5)
    participant L3 as loop(3, 20)
    participant L4 as loop(2, 60)
    participant L5 as loop(1, 120)

    Main->>L1: llamada inicial
    L1->>L2: tail call con (4, 5)
    L2->>L3: tail call con (3, 20)
    L3->>L4: tail call con (2, 60)
    L4->>L5: tail call con (1, 120)
    L5-->>Main: return 120
```




---

# Informe de proceso: puntos 2 y 3

## Punto 2: `ejecutar`

```scala
def ejecutar(e: Estado, o: Orden): Estado = o match {
  case Avanzar(n) => e.mira match {
    case Norte => Estado(e.x, e.y + n, Norte)
    case Este => Estado(e.x + n, e.y, Este)
    case Sur => Estado(e.x, e.y - n, Sur)
    case Oeste => Estado(e.x - n, e.y, Oeste)
  }
  case Girar(s) => Estado(e.x, e.y, girar(e.mira, s))
  case Repetir(veces, orden) =>
    if (veces <= 0) e
    else ejecutar(ejecutar(e, orden), Repetir(veces - 1, orden))
  case Secuencia(Nil) => e
  case Secuencia(primera :: resto) => ejecutar(ejecutar(e, primera), Secuencia(resto))
}
```

### Traza de `ejecutar(inicial, Repetir(2, S))`

Sea `S = Secuencia(List(Avanzar(1), Girar(Derecha)))` e `inicial = Estado(0, 0, Norte)`.

```
ejecutar((0,0,N), Repetir(2, S))
= ejecutar( ejecutar((0,0,N), S), Repetir(1, S) )
    ejecutar((0,0,N), S)
    = ejecutar( ejecutar((0,0,N), Avanzar(1)), Secuencia(List(Girar(Derecha))) )
    = ejecutar( (0,1,N), Secuencia(List(Girar(Derecha))) )
    = ejecutar( ejecutar((0,1,N), Girar(Derecha)), Secuencia(Nil) )
    = ejecutar( (0,1,E), Secuencia(Nil) )
    = (0,1,E)
= ejecutar( (0,1,E), Repetir(1, S) )
= ejecutar( ejecutar((0,1,E), S), Repetir(0, S) )
    ejecutar((0,1,E), S) = (1,1,S)      // avanza al este y gira a la derecha
= ejecutar( (1,1,S), Repetir(0, S) )
= (1,1,S)
```

El resultado es `Estado(1, 1, Sur)`.

### Pila de llamados

Cada llamado de `ejecutar` recibe como argumento el resultado de otro llamado. Ese
argumento se calcula primero y termina antes de que siga el llamado de afuera. Esta
es la pila en el momento más hondo de la traza, cuando se evalúa `Avanzar(1)` dentro
de la primera vuelta:

```mermaid
graph TD
  A["ejecutar((0,0,N), Repetir(2, S)): espera el estado de la primera vuelta"] --> B["ejecutar((0,0,N), S): espera el estado tras Avanzar(1)"]
  B --> C["ejecutar((0,0,N), Avanzar(1)): devuelve (0,1,N)"]
```

Cuando `C` devuelve, `B` sigue con el resto de la secuencia, y cuando `B` devuelve, `A`
continúa con `Repetir(1, S)`. La pila nunca es más honda que la estructura de la orden:
cada `Repetir` y cada `Secuencia` abren un nivel, y las repeticiones se van gastando una
a una porque `veces` baja de a uno.

## Punto 3: `cuantasBasicas` y `profundidad`

```scala
def cuantasBasicas(o: Orden): Int = o match {
  case Avanzar(_) => 1
  case Girar(_) => 1
  case Repetir(veces, orden) => if (veces <= 0) 0 else veces * cuantasBasicas(orden)
  case Secuencia(Nil) => 0
  case Secuencia(primera :: resto) => cuantasBasicas(primera) + cuantasBasicas(Secuencia(resto))
}
```

### Traza de `cuantasBasicas(cuadrado)`

Con `cuadrado = Repetir(4, Secuencia(List(Avanzar(1), Girar(Derecha))))`:

```
cuantasBasicas(Repetir(4, Secuencia(List(Avanzar(1), Girar(Derecha)))))
= 4 * cuantasBasicas(Secuencia(List(Avanzar(1), Girar(Derecha))))
= 4 * ( cuantasBasicas(Avanzar(1)) + cuantasBasicas(Secuencia(List(Girar(Derecha)))) )
= 4 * ( 1 + ( cuantasBasicas(Girar(Derecha)) + cuantasBasicas(Secuencia(Nil)) ) )
= 4 * ( 1 + ( 1 + 0 ) )
= 4 * 2
= 8
```

```mermaid
graph TD
  A["cuantasBasicas(Repetir(4, S)): espera 4 * ..."] --> B["cuantasBasicas(S): espera 1 + ..."]
  B --> C["cuantasBasicas(Secuencia(List(Girar))): espera 1 + ..."]
  C --> D["cuantasBasicas(Secuencia(Nil)): devuelve 0"]
```

Aquí cada llamado deja una operación pendiente (la multiplicación o la suma), así que
la pila crece con la profundidad de la orden y con la longitud de las secuencias, igual
que `cesar` en el taller 1.

```scala
def profundidad(o: Orden): Int = {
  def masHonda(ordenes: List[Orden]): Int = ordenes match {
    case Nil => 0
    case primera :: resto => math.max(profundidad(primera), masHonda(resto))
  }

  o match {
    case Avanzar(_) => 1
    case Girar(_) => 1
    case Repetir(_, orden) => 1 + profundidad(orden)
    case Secuencia(ordenes) => 1 + masHonda(ordenes)
  }
}
```

### Traza de `profundidad(cuadrado)`

```
profundidad(Repetir(4, Secuencia(List(Avanzar(1), Girar(Derecha)))))
= 1 + profundidad(Secuencia(List(Avanzar(1), Girar(Derecha))))
= 1 + ( 1 + masHonda(List(Avanzar(1), Girar(Derecha))) )
= 1 + ( 1 + max( profundidad(Avanzar(1)), masHonda(List(Girar(Derecha))) ) )
= 1 + ( 1 + max( 1, max( profundidad(Girar(Derecha)), masHonda(Nil) ) ) )
= 1 + ( 1 + max( 1, max( 1, 0 ) ) )
= 1 + ( 1 + 1 )
= 3
```

La función auxiliar `masHonda` recorre la lista de órdenes de una secuencia y se queda
con la profundidad mayor. Para la lista vacía devuelve 0, por eso `Secuencia(Nil)` tiene
profundidad 1. En `Repetir` no se multiplica por `veces`: la profundidad mide cuántos
niveles de anidamiento hay y no cuántas veces se repite.


# Informe de proceso: puntos 1 y 4

## Punto 1: girar

### Funcionamiento

La función girar recibe una dirección y un sentido de giro. Mediante un match, contempla las ocho combinaciones posibles entre las cuatro direcciones (Norte, Este, Sur y Oeste) y los dos sentidos (Derecha e Izquierda). En cada caso devuelve la dirección que resulta de girar noventa grados, sin modificar la posición del robot.

### Traza de cuatro giros a la izquierda

Esta traza corresponde a la prueba propia que comprueba que cuatro giros a la izquierda devuelven la dirección inicial:

girar(Norte, Izquierda) = Oeste
girar(Oeste, Izquierda) = Sur
girar(Sur, Izquierda)   = Este
girar(Este, Izquierda)  = Norte

Después de los cuatro giros, la dirección vuelve a ser Norte. No hay llamadas recursivas en esta función: cada combinación se resuelve directamente mediante uno de los casos del match.

## Punto 4: componer, rutina, aplicarA y transformar

### componer

La función construye una Secuencia con las dos órdenes recibidas, manteniendo su orden original: primero se coloca o1 y después o2.

#### Traza de la prueba propia

componer(Girar(Izquierda), Avanzar(3))
= Secuencia(List(Girar(Izquierda), Avanzar(3)))

La traza muestra que la orden de giro queda antes que la orden de avance, tal como se indicó en la llamada.

### rutina

La función construye una orden Repetir que contiene una secuencia formada por un avance y un giro. Los parámetros indican cuántos pasos avanzar, en qué sentido girar y cuántas veces repetir la secuencia.

#### Traza de la prueba propia

rutina(3)(Derecha)(0)
= Repetir(0, Secuencia(List(Avanzar(3), Girar(Derecha))))
La rutina construida tiene cero repeticiones. Por eso, al ejecutarla, el robot permanece en su estado inicial:
ejecutar(inicial, rutina(3)(Derecha)(0))
= inicial

La función construye la orden; el movimiento se produce cuando esa orden se entrega a ejecutar.

### aplicarA

La función recorre recursivamente una lista de estados. En cada llamada, ejecuta la misma orden sobre el primer estado y añade el resultado a la lista que devuelve la llamada recursiva sobre el resto. Cuando la lista está vacía (Nil), devuelve Nil, que constituye el caso base.

#### Traza con los estados de la prueba propia

aplicarA(
  List(Estado(1, 2, Norte), Estado(4, 5, Este)),
  Avanzar(2)
)
= Estado(1, 4, Norte) ::
    aplicarA(List(Estado(4, 5, Este)), Avanzar(2))
= Estado(1, 4, Norte) ::
    (Estado(6, 5, Este) :: aplicarA(Nil, Avanzar(2)))
= Estado(1, 4, Norte) ::
    (Estado(6, 5, Este) :: Nil)
= List(Estado(1, 4, Norte), Estado(6, 5, Este))

Cada estado se procesa una vez y se conserva el orden de la lista original. La recursión termina al llegar a Nil; entonces las llamadas pendientes construyen la lista de resultados con ::.

### transformar

La función aplica una transformación a las órdenes básicas (Avanzar y Girar) y conserva la estructura de las órdenes compuestas. En una orden Repetir, transforma recursivamente la orden contenida y conserva el número de repeticiones. En una Secuencia, transforma cada elemento de la lista y mantiene su orden.

#### Traza de la prueba propia

La prueba define una función que cambia los giros a la derecha por giros a la izquierda y deja las demás órdenes sin cambios. Con cuadrado = Repetir(4, Secuencia(List(Avanzar(1), Girar(Derecha)))), la transformación produce:

transformar(Repetir(4, Secuencia(List(Avanzar(1), Girar(Derecha)))), f)
= Repetir(4, transformar(Secuencia(List(Avanzar(1), Girar(Derecha))), f))
= Repetir(4, Secuencia(List(
    transformar(Avanzar(1), f),
    transformar(Girar(Derecha), f)
  )))
= Repetir(4, Secuencia(List(Avanzar(1), Girar(Izquierda))))

El avance permanece igual y el giro cambia de Derecha a Izquierda, como espera la prueba.

#### Traza de una secuencia vacía

La otra prueba propia comprueba que transformar una secuencia vacía con la función identidad la deja igual:
transformar(Secuencia(Nil), identidad)
= Secuencia(Nil)
## Resumen del proceso
- girar resuelve cada combinación de dirección y sentido mediante un match.
- componer agrupa dos órdenes en una secuencia, conservando su orden.
- rutina construye una orden repetida que contiene un avance y un giro.
- aplicarA recorre recursivamente una lista de estados y ejecuta una orden sobre cada uno.
- transformar recorre las órdenes compuestas, transforma las órdenes básicas y reconstruye la estructura original.