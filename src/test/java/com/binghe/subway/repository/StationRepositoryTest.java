package com.binghe.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.binghe.subway.domain.Station;
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

    @DisplayName("영속성 컨텍스트를 이용하면 1차 캐싱을 이용하여 동일성을 지원한다.")
    @Test
    void equalsAndSame() {
        Station station1 = stations.save(new Station("잠실역"));
        Station station2 = stations.findByName("잠실역");
        assertThat(station1.getId()).isNotNull();
        assertThat(station2.getId()).isNotNull();
        assertThat(station1).isEqualTo(station2);
        assertThat(station1).isSameAs(station2);
    }

    @DisplayName("영속성 컨텍스트의 더티체킹을 이용하여 변경을 감지하여 자동으로 UPDATE 쿼리를 날린다. -> 테스트 필요")
    @Test
    void update() {
        Station station = stations.save(new Station("잠실역"));
        station.changeName("몽촌토성역");

        Station actual = stations.findByName("몽촌토성역");
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("몽촌토성역");
    }

    @DisplayName("1차 캐싱을 이용한 테스트 -> 토론 필요 (로깅에 나오는 것이 정말 쿼리를 날린다는 것인가?)")
    @Test
    void cache() {
        Station station = stations.save(new Station("잠실역"));
        Station findStation1 = stations.findByName("잠실역");
        Station findStation2 = stations.findByName("잠실역");

        assertThat(station).isSameAs(findStation1);
        assertThat(findStation1).isSameAs(findStation2);
    }
}
