import java.math.BigDecimal;

public class producto {
    private int id;
    private String nombre;
    private BigDecimal precio;
    private int inventario;
    private boolean disponibilidad;

    public producto() {}

    public producto(int id, String nombre, BigDecimal precio, int inventario, boolean disponibilidad) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.inventario = inventario;
        this.disponibilidad = disponibilidad;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public int getInventario() { return inventario; }
    public void setInventario(int inventario) { this.inventario = inventario; }
    public boolean isDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(boolean disponibilidad) { this.disponibilidad = disponibilidad; }
}