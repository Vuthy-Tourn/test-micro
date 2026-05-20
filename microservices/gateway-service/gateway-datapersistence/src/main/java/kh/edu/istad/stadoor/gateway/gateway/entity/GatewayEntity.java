package kh.edu.istad.stadoor.gateway.gateway.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("gateways")
@Getter
@Setter
@NoArgsConstructor
public class GatewayEntity implements Persistable<UUID> {

    @Id
    @Column("gateway_id")
    private UUID gatewayId;

    @Column("tenant_id")
    private UUID tenantId;

    @Column("gateway_name")
    private String gatewayName;

    @Column("description")
    private String description;

    @Column("status")
    private String status;

    @Column("auth_type")
    private String authType;

    @Column("gateway_type")
    private String gatewayType;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Transient
    private boolean isNew = false;

    @Override
    public UUID getId() {
        return gatewayId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNew() {
        this.isNew = true;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
