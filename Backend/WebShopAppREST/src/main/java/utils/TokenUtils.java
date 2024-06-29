package utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenUtils {

	private static final String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
	private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

	public String generateToken(String username, String role) {
		String jwtToken = Jwts.builder()
		        .claim("role", role)
		        .claim("email", username)
		        .setSubject("jane")
		        .setId(UUID.randomUUID().toString())
		        .setIssuedAt(Date.from(Instant.now()))
		        .setExpiration(Date.from(Instant.now().plus(5l, ChronoUnit.MINUTES)))
		        .signWith(SIGNATURE_ALGORITHM, secret)
		        .compact();
		return jwtToken;
    }
	
	public String getToken(HttpServletRequest request) {
		String authHeader = getAuthHeader(request);
		if(authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		return null;
	}
	
	private String getAuthHeader(HttpServletRequest request) {
		String header = "Authorization";
		return request.getHeader(header);
	}

	public Claims parseToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser()
					.setSigningKey(secret)
					.parseClaimsJws(token)
					.getBody();
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}
	
	
}
