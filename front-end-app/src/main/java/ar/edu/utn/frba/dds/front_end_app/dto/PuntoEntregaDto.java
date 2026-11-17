package ar.edu.utn.frba.dds.front_end_app.dto;

public class PuntoEntregaDto {
    private Long id;
    private String nombreEntidad;
    private String tipoEntidad;
    private String tituloDonacion;
    private String descripcionDonacion;
    private String direccionEntidad;
    private Double latitud;
    private Double longitud;
    private String fotoEntrega;
    private String fechaEntrega;
    private String estado;

    public PuntoEntregaDto() {}

    public PuntoEntregaDto(Long id, String nombreEntidad, String tipoEntidad, String tituloDonacion,
                           String descripcionDonacion, String direccionEntidad, Double latitud,
                           Double longitud, String fotoEntrega, String fechaEntrega, String estado) {
        this.id = id;
        this.nombreEntidad = nombreEntidad;
        this.tipoEntidad = tipoEntidad;
        this.tituloDonacion = tituloDonacion;
        this.descripcionDonacion = descripcionDonacion;
        this.direccionEntidad = direccionEntidad;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fotoEntrega = fotoEntrega;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreEntidad() { return nombreEntidad; }
    public void setNombreEntidad(String nombreEntidad) { this.nombreEntidad = nombreEntidad; }

    public String getTipoEntidad() { return tipoEntidad; }
    public void setTipoEntidad(String tipoEntidad) { this.tipoEntidad = tipoEntidad; }

    public String getTituloDonacion() { return tituloDonacion; }
    public void setTituloDonacion(String tituloDonacion) { this.tituloDonacion = tituloDonacion; }

    public String getDescripcionDonacion() { return descripcionDonacion; }
    public void setDescripcionDonacion(String descripcionDonacion) { this.descripcionDonacion = descripcionDonacion; }

    public String getDireccionEntidad() { return direccionEntidad; }
    public void setDireccionEntidad(String direccionEntidad) { this.direccionEntidad = direccionEntidad; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getFotoEntrega() { return fotoEntrega; }
    public void setFotoEntrega(String fotoEntrega) { this.fotoEntrega = fotoEntrega; }

    public String getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(String fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
