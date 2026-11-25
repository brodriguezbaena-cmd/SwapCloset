package es.safareyes.swapcloset.service;

import es.safareyes.swapcloset.dtos.request.LoginRequest;
import es.safareyes.swapcloset.dtos.request.RegistroRequest;
import es.safareyes.swapcloset.dtos.response.AuthResponse;

public interface AuthService {

    AuthResponse registrar(RegistroRequest request);

    AuthResponse login(LoginRequest request);
}
