import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class venta {
    private int id;
    private OffsetDateTime fecha;
    private BigDecimal total;
    private List<desc_venta> detalles = new ArrayList<>();

    public venta() {
        this.fecha = OffsetDateTime.now();
        this.total = BigDecimal.ZERO;
    }

    public void agregarDetalle(desc_venta detalle) {
        this.detalles.add(detalle);
        this.total = this.total.add(detalle.getSubtotal());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public OffsetDateTime getFecha() { return fecha; }
    public void setFecha(OffsetDateTime fecha) { this.fecha = fecha; }
    public BigDecimal getTotal() { return total; }
    public List<desc_venta> getDetalles() { return detalles; }
}