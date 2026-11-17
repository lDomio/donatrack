/*package ar.edu.utn.frba.dds.incentivos.main.repositories.impl;

import ar.edu.utn.frba.dds.common.utils.GeneradorIdSecuencial;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.repositories.DonanteRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class DonanteRepositoryImpl implements DonanteRepository {
    private final List<Donante> donantes = new ArrayList<>();
    private final GeneradorIdSecuencial generadorId = new GeneradorIdSecuencial();

    @Override
    public Optional<Donante> findById(Long id) {
        return this.donantes.stream()
                .filter( donante -> donante.getId().equals(id))
                .findFirst();
    }

    @Override
    public void guardar(Donante donante) {
        if(donante.getId() == null){
            donante.setId(generadorId.siguiente());
            donantes.add(donante);
        } else{
            this.findById(donante.getId())
                    .ifPresent(this.donantes::remove);
            donantes.add(donante);
        }
    }

    @Override
    public List<Donante> findAll() {
        return this.donantes;
    }
}
*/