package ar.edu.utn.frba.dds.front_end_app.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
public class RefreshTokenDTO{
  @Getter
  private String refreshToken;
}
