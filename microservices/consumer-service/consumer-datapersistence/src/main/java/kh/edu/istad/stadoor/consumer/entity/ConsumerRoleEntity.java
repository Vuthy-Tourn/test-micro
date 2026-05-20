package kh.edu.istad.stadoor.consumer.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("consumer_roles")
@Getter
@Setter
@NoArgsConstructor
public class ConsumerRoleEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("tenant_id")
    private UUID tenantId;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("status")
    private Boolean status;

    @Column("created_at")
    private Instant createdAt;
}
