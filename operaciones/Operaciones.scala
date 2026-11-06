object Operaciones extends App {
  val productos = Catalogo.cargar()

  // El criterio de orden es el precio unitario en USD.
  val ordenados = productos.sortBy(p => p.precio)
  println(s"1. sortBy = ${ordenados.map(p => p.nombre).mkString(", ")}")

  // Se ordenan las claves al imprimir para hacer reproducible la salida.
  val grupos = productos.groupBy(p => p.categoria)
  println("2. groupBy")
  grupos.toList.sortBy { case (categoria, _) => categoria }.foreach {
    case (categoria, grupo) =>
      println(s"   $categoria = ${grupo.map(p => p.nombre).mkString(", ")}")
  }

  // El primer grupo cumple la condición y el segundo contiene el resto.
  val (criticos, disponibles) = productos.partition(p => p.stock < 5)
  println(s"3. partition (stock < 5) = ${criticos.map(p => p.nombre).mkString(", ")}")
  println(s"   Resto = ${disponibles.map(p => p.nombre).mkString(", ")}")

  // find conserva el orden de entrada y devuelve None si no hay coincidencia.
  val primerAgotado = productos.find(p => p.stock == 0)
  println(s"4. find = ${primerAgotado.map(p => p.nombre)}")

  // Tres pasos: seleccionar, ordenar y tomar la primera coincidencia.
  val prioridad = productos
    .filter(p => p.stock < 5)
    .sortBy(p => -p.precio)
    .find(p => p.categoria == "Tecnologia")
  println(s"5. Combinacion = ${prioridad.map(p => p.nombre)}")
}
