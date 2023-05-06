package com.binghe;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "A") // 테이블에 DTYPE이 Album을 A라고 저장한다.
public class Album extends Item {

    private String artist;
}
