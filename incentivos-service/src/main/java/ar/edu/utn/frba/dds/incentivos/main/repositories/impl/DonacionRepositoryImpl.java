/*package ar.edu.utn.frba.dds.incentivos.main.repositories.impl;

import ar.edu.utn.frba.dds.common.utils.GeneradorIdSecuencial;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.repositories.DonacionRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class DonacionRepositoryImpl implements DonacionRepository {
    private final List<Donacion> donaciones = new ArrayList<>();
    private final GeneradorIdSecuencial generadorId = new GeneradorIdSecuencial();

    @Override
    public Optional<Donacion> findById(@NonNull Long id) {
        return donaciones.stream().filter(donacion -> donacion.getId().equals(id)).findFirst();
    }

    @Override
    public Donacion save(Donacion donacion) {
        if(donacion.getId() == null){
            donacion.setId(generadorId.siguiente());
            donaciones.add(donacion);
        } else{
            this.findById(donacion.getId()).ifPresent(donaciones::remove);
            donaciones.add(donacion);
        }
    }

    @Override
    public List<Donacion> findAll() {
        return this.donaciones;
    }

    @Override
    public void actualizar(Donacion donacion) {
        try {
            this.findById(donacion.getId()).ifPresent(donanteSinActualizar-> {
                donaciones.remove(donanteSinActualizar);
                donaciones.add(donacion);
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Donacion> findAllByUserId(Long idDonante) {
        return donaciones.stream().filter(donacion -> Objects.equals(donacion.getIdDonante(), idDonante)).toList();
    }
}
*/