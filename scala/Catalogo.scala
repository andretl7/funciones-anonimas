import scala.io.Source
import scala.util.Using

case class Producto(nombre: String, categoria: String, precio: Int, stock: Int)

object Catalogo {
  def cargar(): List[Producto] =
    Using.resource(Source.fromFile("catalogo.csv", "UTF-8")) { archivo =>
      archivo.getLines().drop(1).map { fila =>
        val campos = fila.split(",")
        Producto(campos(0), campos(1), campos(2).toInt, campos(3).toInt)
      }.toList
    }
}
