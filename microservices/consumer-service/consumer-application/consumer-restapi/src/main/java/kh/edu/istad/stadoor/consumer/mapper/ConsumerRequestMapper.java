package kh.edu.istad.stadoor.consumer.mapper;

import kh.edu.istad.stadoor.consumer.dto.command.RegisterConsumerInput;
import kh.edu.istad.stadoor.consumer.dto.request.AssignRoleInput;
import kh.edu.istad.stadoor.consumer.dto.request.AssignRoleRequest;
import kh.edu.istad.stadoor.consumer.dto.request.CreateRoleInput;
import kh.edu.istad.stadoor.consumer.dto.request.CreateRoleRequest;
import kh.edu.istad.stadoor.consumer.dto.request.LoginInput;
import kh.edu.istad.stadoor.consumer.dto.request.LoginRequest;
import kh.edu.istad.stadoor.consumer.dto.request.LogoutInput;
import kh.edu.istad.stadoor.consumer.dto.request.LogoutRequest;
import kh.edu.istad.stadoor.consumer.dto.request.RefreshInput;
import kh.edu.istad.stadoor.consumer.dto.request.RefreshRequest;
import kh.edu.istad.stadoor.consumer.dto.request.RegisterConsumerRequest;
import kh.edu.istad.stadoor.consumer.dto.request.UpdateRoleInput;
import kh.edu.istad.stadoor.consumer.dto.request.UpdateRoleRequest;
import kh.edu.istad.stadoor.consumer.dto.request.ValidateCredentialInput;
import kh.edu.istad.stadoor.consumer.dto.request.ValidateCredentialRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsumerRequestMapper {

    RegisterConsumerInput toInput(RegisterConsumerRequest request);

    ValidateCredentialInput toInput(ValidateCredentialRequest request);

    LoginInput toInput(LoginRequest request);

    RefreshInput toInput(RefreshRequest request);

    LogoutInput toInput(LogoutRequest request);

    CreateRoleInput toInput(CreateRoleRequest request);

    UpdateRoleInput toInput(UpdateRoleRequest request);

    AssignRoleInput toInput(AssignRoleRequest request);
}
