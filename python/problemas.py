import csv
from dataclasses import dataclass
from functools import reduce
from pathlib import Path


@dataclass(frozen=True)
class Producto:
    nombre: str
    categoria: str
    precio: int
    stock: int


def main():
    with Path("catalogo.csv").open(encoding="utf-8", newline="") as archivo:
        productos = [
            Producto(nombre, categoria, int(precio), int(stock))
            for nombre, categoria, precio, stock in list(csv.reader(archivo))[1:]
        ]

    # Selección: productos pendientes de reposición.
    agotados = filter(lambda p: p.stock == 0, productos)
    print("1. Agotados = " + ", ".join(map(lambda p: p.nombre, agotados)))

    # Transformación: importe de cada línea del inventario en USD.
    valores = map(lambda p: p.precio * p.stock, productos)
    print("2. Valor por producto (USD) = " + ", ".join(map(str, valores)))

    # Validación: any y all consumen los booleanos hasta decidir el resultado.
    existe_agotado = any(map(lambda p: p.stock == 0, productos))
    precios_validos = all(map(lambda p: p.precio > 0, productos))
    print("3. Existe agotado = " + str(existe_agotado).lower())
    print("3. Todos los precios son positivos = " + str(precios_validos).lower())

    # Acumulación: el total arranca en cero.
    total = reduce(lambda subtotal, p: subtotal + p.precio * p.stock, productos, 0)
    print(f"4. Valor total (USD) = {total}")


if __name__ == "__main__":
    main()
