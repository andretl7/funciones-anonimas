# Funciones anónimas en Scala, Java y Python

Trabajo de consulta de Programación Funcional. Los ejemplos usan un [catálogo ficticio](catalogo.csv) de 12 productos con precios enteros en USD y existencias en unidades.

## Índice

- [1. Explorar](#1-explorar)
- [2. Entender](#2-entender)
- [3. Aplicar](#3-aplicar)
- [4. Ampliar](#4-ampliar)
- [5. Comparar y concluir](#5-comparar-y-concluir)
- [Ejecución](#ejecución)

## 1. Explorar

### Apache Spark

Apache Spark procesa datos distribuidos. En un análisis de registros, un RDD de PySpark puede representar las líneas de un archivo repartidas en varias particiones. El analista proporciona la regla para transformar cada línea y Spark se encarga de aplicarla a las particiones. El siguiente fragmento de su guía convierte cada línea en su longitud. La transformación se evalúa cuando una acción solicita el resultado. [Guía de RDD de Apache Spark](https://spark.apache.org/docs/latest/rdd-programming-guide.html#rdd-operations).

```python
lineLengths = lines.map(lambda s: len(s))
```

### pandas

pandas es una biblioteca de Python para analizar datos tabulares. Un informe con mediciones de temperatura puede necesitar una columna adicional en Fahrenheit. Con `assign`, el cálculo queda junto al nombre de la columna que produce y el resultado se puede encadenar con otros pasos del análisis. El fragmento de la documentación calcula la nueva columna a partir de toda la serie en Celsius. [Documentación de pandas.DataFrame.assign](https://pandas.pydata.org/pandas-docs/version/1.5.3/reference/api/pandas.DataFrame.assign.html).

```python
df.assign(temp_f=lambda x: x.temp_c * 9 / 5 + 32)
```

## 2. Entender

**Función anónima.** Es una expresión que crea una función sin darle un nombre en su definición. La palabra *lambda* viene del cálculo lambda de Alonzo Church, que usa la letra griega λ para expresar una abstracción sobre un parámetro. En Scala, `(x: Int) => x + 1` expresa la idea de λx. x + 1. [Stanford Encyclopedia of Philosophy, cálculo lambda de Church](https://plato.stanford.edu/archives/fall2024/entries/church/supplementD.html).

```scala
((x: Int) => x + 1)(4) // 5
```

**Función nombrada.** Una definición con `def` ofrece un nombre al que otros fragmentos pueden hacer referencia. Si la regla solo se usa al filtrar, la lambda permite escribirla en ese punto. Ambas llamadas devuelven `List(5, 8)`.

```scala
def superaCuatro(x: Int): Boolean = x > 4
val valores = List(2, 5, 8)
val conNombre = valores.filter(superaCuatro)
val conLambda = valores.filter(x => x > 4)
```

**Funciones de primera clase.** Se pueden guardar en variables, pasar como argumentos y devolver desde otras funciones. Así, `map` recibe una transformación y `filter` recibe un predicado. Cada operación aplica el valor función a los elementos según su propósito.

```scala
val duplicar: Int => Int = x => x * 2
List(2, 5).map(duplicar) // List(4, 10)
```

**Closure o clausura.** Combina la función con el entorno que necesita para evaluar sus referencias externas. Aquí el predicado conserva acceso al parámetro `minimo` aunque `crearFiltro` ya haya retornado.

```scala
def crearFiltro(minimo: Int): Int => Boolean = x => x >= minimo
val desdeCinco = crearFiltro(5)
List(2, 5, 8).filter(desdeCinco) // List(5, 8)
```

## 3. Aplicar

### 3.1 Catálogo de problemas

| Tipo | Pregunta | Situación |
| --- | --- | --- |
| Selección | ¿Qué productos tienen stock cero? | Compras necesita una lista de reposición. |
| Transformación | ¿Cuánto vale cada línea del inventario? | Contabilidad necesita un importe por producto en el mismo orden del catálogo. |
| Validación | ¿Hay algún agotado y son positivos todos los precios? | La tienda debe avisar de faltantes y revisar los precios antes de publicar el catálogo. |
| Acumulación | ¿Cuánto vale el inventario completo? | El cierre mensual requiere sumar el valor de todas las existencias. |

### 3.2 Soluciones y resultados

Cada celda indica la expresión central. El código completo carga los datos, presenta los resultados y conserva el orden del catálogo cuando corresponde.

| Tipo | [Scala](scala/Problemas.scala) | [Java](java/Problemas.java) | [Python](python/problemas.py) |
| --- | --- | --- | --- |
| Selección | `productos.filter(p => p.stock == 0)` | `productos.stream().filter(p -> p.stock() == 0)` | `filter(lambda p: p.stock == 0, productos)` |
| Transformación | `productos.map(p => p.precio * p.stock)` | `productos.stream().map(p -> Integer.toString(p.precio() * p.stock()))` para presentar los importes | `map(lambda p: p.precio * p.stock, productos)` |
| Validación | `exists(p => p.stock == 0)` y `forall(p => p.precio > 0)` sobre la lista | `anyMatch(p -> p.stock() == 0)` y `allMatch(p -> p.precio() > 0)` sobre Streams separados | `any(map(lambda p: p.stock == 0, productos))` y `all(map(lambda p: p.precio > 0, productos))` |
| Acumulación | `productos.foldLeft(0)((subtotal, p) => subtotal + p.precio * p.stock)` | `productos.stream().mapToInt(p -> p.precio() * p.stock()).reduce(0, (a, b) -> a + b)` | `reduce(lambda subtotal, p: subtotal + p.precio * p.stock, productos, 0)` |

Las tres ejecuciones produjeron la misma salida. Python imprime los booleanos en minúsculas para facilitar la comparación textual.

```text
1. Agotados = Pasta, Teclado
2. Valor por producto (USD) = 160, 120, 0, 66, 96, 80, 216, 0, 450, 150, 60, 150
3. Existe agotado = true
3. Todos los precios son positivos = true
4. Valor total (USD) = 1548
```

Evidencia de ejecución en [Scala](img/scala.png), [Java](img/java.png) y [Python](img/python.png).

## 4. Ampliar

### 4.1 Cuatro operaciones nuevas

Las fichas usan el catálogo de la [Parte 3](#3-aplicar). Los ejemplos completos y el código que imprime los resultados están en [Operaciones.scala](operaciones/Operaciones.scala). [Documentación de la colección List de Scala 2.13](https://www.scala-lang.org/api/2.13.16/scala/collection/immutable/List.html).

#### sortBy

Ordena los elementos por una clave. La lambda `Producto => Int` devuelve el precio de cada producto. Una tienda puede usarla para mostrar primero los artículos más baratos. El ordenamiento es un tipo nuevo que, como la búsqueda de extremos, usa un criterio de comparación.

```scala
productos.sortBy(p => p.precio)
```

Nombres en orden: Lapiz, Pasta, Jabon, Arroz, Cuaderno, Cafe, Toalla, Mouse, Lampara, Mochila, Teclado, Monitor.

#### groupBy

Construye un mapa con un grupo por cada clave. La lambda `Producto => String` devuelve la categoría. Sirve para organizar un catálogo por secciones. La agrupación es un tipo nuevo.

```scala
productos.groupBy(p => p.categoria)
```

| Categoría | Productos |
| --- | --- |
| Alimentos | Arroz, Cafe, Pasta |
| Hogar | Jabon, Toalla, Lampara |
| Papeleria | Cuaderno, Lapiz, Mochila |
| Tecnologia | Mouse, Teclado, Monitor |

#### partition

Divide la lista en dos listas según un predicado. La lambda `Producto => Boolean` devuelve si hay menos de cinco unidades. Permite que el almacén separe los productos críticos del resto. Se parece a la [selección](#31-catálogo-de-problemas) y conserva los dos grupos.

```scala
productos.partition(p => p.stock < 5)
```

Críticos: Pasta, Lampara, Teclado, Monitor. Resto: Arroz, Cafe, Jabon, Toalla, Mouse, Cuaderno, Lapiz, Mochila.

#### find

Devuelve el primer elemento que cumple el predicado en un `Option`. La lambda `Producto => Boolean` devuelve si el producto está agotado. Sirve para tomar el siguiente faltante según el orden del catálogo. Se parece a la [selección](#31-catálogo-de-problemas), con una sola coincidencia y `None` si no hay ninguna.

```scala
productos.find(p => p.stock == 0)
```

Al mostrar el nombre dentro del `Option`, la salida es `Some(Pasta)`.

### 4.2 Cadena de operaciones

**¿Cuál es el producto de tecnología más caro entre los que tienen menos de cinco unidades?**

```scala
productos
  .filter(p => p.stock < 5)
  .sortBy(p => -p.precio)
  .find(p => p.categoria == "Tecnologia")
```

1. `filter` selecciona los productos que requieren revisión de inventario.
2. `sortBy` usa el precio negado como clave para ordenar de mayor a menor.
3. `find` toma el primer producto tecnológico de ese orden. Su nombre aparece como `Some(Monitor)`.

[Evidencia de las cuatro operaciones y de la cadena](img/operaciones.png).

## 5. Comparar y concluir

### 5.1 Scala, Java y Python frente a frente

| Criterio | Scala | Java | Python |
| --- | --- | --- | --- |
| Sintaxis | `p => p.stock == 0` | `p -> p.stock() == 0` | `lambda p: p.stock == 0` |
| Tipo | Se infiere en `filter`. Se puede declarar `Producto => Boolean`. | El Stream infiere el tipo objetivo. Para `filter` es `Predicate<Producto>`. | Tipado dinámico. Se puede anotar una variable como `Callable[[Producto], bool]`. |
| Operaciones de los ejemplos | `filter`, `map`, `exists`, `forall`, `foldLeft` | `filter`, `map`, `mapToInt`, `anyMatch`, `allMatch`, `reduce` | `filter`, `map`, `any`, `all`, `functools.reduce` |
| Restricción | El tipo esperado debe dar suficiente información para inferir los parámetros. | La lambda requiere una interfaz funcional como tipo objetivo. Las variables locales capturadas deben ser finales o efectivamente finales. | El cuerpo de una lambda se limita a una expresión. |
| Legibilidad | Las operaciones se leen directamente sobre la lista. | Los nombres de Stream expresan la intención, con más llamadas para construir la cadena. | Las lambdas son breves. Al anidar `any(map(...))`, la regla queda un nivel más adentro. |

Scala resulta el más natural para estos ejercicios. En validación, los predicados quedan al lado de `exists` y `forall`. En acumulación aparecen las mayores diferencias. Scala y Python actualizan el subtotal a partir de cada producto en un solo pliegue. En Java, el ejemplo primero transforma cada producto en un importe con `mapToInt` y luego reduce los enteros con una suma asociativa y un cero como identidad.

### 5.2 Conclusión

Las funciones anónimas permiten cambiar la regla de un recorrido en el lugar donde se usa. En el catálogo, la condición de stock cero selecciona productos y también sirve para saber si existe algún faltante. Cada operación conserva su propio comportamiento, como detenerse al encontrar una coincidencia o reunir todos los productos que cumplen.

La misma separación se ve en [Apache Spark](#apache-spark). La lambda define cómo convertir una línea en una longitud y la biblioteca aplica esa regla a los datos repartidos. En ambos casos, la expresión deja visible la decisión del problema y la colección o biblioteca se encarga del recorrido.

## Ejecución

Desde la raíz del repositorio, con Scala CLI, Java 17 o posterior y Python 3.8 o posterior:

```sh
scala-cli run scala --scala 2.13.16 --main-class Problemas --server=false
java java/Problemas.java
python python/problemas.py
scala-cli run scala/Catalogo.scala operaciones/Operaciones.scala --scala 2.13.16 --main-class Operaciones --server=false
```

Las imágenes de `img/` reproducen la salida capturada de cada ejecución. La comprobación local de Scala se hizo compilando con el compilador 2.13.16 y ejecutando las clases con Java.

## Referencias y asistencia

Además de las fuentes citadas en sus secciones, se consultaron las referencias de [lambdas en Java](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html) y [programación funcional en Python](https://docs.python.org/3/howto/functional.html). Se utilizó OpenAI Codex para preparar el código, redactar la documentación y verificar las ejecuciones.
