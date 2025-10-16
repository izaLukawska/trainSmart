package org.lukawska.trainsmart.statements.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

}
