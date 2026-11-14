import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        BaseDatos db = new BaseDatos();
        Scanner sc = new Scanner(System.in);

        cargarDatosDemo(db);

        int opcion = -1;
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        System.out.println("====================================================");
        System.out.println("   SISTEMA DE GESTIÓN DE VENTAS E INVENTARIO (v1.0) ");
        System.out.println("====================================================");

        while (opcion != 0) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Ver catálogo e inventario actual");
            System.out.println("2. Registrar nuevo producto");
            System.out.println("3. Reabastecer stock de un producto");
            System.out.println("4. Registrar nueva venta");
            System.out.println("5. Ver historial de ventas y detalles");
            System.out.println("6. Proyección / Estimación de ingresos en un plazo");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            if (!sc.hasNextInt()) {
                System.out.println("Entrada inválida. Por favor ingrese un número.");
                sc.next();
                continue;
            }
            opcion = sc.nextInt();

            switch (opcion) {
                case 1 -> {
                    System.out.println("\n--- CATÁLOGO E INVENTARIO DISPONIBLE ---");
                    System.out.printf("%-5s %-22s %-12s %-10s%n", "ID", "PRODUCTO", "PRECIO", "STOCK");
                    System.out.println("----------------------------------------------------");
                    for (producto p : db.listarProductosDisponibles()) {
                        System.out.printf("%-5d %-22s S/ %-9.2f %-10d%n",
                                p.getId(), p.getNombre(), p.getPrecio(), p.getInventario());
                    }
                }
                case 2 -> {
                    sc.nextLine();
                    System.out.println("\n--- NUEVO PRODUCTO ---");
                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine();
                    System.out.print("Precio unitario: ");
                    BigDecimal precio = sc.nextBigDecimal();
                    System.out.print("Stock inicial: ");
                    int stock = sc.nextInt();

                    producto creado = db.agregarProducto(nombre, precio, stock);
                    System.out.println("Producto registrado con ID #" + creado.getId());
                }
                case 3 -> {
                    System.out.println("\n--- REABASTECER INVENTARIO ---");
                    System.out.print("ID del producto: ");
                    int id = sc.nextInt();
                    System.out.print("Cantidad de unidades que ingresan: ");
                    int cantidad = sc.nextInt();

                    if (db.agregarStock(id, cantidad)) {
                        producto p = db.buscarProductoPorId(id);
                        System.out.println("Stock actualizado. Nuevo inventario de " + p.getNombre() + ": " + p.getInventario());
                    } else {
                        System.out.println("No se pudo actualizar. Verifique el ID o la cantidad.");
                    }
                }
                case 4 -> {
                    System.out.println("\n--- NUEVA VENTA ---");
                    venta ventaActual = new venta();

                    while (true) {
                        System.out.print("Ingrese ID del producto (o 0 para finalizar venta): ");
                        int prodId = sc.nextInt();
                        if (prodId == 0) break;

                        producto p = db.buscarProductoPorId(prodId);
                        if (p == null || !p.isDisponibilidad()) {
                            System.out.println("Producto no existe o está agotado.");
                            continue;
                        }

                        System.out.print("Cantidad para '" + p.getNombre() + "' (Disponible: " + p.getInventario() + "): ");
                        int cant = sc.nextInt();

                        if (cant <= 0 || cant > p.getInventario()) {
                            System.out.println("Cantidad inválida o supera el stock disponible.");
                            continue;
                        }

                        ventaActual.agregarDetalle(new desc_venta(prodId, cant, p.getPrecio()));
                        System.out.println("-> Agregado al carrito. Total parcial: S/ " + ventaActual.getTotal());
                    }

                    if (!ventaActual.getDetalles().isEmpty()) {
                        if (db.registrarVenta(ventaActual)) {
                            System.out.println("\n=========================================");
                            System.out.println(" VENTA #" + ventaActual.getId() + " REGISTRADA CON ÉXITO");
                            System.out.println(" Total cobrado: S/ " + ventaActual.getTotal());
                            System.out.println(" Inventario descontado automáticamente.");
                            System.out.println("=========================================");
                        }
                    } else {
                        System.out.println("Venta cancelada (sin productos).");
                    }
                }
                case 5 -> {
                    System.out.println("\n--- HISTORIAL DE VENTAS ---");
                    for (venta v : db.listarVentas()) {
                        System.out.printf("Venta #%d | Fecha: %s | Total: S/ %.2f%n",
                                v.getId(), v.getFecha().format(formatoFecha), v.getTotal());
                        for (desc_venta det : v.getDetalles()) {
                            producto p = db.buscarProductoPorId(det.getProductoId());
                            String nombreProd = (p != null) ? p.getNombre() : "Producto #" + det.getProductoId();
                            System.out.printf("   * %dx %s (P.Unit: S/ %.2f) -> Subtotal: S/ %.2f%n",
                                    det.getCantidad(), nombreProd, det.getPrecioUnitario(), det.getSubtotal());
                        }
                    }
                }
                case 6 -> {
                    System.out.println("\n--- ESTIMACIÓN DE INGRESOS A FUTURO ---");
                    System.out.print("Analizar ventas de los últimos cuántos días (ej. 7): ");
                    int diasHistorial = sc.nextInt();
                    System.out.print("Proyectar ingresos para los próximos cuántos días (ej. 30): ");
                    int diasProyeccion = sc.nextInt();

                    if (diasHistorial <= 0 || diasProyeccion <= 0) {
                        System.out.println("Los días deben ser mayores a 0.");
                    } else {
                        BigDecimal proyeccion = db.estimarIngresosProximosDias(diasHistorial, diasProyeccion);
                        System.out.println("\n----------------------------------------------------");
                        System.out.println("Basado en el ritmo de ventas de los últimos " + diasHistorial + " días:");
                        System.out.println("Ingreso estimado para los próximos " + diasProyeccion + " días: S/ " + proyeccion);
                        System.out.println("----------------------------------------------------");
                    }
                }
                case 0 -> System.out.println("Cerrando sistema...");
                default -> System.out.println("Opción no válida.");
            }
        }
        sc.close();
    }

    private static void cargarDatosDemo(BaseDatos db) {
        venta v1 = new venta();
        v1.setFecha(OffsetDateTime.now().minusDays(5));
        v1.agregarDetalle(new desc_venta(1, 1, new BigDecimal("2500.00")));
        v1.agregarDetalle(new desc_venta(2, 2, new BigDecimal("45.00")));
        db.registrarVenta(v1);

        venta v2 = new venta();
        v2.setFecha(OffsetDateTime.now().minusDays(2));
        v2.agregarDetalle(new desc_venta(3, 2, new BigDecimal("180.00")));
        db.registrarVenta(v2);

        venta v3 = new venta();
        v3.setFecha(OffsetDateTime.now().minusDays(1));
        v3.agregarDetalle(new desc_venta(2, 3, new BigDecimal("45.00")));
        db.registrarVenta(v3);
    }
}