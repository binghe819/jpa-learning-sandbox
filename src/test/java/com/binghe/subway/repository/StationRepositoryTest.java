package com.binghe.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.binghe.subway.domain.Station;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class StationRepositoryTest {

    @Autowired
    private StationRepository stations;

    @Test
    void save() {
        Station station = new Station("잠실역");
        Station actual = stations.save(station);
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("잠실역");
    }

    @Test
    void findByName() {
        stations.save(new Station("잠실역"));
        Station actual = stations.findByName("잠실역");
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("잠실역");
    }

    @DisplayName("영속성 컨텍스트를 이용해서 동등성과 동일성을 지원한다.")
    @Test
    void equals() {
        Station station1 = stations.save(new Station("잠실역"));
        Station station2 = stations.findByName("잠실역");
        assertThat(station1.getId()).isNotNull();
        assertThat(station2.getId()).isNotNull();
        assertThat(station1).isEqualTo(station2);
        assertThat(station1).isSameAs(station2);
    }
}
