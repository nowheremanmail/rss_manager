/**
 * © Copyright Atos Origin, 2016. All rights reserved
 */
package com.dag.news;

import org.springframework.boot.ExitCodeGenerator;

public class ExitException extends RuntimeException implements ExitCodeGenerator {
	
	
	@Override
	public int getExitCode() {
		return 10;
	}
}