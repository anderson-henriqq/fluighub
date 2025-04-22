package com.fluig.service;


import com.fluig.model.GenericModel;
import com.fluig.model.ResponseGeneralModel;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.Key;

public class CheckTokenService {
    private static final String secretKey = "WgATDRqRDQkLqVxWbOgrVBqTVAbKbUWFUkhNCkfOmGMUePLYnbctTN";
    Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

    public Response executeService(GenericModel request) throws IOException {
        return validateToken(request.getString());
    }

    private Response validateToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new BadRequestException("Token is null");
        }
        try {
            Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();

            String login = claims.getSubject();
            ResponseGeneralModel response = new ResponseGeneralModel(login, false, 200);
            return Response.status(response.getCode()).entity(new Gson().toJson(response)).build();
        } catch (ExpiredJwtException e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).entity("Token expirado").build();
        } catch (SignatureException e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).entity("assinatura inválida").build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).entity("Token inválido").build();
        }
    }
}
