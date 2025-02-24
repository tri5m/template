package com.example.template.common.data;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @title: TriplesKeyValue
 * @author: trifolium
 * @date: 2021/12/27
 * @modified :
 */
@Setter
@Getter
public class TriplesKeyValue<K, V1, V2> extends KeyValue<K, V1> {

    private V2 value2;

    public TriplesKeyValue(K k, V1 v, V2 v2) {
        super(k, v);
        this.value2 = v2;
    }

    public TriplesKeyValue() {
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || super.equals(obj)) {
            return false;
        }

        if (obj instanceof TriplesKeyValue<?, ?, ?> o) {
            return Objects.equals(this.getValue2(), o.getValue2());
        }

        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getKey(), this.getValue(), this.getValue2());
    }

}
