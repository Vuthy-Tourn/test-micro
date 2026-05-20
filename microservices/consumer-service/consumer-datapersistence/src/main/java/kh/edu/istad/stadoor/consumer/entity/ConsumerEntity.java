package kh.edu.istad.stadoor.consumer.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("consumers")
@Getter
@Setter
@NoArgsConstructor
public class ConsumerEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("tenant_id")
    private UUID tenantId;

    @Column("gateway_id")
    private UUID gatewayId;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("type")
    private String type;

    @Column("auth_type")
    private String authType;

    @Column("status")
    private String status;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
