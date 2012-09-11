/**
 * Copyright 2012 Kamran Zafar 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 */
package org.kamranzafar.auth.rs;

import java.util.logging.Logger;

import javax.naming.NamingSecurityException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Kamran Zafar
 * 
 */
@Provider
public class AuthExceptionMapper implements ExceptionMapper<AuthException> {
	private static Logger logger = Logger.getLogger(AuthExceptionMapper.class.getName());

	@Override
	public Response toResponse(AuthException e) {
		logger.fine("Error authenticating user: " + e.getMessage());
		return Response.status(getStatus(e.getCause()))
				.entity("{\"status\":\"ERROR\", \"errorMessage\":\"" + e.getMessage() + "\"}").build();
	}

	private Status getStatus(Throwable e) {
		if (e == null) {
			return Status.BAD_REQUEST;
		} else if (e instanceof NamingSecurityException) {
			return Status.UNAUTHORIZED;
		}

		return Status.INTERNAL_SERVER_ERROR;
	}
}
