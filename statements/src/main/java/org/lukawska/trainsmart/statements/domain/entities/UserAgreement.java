package org.lukawska.trainsmart.statements.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;

@Entity
@Table(name = "user_agreements")
@Getter
@NoArgsConstructor
public class UserAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String statementCode;

    private int statementVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgreementStatus status;

    public UserAgreement(User user, String statementCode, int statementVersion, AgreementStatus status) {
        this.user = user;
        this.statementCode = statementCode;
        this.statementVersion = statementVersion;
        this.status = status;
    }

    public void updateStatementVersion(int version) {
        this.statementVersion = version;
    }

    public void changeStatus(AgreementStatus newStatus) {
        this.status = newStatus;
    }
}
