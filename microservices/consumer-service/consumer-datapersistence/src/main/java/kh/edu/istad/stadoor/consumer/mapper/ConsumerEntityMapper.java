package kh.edu.istad.stadoor.consumer.mapper;

import kh.edu.istad.stadoor.consumer.dto.response.ConsumerResponse;
import kh.edu.istad.stadoor.consumer.entity.ConsumerEntity;
import kh.edu.istad.stadoor.consumer.valueobject.consumer.ConsumerStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsumerEntityMapper {

    @Mapping(target = "consumerId", source = "id")
    @Mapping(target = "consumerType", source = "type")
    ConsumerResponse toResponse(ConsumerEntity entity);

    default ConsumerStatus mapStatus(String status) {
        if (status == null) return ConsumerStatus.ACTIVE;
        return ConsumerStatus.valueOf(status);
    }
}
