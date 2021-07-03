package com.binghe.subway.repository;

import com.binghe.subway.domain.Station;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {

    Station findByName(String name);
}
