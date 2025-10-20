package org.lukawska.trainsmart.statements.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;

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
		if (newStatus == null) {
			throw new StatementException(ExceptionType.INVALID_STATUS);
		}

		this.status = newStatus;
	}
}
