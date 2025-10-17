package org.lukawska.trainsmart.statements.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;

import java.util.Objects;

@Entity
@Table(name = "user_agreements")
@Getter
@NoArgsConstructor
public class UserAgreement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private String statementCode;

	private int statementVersion;

	@Enumerated(EnumType.STRING)
	private AgreementStatus status;

	public UserAgreement(Long userId, String statementCode, AgreementStatus status) {
		this.userId = userId;
		this.statementCode = statementCode;
		this.status = status;
	}

	public void updateStatementVersion(int version) {
		this.statementVersion = version;
	}

	public void changeStatus(AgreementStatus newStatus) {
		if (Objects.isNull(newStatus)) {
			throw new IllegalArgumentException("Status cannot be null");
		}
		if (this.status == newStatus) {
			return;
		}
		this.status = newStatus;
	}
}
