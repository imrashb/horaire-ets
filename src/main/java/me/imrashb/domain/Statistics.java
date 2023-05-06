package me.imrashb.domain;

import lombok.*;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
@Getter
public class Statistics {

    @Id
    private Long id = 0L;

    @Column(name = "total_combinaisons")
    private Long totalCombinaisons = 0L;

    @Column(name = "total_generations")
    private Long totalGenerations = 0L;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "generations_per_programmes")
    private Map<Programme, Long> generationsPerProgrammes = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "generations_per_sessions")
    private Map<String, Long> generationsPerSessions = new HashMap<>();

    @Column(name = "time_spent_generating_nanoseconds")
    private Long timeSpentGeneratingNanoseconds = 0L;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "average", column = @Column(name = "average_nombre_cours")),
            @AttributeOverride( name = "size", column = @Column(name = "size_nombre_cours"))
    })
    private Average averageNombreCours = new Average();


    public long getTimeSpentGenerating(@NonNull TimeUnit timeUnit) {
        return timeUnit.convert(timeSpentGeneratingNanoseconds, TimeUnit.NANOSECONDS);
    }

    public void addTotalCombinaisons(long amount) {
        this.totalCombinaisons+=amount;
    }

    public void incrementTotalGenerations() {
        this.totalGenerations++;
    }

    public void incrementGenerationsPerSession(String sessionId) {
        Long amount = Optional.ofNullable(generationsPerSessions.get(sessionId)).orElse(0L);
        generationsPerSessions.put(sessionId, amount+1);
    }

    public synchronized void addGenerationsPerProgrammes(@NonNull List<Cours> cours) {
        for(Cours c : cours) {
            for(Programme p : c.getProgrammes()) {
                Long amount = Optional.ofNullable(generationsPerProgrammes.get(p)).orElse(0L);
                generationsPerProgrammes.put(p, amount+1);
            }
        }
    }

    public void addTimeSpentGenerating(long nanoseconds) {
        this.timeSpentGeneratingNanoseconds+=nanoseconds;
    }

    public void addNombreCoursAverage(int value) {
        this.averageNombreCours.add(value);
    }

    @Embeddable
    @Getter
    private static class Average {

        @Column(name="average")
        private double average = 0L;

        @Column(name="size")
        private long size = 0L;

        public void add(int... values) {
            size+=values.length;
            average = average + (IntStream.of(values).sum()-average)/size;
        }

    }

}
