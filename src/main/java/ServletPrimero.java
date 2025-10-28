// Implementación del servlet principal (pantalla uno / menú / juego)
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@WebServlet("/menu")
public class ServletPrimero extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/pantallainicial.html");
            return;
        }

        String view = req.getParameter("view");
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        if ("juego".equals(view)) {
            renderJuego(out, session);
        } else if ("resultado".equals(view)) {
            renderResultado(out, session);
        } else {
            renderMenu(out, session);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        // Si action es null, venimos del formulario de login (pantallainicial.html)
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
            // Sesión inválida -> volver al login
            resp.sendRedirect(req.getContextPath() + "/pantallainicial.html");
            return;
        }

        if ("iniciarJuego".equals(action)) {
            int numeroSecreto = new Random().nextInt(100) + 1; // 1-100
            session.setAttribute("numeroSecreto", numeroSecreto);
            session.setAttribute("intentos", 0);
            session.removeAttribute("resultado");
            resp.sendRedirect(req.getContextPath() + "/menu?view=juego");
            return;
        }

        if ("adivinar".equals(action)) {
            procesarIntento(req, resp, session);
            return;
        }

        // Acción no reconocida -> volver al menú
        resp.sendRedirect(req.getContextPath() + "/menu");
    }

    private void procesarLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String usuario = req.getParameter("usuario");
        String password = req.getParameter("password");

        // Validación simple: ambos campos no vacíos
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
            // No hay juego iniciado
            resp.sendRedirect(req.getContextPath() + "/menu");
            return;
        }

        int numeroSecreto = (int) secretObj;
        int intentos = session.getAttribute("intentos") != null ? (int) session.getAttribute("intentos") : 0;

        String numeroStr = req.getParameter("numero");
        String resultado;
        try {
            int numero = Integer.parseInt(numeroStr);
            intentos++;
            session.setAttribute("intentos", intentos);

            if (numero == numeroSecreto) {
                resultado = "GANASTE en " + intentos + " intentos!";
                // Guardar puntuación en el contexto de aplicación
                addPuntuacion(session, intentos);
            } else {
                resultado = "PERDISTE. El número era " + numeroSecreto + ". Has hecho " + intentos + " intentos.";
            }

            session.setAttribute("resultado", resultado);
            // Limpiar número secreto para forzar reinicio si quiere volver a jugar
            session.removeAttribute("numeroSecreto");
        } catch (NumberFormatException e) {
            session.setAttribute("resultado", "Entrada inválida. Introduce un número entero.");
        }

        resp.sendRedirect(req.getContextPath() + "/menu?view=resultado");
    }

    private void addPuntuacion(HttpSession session, int intentos) {
        // Estructura compartida en el ServletContext para todas las puntuaciones
        List<String> lista = (List<String>) getServletContext().getAttribute("puntuaciones");
        if (lista == null) {
            List<String> nueva = Collections.synchronizedList(new ArrayList<>());
            getServletContext().setAttribute("puntuaciones", nueva);
            lista = nueva;
        }
        String usuario = (String) session.getAttribute("user");
        lista.add(usuario + " - " + intentos + " intentos");
    }

    private void renderMenu(PrintWriter out, HttpSession session) {
        out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Menú</title></head><body>");
        out.println("<h1>Bienvenido, " + escapeHtml((String) session.getAttribute("user")) + "</h1>");

        // Botón iniciar juego (POST)
        out.println("<form method='post' action='menu'>");
        out.println("<button name='action' value='iniciarJuego' type='submit'>Iniciar Juego</button>");
        out.println("</form><br>");

        // Botón ver puntuaciones (GET a /puntuaciones)
        out.println("<form method='get' action='puntuaciones'>");
        out.println("<button type='submit'>Ver Puntuaciones</button>");
        out.println("</form><br>");

        // Botón salir (logout)
        out.println("<form method='post' action='menu'>");
        out.println("<button name='action' value='logout' type='submit'>Salir / Cerrar sesión</button>");
        out.println("</form>");

        out.println("</body></html>");
    }

    private void renderJuego(PrintWriter out, HttpSession session) {
        out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Juego - Adivina el número</title></head><body>");
        out.println("<h1>Juego: Adivina el número</h1>");
        out.println("<p>Introduce un número entre 1 y 100:</p>");
        out.println("<form method='post' action='menu'>");
        out.println("<input type='number' name='numero' min='1' max='100' required>");
        out.println("<button name='action' value='adivinar' type='submit'>Adivinar</button>");
        out.println("</form>");
        out.println("<br><a href='menu'>Volver al Menú</a>");
        out.println("</body></html>");
    }

    private void renderResultado(PrintWriter out, HttpSession session) {
        String resultado = (String) session.getAttribute("resultado");
        out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Resultado</title></head><body>");
        out.println("<h1>Resultado</h1>");
        out.println("<p>" + escapeHtml(resultado) + "</p>");
        out.println("<a href='menu'>Volver al Menú</a>");
        out.println("</body></html>");
    }

    // Very small helper to avoid simple XSS from usernames/result strings
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#x27;");
    }
}
