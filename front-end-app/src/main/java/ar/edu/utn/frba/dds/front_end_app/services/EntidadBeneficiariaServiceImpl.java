package ar.edu.utn.frba.dds.front_end_app.services;

import ar.edu.utn.frba.dds.front_end_app.dto.CategoriaResponseSinSubcategoria;
import ar.edu.utn.frba.dds.front_end_app.dto.SubcategoriaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonacionResponseEntidad;
import ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria.EntidadBeneficiariaDTO;
import ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria.NecesidadDTO;
import ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria.NecesidadRequest;
import ar.edu.utn.frba.dds.front_end_app.exceptions.UnauthorizedException;
import ar.edu.utn.frba.dds.front_end_app.exceptions.ValidationException;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.EntidadBeneficiariaApiService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class EntidadBeneficiariaServiceImpl {
  private final EntidadBeneficiariaApiService entidadBeneficiariaApiService;
  private final DonacionService donacionService;

  public EntidadBeneficiariaServiceImpl(EntidadBeneficiariaApiService entidadBeneficiariaApiService, DonacionService donacionService) {
    this.entidadBeneficiariaApiService = entidadBeneficiariaApiService;
    this.donacionService = donacionService;
  }
  public EntidadBeneficiariaDTO getEntidadById(Long id){
    return entidadBeneficiariaApiService.getById(id);
  }
  public List<NecesidadDTO> getNecesidadesByIdEntidad(Long idEntidad){
    return entidadBeneficiariaApiService.getNecesidadesByIdEntidad(idEntidad);
  }
  public void confirmarDonacionAsignadaAEntidad(Long idDonacion, Long idEntidad){
    confirmarDonacionAsignadaAEntidad(idDonacion, idEntidad, null);
  }
  public void confirmarDonacionAsignadaAEntidad(Long idDonacion, Long idEntidad, String fotoEntrega){
    EntidadBeneficiariaDTO entidad = this.getEntidadById(idEntidad);
    List<DonacionResponseEntidad> donaciones = this.getDonacionesByIdEntidad(idEntidad);
    boolean asignada = (donaciones != null && donaciones.stream().anyMatch(d -> Objects.equals(d.id(), idDonacion)))
        || (entidad != null && entidad.donacionesAsignadas() != null && entidad.donacionesAsignadas().stream().anyMatch(d -> Objects.equals(d.id(), idDonacion)));
    if (asignada) {
      String razonSocial = entidad != null && entidad.razonSocial() != null ? entidad.razonSocial() : "Entidad";
      donacionService.confirmarDonacion(idDonacion, "La entidad confirmó la recepción.", "La entidad " + razonSocial + ", con id " + idEntidad, fotoEntrega);
    }
    else{
      throw new UnauthorizedException("La donación que trató de confirmar no está asignada a usted.");
    }
  }
  public List<DonacionResponseEntidad> getDonacionesEnTraslado(Long idEntidad){
    return this.getDonacionesByIdEntidad(idEntidad).stream().filter(d -> d.estado().toString().equals("EN_TRASLADO")).toList();
  }
  public NecesidadDTO getNecesidadById(Long idEntidad, Long idNecesidad){
    List<NecesidadDTO> necesidades = entidadBeneficiariaApiService.getNecesidadesByIdEntidad(idEntidad);
    NecesidadDTO necesidad = entidadBeneficiariaApiService.getNecesidadById(idNecesidad);
    if (necesidades.stream().noneMatch(n -> n.id().equals(necesidad.id()))){
      throw new UnauthorizedException("Solo puede ver sus necesidades.");
    }
    return necesidad;
  }
  public List<SubcategoriaResponse> getSubcategorias(Long categoriaId){
    return entidadBeneficiariaApiService.getSubcategorias(categoriaId);
  }
  public List<CategoriaResponseSinSubcategoria> getCategorias(){
    return entidadBeneficiariaApiService.getCategorias();
  }
  public void crearNecesidad(Long entidadId, NecesidadRequest necesidad){
    ValidationException validationException = new ValidationException("Errores de validación");

    if(necesidad.getCantidad() == null){
      validationException.addFieldError("cantidad", "La cantidad en obligatoria");
    }
    else {
      if (necesidad.getCantidad() <= 0) {
        validationException.addFieldError("cantidad", "La cantidad tiene que ser  mayor a 0");
      }
    }

    if(necesidad.getDescripcion() == null || necesidad.getDescripcion().trim().isEmpty()){
      validationException.addFieldError("descripcion", "La descripcion es obligatoria");
    }
    if(necesidad.getTipoNecesidad() == null){
      validationException.addFieldError("tipoNecesidad", "La necesidad debe ser recurrente o extraordinaria");
    }
    else {
      if (!necesidad.getTipoNecesidad().equals("RECURRENTE") && !necesidad.getTipoNecesidad().equals("EXTRAORDINARIA")) {
        validationException.addFieldError("tipoNecesidad", "La necesidad debe ser recurrente o extraordinaria");
      }
      if (necesidad.getTipoNecesidad().equals("RECURRENTE")) {
        if(necesidad.getPeriodo()==null){
          validationException.addFieldError("periodo", "El periodo es obligatorio");
        }
        else {
          if (!this.esPeriodoISO8601Valido(necesidad.getPeriodo())) {
            validationException.addFieldError("periodo", "El periodo debe ser válido");
          }
          if (Period.parse(necesidad.getPeriodo()).isZero()) {
            validationException.addFieldError("periodo", "El periodo debe ser mayor que 0");
          }
          if (necesidad.getInicioRecurrencia() == null) {
            validationException.addFieldError("inicioRecurrencia", "El inicio de recurrencia es obligatorio");
          }
          else{
            if (necesidad.getInicioRecurrencia().isBefore(LocalDate.now())){
              validationException.addFieldError("inicioRecurrencia", "  El inicio de recurrencia debe ser hoy o en el futuro");
            }
          }
        }
      }
    }
    if(validationException.hasFieldErrors()){
      throw validationException;
    }
    entidadBeneficiariaApiService.crearNecesidad(entidadId, necesidad);
  }
  public List<DonacionResponseEntidad> getDonacionesByIdEntidad(Long entidadId){
    return entidadBeneficiariaApiService.getDonacionesByIdEntidad(entidadId);
  }
  public Long getEntidadIdByUsuarioId(Long usuarioId){
    return entidadBeneficiariaApiService.obtenerPorIdUsuarioAuth(usuarioId).id();
  }
  private boolean esPeriodoISO8601Valido(String texto) {
    if (texto == null || texto.isEmpty()) {
      return false;
    }

    try {
      // Intentar parsear como Period (ej. P1Y2M10D)
      Period.parse(texto);
      return true;
    } catch (DateTimeParseException e1) {
      try {
        // Intentar parsear como Duration (ej. PT12H30M)
        Duration.parse(texto);
        return true;
      } catch (DateTimeParseException e2) {
        // Si falla en ambos, no es un formato ISO-8601 válido para Java nativo
        return false;
      }
    }
  }
}
