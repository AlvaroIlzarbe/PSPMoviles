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
import java.util.Collections;
import java.util.List;
import java.util.Random;

@WebServlet("/menu")
public class ServletPrimero extends HttpServlet {

    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        this.templateEngine = (TemplateEngine) getServletContext()
                .getAttribute(ThymeleafConstants.TEAMPLATE_KEY_BUSQUEDA);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/pantallainicial.html");
            return;
        }

        String view = req.getParameter("view");
        resp.setContentType(ThymeleafConstants.CONTENT_TYPE);

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(getServletContext());
        IWebExchange webExchange = application.buildExchange(req, resp);
        WebContext context = new WebContext(webExchange);

        if ("juego".equals(view)) {
            templateEngine.process("pantallaJuego", context, resp.getWriter());
        } else if ("resultado".equals(view)) {
            String resultado = (String) session.getAttribute("resultado");
            context.setVariable("resultado", resultado != null ? resultado : "Sin resultado");
            templateEngine.process("pantallaFinalJuego", context, resp.getWriter());
        } else {
            String usuario = (String) session.getAttribute("user");
            context.setVariable("usuario", usuario);
            templateEngine.process("pantallaMenu", context, resp.getWriter());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null) {
            procesarLogin(req, resp);
            return;
        }

        HttpSession session = req.getSession(false);

        if ("logout".equals(action)) {
            if (session != null) session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/pantallainicial.html");
            return;
        }

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/pantallainicial.html");
            return;
        }

        if ("iniciarJuego".equals(action)) {
            int numeroSecreto = new Random().nextInt(100) + 1;
            session.setAttribute("numeroSecreto", numeroSecreto);
            session.setAttribute("intentos", 0);
            session.removeAttribute("resultado");
            session.removeAttribute("pista");
            session.removeAttribute("colorPista");
            session.removeAttribute("intentosRestantes");
            session.removeAttribute("puntos");
            resp.sendRedirect(req.getContextPath() + "/menu?view=juego");
            return;
        }

        if ("adivinar".equals(action)) {
            procesarIntento(req, resp, session);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/menu");
    }

    private void procesarLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String usuario = req.getParameter("usuario");
        String password = req.getParameter("password");

        if (usuario != null && !usuario.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
            HttpSession session = req.getSession(true);
            session.setAttribute("user", usuario.trim());
            resp.sendRedirect(req.getContextPath() + "/menu");
        } else {
            resp.sendRedirect(req.getContextPath() + "/pantallainicial.html?error=true");
        }
    }

    private void procesarIntento(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        Object secretObj = session.getAttribute("numeroSecreto");
        if (secretObj == null) {
            resp.sendRedirect(req.getContextPath() + "/menu");
            return;
        }

        int numeroSecreto = (int) secretObj;
        int intentos = session.getAttribute("intentos") != null ?
                (int) session.getAttribute("intentos") : 0;
        final int MAX_INTENTOS = 10;

        String numeroStr = req.getParameter("numero");
        try {
            int numero = Integer.parseInt(numeroStr);
            intentos++;
            session.setAttribute("intentos", intentos);

            if (numero == numeroSecreto) {
                int puntos = Math.max(0, 10 - intentos + 1);
                String resultado = "¡GANASTE en " + intentos + " intentos! Puntos obtenidos: " + puntos;
                session.setAttribute("resultado", resultado);
                session.setAttribute("puntos", puntos);
                addPuntuacion(session, puntos);
                session.removeAttribute("numeroSecreto");
                session.removeAttribute("pista");
                resp.sendRedirect(req.getContextPath() + "/menu?view=resultado");
            } else if (intentos >= MAX_INTENTOS) {
                String resultado = "¡PERDISTE! Se acabaron los intentos. El número era " + numeroSecreto + ". Puntos: 0";
                session.setAttribute("resultado", resultado);
                session.setAttribute("puntos", 0);
                addPuntuacion(session, 0);
                session.removeAttribute("numeroSecreto");
                session.removeAttribute("pista");
                resp.sendRedirect(req.getContextPath() + "/menu?view=resultado");
            } else {
                String pista;
                String colorPista;
                if (numero < numeroSecreto) {
                    pista = "El número es SUPERIOR";
                    colorPista = "green";
                } else {
                    pista = "El número es INFERIOR";
                    colorPista = "red";
                }
                session.setAttribute("pista", pista);
                session.setAttribute("colorPista", colorPista);
                session.setAttribute("intentosRestantes", MAX_INTENTOS - intentos);
                resp.sendRedirect(req.getContextPath() + "/menu?view=juego");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("pista", "Por favor, introduce un número válido");
            session.setAttribute("colorPista", "orange");
            resp.sendRedirect(req.getContextPath() + "/menu?view=juego");
        }
    }

    private void addPuntuacion(HttpSession session, int puntos) {
        List<String> lista = (List<String>) getServletContext().getAttribute("puntuaciones");
        if (lista == null) {
            List<String> nueva = Collections.synchronizedList(new ArrayList<>());
            getServletContext().setAttribute("puntuaciones", nueva);
            lista = nueva;
        }
        String usuario = (String) session.getAttribute("user");
        lista.add(usuario + " ..... " + puntos + " puntos");
    }
}

