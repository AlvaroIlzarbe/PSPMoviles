import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import Config.ThymeleafConstants;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/puntuaciones")
public class ServletSegundo extends HttpServlet {

    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        this.templateEngine = (TemplateEngine) getServletContext()
                .getAttribute(ThymeleafConstants.TEAMPLATE_KEY_BUSQUEDA);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/pantallainicial.html");
            return;
        }

        List<String> puntuaciones = (List<String>) getServletContext().getAttribute("puntuaciones");

        if (puntuaciones == null) {
            puntuaciones = new ArrayList<>();
        }

        resp.setContentType(ThymeleafConstants.CONTENT_TYPE);

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(getServletContext());
        IWebExchange webExchange = application.buildExchange(req, resp);
        WebContext context = new WebContext(webExchange);

        context.setVariable("puntuaciones", puntuaciones);
        context.setVariable("usuario", session.getAttribute("user"));

        templateEngine.process("puntuacionJugadores", context, resp.getWriter());
    }
}

