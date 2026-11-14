import java.math.BigDecimal;

public class producto {
    private int id;
    private String nombre;
    private BigDecimal precio;
    private int inventario;
    private boolean disponibilidad;

    public producto(int id, String nombre, BigDecimal precio, int inventario) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.inventario = inventario;
        this.disponibilidad = inventario > 0;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public int getInventario() { return inventario; }
    public void setInventario(int inventario) { 
        this.inventario = inventario; 
        this.disponibilidad = inventario > 0;
    }
    public boolean isDisponibilidad() { return disponibilidad; }
}