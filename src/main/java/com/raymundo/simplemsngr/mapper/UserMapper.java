package com.raymundo.simplemsngr.mapper;

import com.raymundo.simplemsngr.dto.UserDto;
import com.raymundo.simplemsngr.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto toDto(UserEntity userEntity);

    UserEntity toEntity(UserDto userDto);

}
