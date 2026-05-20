package kh.edu.istad.stadoor.consumer.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("jwt_credentials")
@Getter
@Setter
@NoArgsConstructor
public class JwtCredentialEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("consumer_credential_id")
    private UUID consumerCredentialId;

    @Column("gateway_id")
    private UUID gatewayId;

    @Column("username")
    private String username;

    @Column("password_hash")
    private String passwordHash;

    @Column("secret_key")
    private String secretKey;

    @Column("algorithm")
    private String algorithm;

    @Column("access_token_ttl")
    private int accessTokenTtl;

    @Column("refresh_token_ttl")
    private int refreshTokenTtl;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
