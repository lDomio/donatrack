package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Period;

@Converter( autoApply = false )
public class PeriodConverter implements AttributeConverter<Period, String> {

    @Override
    public String convertToDatabaseColumn(Period period) {
        if (period == null) {
            return null;
        }
        return period.toString();
    }

    @Override
    public Period convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        return Period.parse(dbData);
    }
}
