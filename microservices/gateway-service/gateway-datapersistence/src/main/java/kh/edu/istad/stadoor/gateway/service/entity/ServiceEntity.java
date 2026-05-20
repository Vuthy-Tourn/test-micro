package kh.edu.istad.stadoor.gateway.service.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("services")
@Getter
@Setter
@NoArgsConstructor
public class ServiceEntity implements Persistable<UUID> {

    @Id
    @Column("service_id")
    private UUID serviceId;

    @Column("gateway_id")
    private UUID gatewayId;

    @Column("name")
    private String name;

    @Column("service_type")
    private String serviceType;

    @Column("status")
    private String status;

    @Column("base_url")
    private String baseUrl;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;


//
    @Override
    public UUID getId() {
        return serviceId;
    }

    @Transient
    private boolean isNew;

    @Override
    public boolean isNew() {
        return isNew;
    }

}