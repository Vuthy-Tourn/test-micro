package kh.edu.istad.stadoor.consumer.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("consumer_role_mappings")
@Getter
@Setter
@NoArgsConstructor
public class ConsumerRoleMappingEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("consumer_id")
    private UUID consumerId;

    @Column("role_id")
    private UUID roleId;

    @Column("assigned_at")
    private Instant assignedAt;

    @Column("assigned_by_user_id")
    private UUID assignedByUserId;
}
