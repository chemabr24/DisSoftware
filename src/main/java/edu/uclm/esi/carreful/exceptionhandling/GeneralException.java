package edu.uclm.esi.carreful.exceptionhandling;

import org.springframework.core.NestedRuntimeException;

public class GeneralException extends NestedRuntimeException {

	  private static final long serialVersionUID = 1L;

	  /**
	   * Este método muestra el mensaje de excepción de usuario no encontrado.
	   */
	  public GeneralException(String msg) {

	    super(msg);

	  }

}
