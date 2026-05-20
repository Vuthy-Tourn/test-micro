package kh.edu.istad.stadoor.consumer.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("api_key_credentials")
@Getter
@Setter
@NoArgsConstructor
public class ApiKeyCredentialEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("consumer_credential_id")
    private UUID consumerCredentialId;

    @Column("api_key")
    private String apiKey;

    @Column("key_hash")
    private String keyHash;

    @Column("created_at")
    private Instant createdAt;
}
