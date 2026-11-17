package ar.edu.utn.frba.dds.donaciones.main.dtos.donantes;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
    @JsonSubTypes.Type(value = HumanoCreateRequest.class, name = "HUMANA"),
    @JsonSubTypes.Type(value = JuridicoCreateRequest.class, name = "JURIDICA")
})
public interface DonanteCreateRequest {
}
