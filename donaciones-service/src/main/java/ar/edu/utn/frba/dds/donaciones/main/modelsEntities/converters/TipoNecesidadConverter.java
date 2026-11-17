package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.converters;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.TipoNecesidad;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.TipoNecesidadEnum;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.TipoNecesidadExtraordinaria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.TipoNecesidadRecurrente;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TipoNecesidadConverter implements AttributeConverter<TipoNecesidad, String> {

    @Override
    public String convertToDatabaseColumn(TipoNecesidad attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getTipoNecesidad().name();
    }

    @Override
    public TipoNecesidad convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        TipoNecesidadEnum tipo = TipoNecesidadEnum.valueOf(dbData);
        return switch (tipo) {
            case RECURRENTE -> new TipoNecesidadRecurrente();
            case EXTRAORDINARIA -> new TipoNecesidadExtraordinaria();
        };
    }
}
