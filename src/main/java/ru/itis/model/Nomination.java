package ru.itis.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "nominations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "movie_night_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Nomination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean winner = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_night_id", nullable = false)
    private MovieNight movieNight;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nominated_by", nullable = false)
    private User nominatedBy;

    @OneToMany(mappedBy = "nomination", cascade = CascadeType.ALL, orphanRemoval = true)
    // mappedBy = "nomination" - не надо создавать вспомогательную таблицу,
    // ибо все описано в поле nomiation в Vote
    // orphanRemoval = true - удаляется из списка и базы
    private List<Vote> votes = new ArrayList<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nomination)) return false;
        Nomination that = (Nomination) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}