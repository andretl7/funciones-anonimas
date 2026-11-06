import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class Problemas {
    record Producto(String nombre, String categoria, int precio, int stock) {}

    public static void main(String[] args) throws Exception {
        var productos = Files.readAllLines(Path.of("catalogo.csv")).stream()
                .skip(1)
                .map(fila -> fila.split(","))
                .map(c -> new Producto(c[0], c[1], Integer.parseInt(c[2]), Integer.parseInt(c[3])))
                .toList();

        // Selección: productos pendientes de reposición.
        var agotados = productos.stream()
                .filter(p -> p.stock() == 0)
                .map(p -> p.nombre())
                .collect(Collectors.joining(", "));
        System.out.println("1. Agotados = " + agotados);

        // Transformación: importe de cada línea del inventario en USD.
        var valores = productos.stream()
                .map(p -> Integer.toString(p.precio() * p.stock()))
                .collect(Collectors.joining(", "));
        System.out.println("2. Valor por producto (USD) = " + valores);

        // Validación: existencia de faltantes y validez de los precios.
        var existeAgotado = productos.stream().anyMatch(p -> p.stock() == 0);
        var preciosValidos = productos.stream().allMatch(p -> p.precio() > 0);
        System.out.println("3. Existe agotado = " + existeAgotado);
        System.out.println("3. Todos los precios son positivos = " + preciosValidos);

        // Acumulación: el flujo de importes se reduce mediante una suma.
        var total = productos.stream()
                .mapToInt(p -> p.precio() * p.stock())
                .reduce(0, (a, b) -> a + b);
        System.out.println("4. Valor total (USD) = " + total);
    }
}
