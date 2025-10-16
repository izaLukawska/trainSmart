package org.lukawska.trainsmart.statements.application.services;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.statements.domain.UserAgreementRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAgreementService {

	private final UserAgreementRepository repository;

}
