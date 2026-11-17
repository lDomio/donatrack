package ar.edu.utn.frba.dds.front_end_app.config;

import ar.edu.utn.frba.dds.front_end_app.interceptors.VerificarIdDonanteInterceptor;
import ar.edu.utn.frba.dds.front_end_app.interceptors.VerificarIdEntidadInterceptor;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final VerificarIdEntidadInterceptor verificarIdEntidadInterceptor;
  private final VerificarIdDonanteInterceptor verificarIdDonanteInterceptor;
  private final String uploadsDir;

  public WebMvcConfig(VerificarIdEntidadInterceptor verificarIdEntidadInterceptor,
                      VerificarIdDonanteInterceptor verificarIdDonanteInterceptor,
                      @Value("${app.uploads.dir:uploads}") String uploadsDir) {
    this.verificarIdEntidadInterceptor = verificarIdEntidadInterceptor;
    this.verificarIdDonanteInterceptor = verificarIdDonanteInterceptor;
    this.uploadsDir = uploadsDir;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String directorioUploads = Path.of(uploadsDir).toAbsolutePath().normalize().toUri().toString();
    registry.addResourceHandler("/uploads/**")
        .addResourceLocations(directorioUploads.endsWith("/") ? directorioUploads : directorioUploads + "/");
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry){
    registry.addInterceptor(verificarIdEntidadInterceptor)
        .addPathPatterns("/entidadBeneficiaria/**");
    registry.addInterceptor(verificarIdDonanteInterceptor)
        .addPathPatterns("/personaDonante/**");
  }
}
