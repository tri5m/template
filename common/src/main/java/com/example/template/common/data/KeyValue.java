package com.example.template.common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @author trifolium
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeyValue<K, V> {

    private K key;

    private V value;

    @Override
    public String toString() {
        return this.key + ":" + this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof KeyValue<?, ?> o) {
            return Objects.equals(this.key, o.getKey())
                    && Objects.equals(this.value, o.getValue());
        }

        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getKey(), this.getValue());
    }

}
