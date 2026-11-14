import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class BaseDatos {
    private List<producto> tablaProductos = new ArrayList<>();
    private List<venta> tablaVentas = new ArrayList<>();
    private List<desc_venta> tablaDescVentas = new ArrayList<>();

    // Contadores para simular el autoincremento de los IDs por ahora (sin aleatorios)
    private int contadorProductoId = 1;
    private int contadorVentaId = 1;
    private int contadorDescId = 1;

    public BaseDatos() {
        // Datos de ejemplo
        agregarProducto("Laptop HP", new BigDecimal("2500.00"), 5);
        agregarProducto("Mouse Inalámbrico", new BigDecimal("45.00"), 20);
        agregarProducto("Iphone promax de 1Tb", new BigDecimal("180.00"), 10);
    }

    public producto agregarProducto(String nombre, BigDecimal precio, int inventario) {
        producto nuevo = new producto(contadorProductoId++, nombre, precio, inventario);
        tablaProductos.add(nuevo);
        return nuevo;
    }

    public producto buscarProductoPorId(int id) {
        for (producto p : tablaProductos) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public List<producto> listarProductosDisponibles() {
        List<producto> disponibles = new ArrayList<>();
        for (producto p : tablaProductos) {
            if (p.isDisponibilidad()) {
                disponibles.add(p);
            }
        }
        return disponibles;
    }

    public boolean agregarStock(int productoId, int cantidadExtra) {
        producto p = buscarProductoPorId(productoId);
        if (p != null && cantidadExtra > 0) {
            p.setInventario(p.getInventario() + cantidadExtra);
            return true;
        }
        return false;
    }

    // Venta
    public boolean registrarVenta(venta ventaObj) {
        // verifica que sean ids validos y que esten disponibles
        for (desc_venta det : ventaObj.getDetalles()) {
            producto p = buscarProductoPorId(det.getProductoId());
            if (p == null || !p.isDisponibilidad() || p.getInventario() < det.getCantidad()) {
                System.out.println("Error: Stock insuficiente o producto invalido (ID: " + det.getProductoId() + ")");
                return false;
            }
        }

        // Asignar un ID a la venta y la guarda
        ventaObj.setId(contadorVentaId++);
        tablaVentas.add(ventaObj);

        // Guardar detalles y descontar inventario
        for (desc_venta det : ventaObj.getDetalles()) {
            det.setId(contadorDescId++);
            det.setVentaId(ventaObj.getId());
            tablaDescVentas.add(det);

            producto p = buscarProductoPorId(det.getProductoId());
            p.setInventario(p.getInventario() - det.getCantidad());
        }

        return true;
    }

    public List<venta> listarVentas() {
        return tablaVentas;
    }

    // Estimacion de ganancias
    public BigDecimal estimarIngresosProximosDias(int diasHistoricos, int diasAProyectar) {
        OffsetDateTime fechaLimite = OffsetDateTime.now().minusDays(diasHistoricos);
        BigDecimal totalHistorico = BigDecimal.ZERO;

        for (venta v : tablaVentas) {
            if (!v.getFecha().isBefore(fechaLimite)) {
                totalHistorico = totalHistorico.add(v.getTotal());
            }
        }

        BigDecimal promedioDiario = totalHistorico.divide(
            BigDecimal.valueOf(diasHistoricos), 2, RoundingMode.HALF_UP
        );
        return promedioDiario.multiply(BigDecimal.valueOf(diasAProyectar));
    }
}