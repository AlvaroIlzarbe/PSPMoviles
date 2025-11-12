package Config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

@WebListener
public class ThymeleafInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent contx){
        ServletContext servletContext = contx.getServletContext();
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);

        WebApplicationTemplateResolver resolutor = new WebApplicationTemplateResolver(application);
        resolutor.setPrefix(UrlConstants.TEMPLATE_LOCALIZACION);
        resolutor.setSuffix(UrlConstants.TEMPLATE_TIPO_ARCHIVO);
        resolutor.setTemplateMode(ThymeleafConstants.TEAMPLATE_MODE);
        resolutor.setCharacterEncoding(ThymeleafConstants.CHARACTER_ENCODING);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolutor);

        servletContext.setAttribute(ThymeleafConstants.TEAMPLATE_KEY_BUSQUEDA,templateEngine);
    }
}
