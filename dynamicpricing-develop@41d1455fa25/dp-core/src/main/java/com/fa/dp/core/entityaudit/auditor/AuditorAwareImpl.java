/**
 * 
 */
package com.fa.dp.core.entityaudit.auditor;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

public class AuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		// TODO identify current user
		return Optional.of("SYSTEM");
	}

}
