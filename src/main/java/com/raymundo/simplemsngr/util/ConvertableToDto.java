package com.raymundo.simplemsngr.util;

public interface ConvertableToDto<D extends BaseDto> {

    D toDto();
}
