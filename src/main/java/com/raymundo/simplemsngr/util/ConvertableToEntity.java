package com.raymundo.simplemsngr.util;

public interface ConvertableToEntity<E extends BaseEntity> {

    E toEntity();
}
