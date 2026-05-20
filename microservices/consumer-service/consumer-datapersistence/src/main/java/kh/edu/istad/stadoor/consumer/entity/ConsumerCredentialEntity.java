package kh.edu.istad.stadoor.consumer.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("consumer_credentials")
@Getter
@Setter
@NoArgsConstructor
public class ConsumerCredentialEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("consumer_id")
    private UUID consumerId;

    @Column("tenant_id")
    private UUID tenantId;

    @Column("credential_type")
    private String credentialType;

    @Column("status")
    private boolean status;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
