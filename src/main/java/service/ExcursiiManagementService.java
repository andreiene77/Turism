package service;

import model.Excursie;
import repository.IRepository;

import java.time.LocalTime;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ExcursiiManagementService {
    private IRepository<String, Excursie> repoExcursie;

    public ExcursiiManagementService(IRepository<String, Excursie> repoExcursie) {
        this.repoExcursie = repoExcursie;
    }

    public Iterable<Excursie> searchExcursii(String obiectiv, LocalTime minTime, LocalTime maxTime) {
        Stream<Excursie> stream_excursii = StreamSupport.stream(repoExcursie.findAll().spliterator(), false);
        Predicate<Excursie> bySearch = ex ->
                ex.getObiectiv().contains(obiectiv) &&
                        ex.getOraPlecarii().isBefore(maxTime) &&
                        ex.getOraPlecarii().isAfter(minTime);
        return stream_excursii.filter(bySearch).collect(Collectors.toList());
    }

    public Iterable<Excursie> getAllExcursii() {
        return repoExcursie.findAll();
    }
}
