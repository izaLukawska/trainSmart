package org.lukawska.trainsmart.statements.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

	@Setter
	private int statementVersion;

	@Enumerated(EnumType.STRING)
	@Setter
	private AgreementStatus status;

	public UserAgreement(Long userId, String statementCode, int statementVersion) {
		this.userId = userId;
		this.statementCode = statementCode;
		this.statementVersion = statementVersion;
	}

	public UserAgreement(Long userId, String statementCode, int statementVersion, AgreementStatus status) {
		this.userId = userId;
		this.statementCode = statementCode;
		this.statementVersion = statementVersion;
		this.status = status;
	}
}
