package co.assets.manage.enums.api;

import java.io.Serializable;

public interface IEnum extends Serializable {

    Integer getCode();

    String getName();

    default boolean equals(Integer code) {
        return this.getCode().equals(code);
    }

    default boolean equals(Byte code) {
        return this.getCode().equals(code.intValue());
    }

    default boolean equals(String code) {
        return this.getName().equals(code);
    }

}
