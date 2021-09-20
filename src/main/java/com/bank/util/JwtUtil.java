package com.bank.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.bank.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

	// Secure Secret Key
	private String SECRET_KEY = "tYrQ@058!";

	public String extractUserName(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Extract the claims.
	 *
	 * @param <T>            the generic type
	 * @param token          the token
	 * @param claimsResolver the claims resolver
	 * @return the t
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String generateToken(Authentication authentation) {
		User user = (User) authentation.getPrincipal();
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, user.getUsername());
	}
	
	public boolean validateToken(String token , UserDetails userDetails) {
		final String userName = extractUserName(token);
		return (userName.equals(userDetails.getUsername()) 
				&& !isTokenExpired(token));
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private String createToken(Map<String, Object> claims, String subject) {
		Calendar issuedDate = Calendar.getInstance();
		Calendar expiredDate = Calendar.getInstance();
		expiredDate.setTime(issuedDate.getTime());
		expiredDate.add(Calendar.HOUR, 2);
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(issuedDate.getTime())
				.setExpiration(expiredDate.getTime()).signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
	}

}
