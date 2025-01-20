package com.example.demo.config;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demo.User.MyUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Autowired
    ApplicationContext context;

    private String secretKey;

    public JwtService(){
        secretKey = generateSecretKey();
    }

    private String generateSecretKey(){
        try{
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGen.generateKey();
            System.out.println(secretKey.toString());
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch(NoSuchAlgorithmException e){
            throw new RuntimeException("Error generating secret key", e);
        }
    }

    // Example in AuthenticationService
    public String createToken(String username) {
        UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
        return generateToken(username, authorities);
    }

    
    public String generateToken(String username,List<GrantedAuthority> authorities){

        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", authorities.stream()
                .map(GrantedAuthority::getAuthority)  // Extracting role names
                .collect(Collectors.toList()));

        return Jwts.builder()
            .setClaims(claims).setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000*60*10))
            .signWith(getKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @SuppressWarnings("unchecked")
    public List<GrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        List<String> roles = claims.get("roles", List.class);  // Assuming roles are in the "roles" claim

        return roles.stream()
                    .map(SimpleGrantedAuthority::new)  // Add ROLE_ prefix
                    .collect(Collectors.toList());

    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getKey()).build()
            .parseClaimsJws(token).getBody();
    }
        
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
        
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
        
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
