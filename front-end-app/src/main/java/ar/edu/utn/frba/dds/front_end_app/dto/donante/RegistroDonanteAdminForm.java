package ar.edu.utn.frba.dds.front_end_app.dto.donante;

public record RegistroDonanteAdminForm(
    String tipoPersona,
    String tipoDocumento,
    String numeroDocumento,
    String nombreRazonSocial,
    String emailDonante,
    String telefonoDonante,
    String username,
    String password,
    String confirmPassword
) {
    public RegistroDonanteAdminForm(
        String tipoPersona,
        String tipoDocumento,
        String numeroDocumento,
        String nombreRazonSocial,
        String emailDonante,
        String telefonoDonante
    ) {
        this(tipoPersona, tipoDocumento, numeroDocumento, nombreRazonSocial, emailDonante, telefonoDonante, null, null, null);
    }
}
